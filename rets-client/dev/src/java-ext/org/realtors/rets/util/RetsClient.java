package org.realtors.rets.util;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.realtors.rets.client.CommonsHttpClient;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.RetsHttpClient;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MSystem;
import org.realtors.rets.util.transaction.metadata.MetadataRetsTransaction;


public class RetsClient {

    private final RetsProfile retsProfile;
    private final ObjectPool pool;
    
    public RetsClient(RetsProfile retsProfile) {
	    this.retsProfile = retsProfile;
		long sessionTimeout = retsProfile.getSessionTimeout();
		
		int sessionConcurrentLimit = retsProfile.getSessionConcurrentLimit();
	    // this pool manages concurrent access to a single connection
	    this.pool = new GenericObjectPool(new BasePoolableObjectFactory(){
			private RetsSession connection;
			@Override
			public synchronized Object makeObject() throws Exception {
				if (this.connection == null) this.connection = RetsClient.this.createSession();
				return this.connection;
			}
			@Override
			public synchronized void destroyObject(Object obj) throws Exception {
				this.connection = null;
			}
	    },sessionConcurrentLimit,GenericObjectPool.WHEN_EXHAUSTED_BLOCK,0,sessionConcurrentLimit,0,false,false, (sessionTimeout/2), sessionConcurrentLimit, sessionTimeout, false);
	}
    
    public RetsProfile getRetsProfile() {
		return this.retsProfile;
	}
    
    // ---------------------------------------------------------------------------------------------------------------------------
    // RETRIABLE METHOD OF THE RETSCLIENT 
    // ---------------------------------------------------------------------------------------------------------------------------
    public interface ResponseTouch<T>{
    	public void apply(T t) throws Exception;
    }
	/** note that ALL rets requests funnel back to here */
    public <T> T executeRetsTransaction(final RetsTransaction<T> transaction, final ResponseTouch<T>... onComplete) throws Exception {
		List<Exception> attempts = new LinkedList<Exception>();
    	do{
    		RetsSession session = null;
			try{
				session = (RetsSession) this.pool.borrowObject();
				T result = transaction.execute(session);
				if( onComplete != null ) for( ResponseTouch<T> notify : onComplete) notify.apply(result);
				this.pool.returnObject(session);
				return result;
			}catch(Exception e){
				this.pool.invalidateObject(session);
				attempts.add(e);
				if( attempts.size() > this.retsProfile.getRetryCount() ) throw e;
				if( Thread.currentThread().isInterrupted() ) throw new InterruptedException(String.format("%s interrupted",this));
				Logger.getLogger(RetsClient.class).warn(String.format("RetsClient [%s] error processing rets transaction attempt %s (delay of %s) caused by %s", this, attempts.size(), this.retsProfile.getRetryDelay(), e.getMessage()));
				Thread.sleep(this.retsProfile.getRetryDelay());
			}
		}while(attempts.size() <= this.retsProfile.getRetryCount());
    	throw attempts.get(0);
	}
	
	protected RetsSession createSession() throws RetsException {
		Logger.getLogger(RetsClient.class).debug(String.format("RetsClient [%s] creating RetsSession in thread '%s'", this, Thread.currentThread().getName()));
		/** override the doRequest method to allow partial use of posts (see nefmls) */
    	CommonsHttpClient retsClient = createClient();
    	// manage concurrent connections (use the retsprofile max concurrent connections if we arent using a pool to limit connections)
    	retsClient.getHttpClient().getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(Integer.MAX_VALUE);
    	retsClient.getHttpClient().getHttpConnectionManager().getParams().setMaxTotalConnections(Integer.MAX_VALUE);
    	// this is a timeout waiting for a free connection from _OUR_OWN_ connection manager (??? THIS REALLY SHOULDNT BE NECESSARY )
    	retsClient.getHttpClient().getParams().setConnectionManagerTimeout(TimeUnit.MINUTES.toMillis(10));
    	// this is the time to initial connection, this shouldnt be long
    	retsClient.getHttpClient().getHttpConnectionManager().getParams().setConnectionTimeout((int)TimeUnit.SECONDS.toMillis(10));
    	// if this is null, it doesnt matter anyways
    	retsClient.getHttpClient().getHostConfiguration().getParams().setVirtualHost(this.retsProfile.getHostHeaderOverride());

    	RetsCredentials credentials = this.retsProfile.getCredentials();
		// NOTE that this session is never technically loggin out
		RetsSession temp = this.createRetsSession(retsClient);
		temp.setMethod(this.retsProfile.getHttpMethodDefault());
		temp.login(credentials.getUsername(), credentials.getPassword());
	    // RETS (Table 4-2 see Login-URL) allows for server migration, should update whatever stores this 
		if( !StringUtils.equals(this.retsProfile.getLoginUrl(),temp.getLoginUrl()) ) {
			Logger.getLogger(RetsClient.class).warn(String.format("login url for %s is updated to %s", this.retsProfile.getName(), temp.getLoginUrl()));
			if( StringUtils.isNotEmpty(temp.getLoginUrl()) ) this.retsProfile.setLoginUrl(temp.getLoginUrl());
		}
		return temp;
	}

	protected CommonsHttpClient createClient() {
		return new CommonsHttpClient(this.retsProfile.getWaitTimeout(), this.retsProfile.getUserAgentPassword(),this.retsProfile.isGzipEnabled());
	}
	
	protected void destroySession(RetsSession session) {
		Logger.getLogger(RetsClient.class).debug(String.format("RetsClient [%s] destroying RetsSession in thread '%s'", this, Thread.currentThread().getName()));
		try {
			if (session != null) session.logout();
		} catch (Exception e) {
			// shut up!
		}
	}
	
	protected RetsSession createRetsSession(RetsHttpClient retsClient) {
		return new RetsSession(this.retsProfile.getLoginUrl(), retsClient, this.retsProfile.getRetsVersion(), this.retsProfile.getUserAgent(), this.retsProfile.isRetsStrict());
	}
	
    // ---------------------------------------------------------------------------------------------------------------------------
    // METADATA
    // ---------------------------------------------------------------------------------------------------------------------------
    /** help for small periods of time */
	private SoftReference<Metadata> metadata;
	private long lastMetadataTime = System.currentTimeMillis();
	public synchronized Metadata getMetadata() {
        long staleness = System.currentTimeMillis()-this.lastMetadataTime;
		if( this.metadata != null && this.metadata.get() != null && staleness < this.retsProfile.getMetadataTimeout() ) return this.metadata.get();
		
		try {
			this.metadata = new SoftReference<Metadata>(this.executeRetsTransaction(new MetadataRetsTransaction(this)));
			this.lastMetadataTime = System.currentTimeMillis();
		} catch (Exception e) {
			Logger.getLogger(RetsClient.class).error(String.format("RetsClient [%s] reading metadata in thread '%s', setting blank", this, Thread.currentThread().getName()),e);
			this.metadata = new SoftReference<Metadata>(new Metadata(new MSystem()));
			// dont try again for 5 minutes
			this.lastMetadataTime += TimeUnit.MINUTES.toMillis(5);
		}
		return this.metadata.get();
    }
	
	@Override
	public String toString() {
		return String.format("%s (%s)", this.getRetsProfile() != null ? this.getRetsProfile().getName() : "no_rets_profile_name", Integer.toHexString(this.hashCode()));
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.pool.close();
	}
}

