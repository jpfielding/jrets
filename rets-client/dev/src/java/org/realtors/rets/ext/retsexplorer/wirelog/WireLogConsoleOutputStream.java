package org.realtors.rets.ext.retsexplorer.wirelog;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;


/** OutputStream that appends to a WireLogConsole */
public class WireLogConsoleOutputStream extends OutputStream implements Closeable, Flushable {

	private int bufferLength = 1024*128;

	private int batchLength = 1024*64;
	
	private long batchInterval = 250;
	
	private TimeUnit batchIntervalTimeUnit = TimeUnit.MILLISECONDS;
	
	private volatile WireLogConsole console;
	
	private StringWriter pipe;
	
	private ScheduledThreadPoolExecutor flushExecutor;
	
	public WireLogConsoleOutputStream() {
		this(null);
	}
	
	public WireLogConsoleOutputStream(WireLogConsole console) {
		super();
		this.console = console;
		this.pipe = new StringWriter(this.bufferLength);

		final ThreadFactory threadFactory = new ThreadFactory(){
			AtomicInteger counter = new AtomicInteger();
			public Thread newThread(Runnable arg0) {
				Thread thread = new Thread(arg0, String.format("%s-%s",getClass().getSimpleName(), this.counter.incrementAndGet()));
				thread.setDaemon(true);
				return thread;
			}};
		this.flushExecutor = new ScheduledThreadPoolExecutor(1, threadFactory) {
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				boolean resurrect = !( (t==null) || isTerminating() || isShutdown() || isTerminated() || isClosed() ); 
				if (resurrect) {
					scheduleAtFixedRate(r, 0L, WireLogConsoleOutputStream.this.batchInterval, WireLogConsoleOutputStream.this.batchIntervalTimeUnit);
				}
			}
		};
		this.flushExecutor.setMaximumPoolSize(1);
		this.flushExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				doAutoFlush();
			}
		}, this.batchInterval, this.batchInterval, this.batchIntervalTimeUnit);
		
	}
	
	public WireLogConsole getConsole() {
		return this.console;
	}

	public void setConsole(WireLogConsole console) {
		this.console = console;
	}

	@Override
	public void write(int b) throws IOException {
		if (isPaused()) {
			return;
		}
		ensureOpen();
		String text = new String(new byte[]{(byte)(0xff & b)});
		StringBuffer buffer = this.pipe.getBuffer();
		if (buffer.length() >= buffer.capacity()) {
			flush();
			appendToConsole(text);
			return;
		}
		this.pipe.write(text);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (isPaused()) {
			return;
		}
		ensureOpen();
		if (b == null) {
			throw new NullPointerException();
		}
		String text = new String(b, off, len);
		StringBuffer buffer = this.pipe.getBuffer();
		if (len >= buffer.capacity()) {
		    flush();
		    appendToConsole(text);
		    return;
		}
		if (len > buffer.capacity()-buffer.length()) {
		    flush();
		}		
		this.pipe.write(text);
	}

	@Override
	public void close() {
		this.flushExecutor.shutdownNow();
		this.console = null;
		this.pipe.getBuffer().setLength(0);
		this.pipe = null;
	}
	
	private void appendToConsole(final String text) {
		if (this.console == null) {
			return;
		}
		if (SwingUtilities.isEventDispatchThread()) {
			this.console.appendText(text);
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WireLogConsoleOutputStream.this.console.appendText(text);
			}
		});
	}
	
	@Override
	public void flush() {
		if (isPaused()) {
			return;
		}
		StringBuffer buffer = this.pipe.getBuffer();
		synchronized (buffer) {
			appendToConsole(buffer.toString());
			buffer.setLength(0);
		}
	}
	
    private void ensureOpen() throws IOException {
    	if (isClosed()) {
    	    throw new IOException("Stream closed");
        }
    }
    
    private boolean isClosed() {
    	return (this.pipe == null);
    }

    private void doAutoFlush() {
		if (isPaused()) {
			return;
		}
		String text;
		StringBuffer buffer = WireLogConsoleOutputStream.this.pipe.getBuffer();
		synchronized(buffer) {
			if (buffer.length() <= WireLogConsoleOutputStream.this.batchLength) {
				flush();
				return;
			}
			text = buffer.substring(0, WireLogConsoleOutputStream.this.batchLength);
			buffer.delete(0, WireLogConsoleOutputStream.this.batchLength);
		}
		appendToConsole(text);
    }

	public boolean isPaused() {
		return this.console.isPaused();
	}

	public void setPaused(boolean paused) {
		this.console.setPaused(paused);
	}
    
    

}
