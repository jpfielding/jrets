package org.realtors.rets.retsexplorer.wirelog;

import java.io.OutputStream;
import java.util.Properties;

import org.realtors.rets.util.RetsClient;
import org.realtors.rets.util.RetsClientConfig;

public class WireLoggedRetsClientConfig extends RetsClientConfig {

	private final OutputStream requestBranch;
	private final OutputStream responseBranch;

	public WireLoggedRetsClientConfig(RetsClientConfig config, OutputStream requestBranch, OutputStream responseBranch) {
		this(config.getProperties(), config.getRetsServiceName(), requestBranch, responseBranch);
	}
	
	public WireLoggedRetsClientConfig(Properties properties, String retsServiceName, OutputStream requestBranch, OutputStream responseBranch) {
		super(properties, retsServiceName);
		this.requestBranch = requestBranch;
		this.responseBranch = responseBranch;
	}

	@Override
	public RetsClient createClient() {
		return new WireLoggedRetsClient(createRetsProfile(), this.requestBranch, this.responseBranch);
	}
}
