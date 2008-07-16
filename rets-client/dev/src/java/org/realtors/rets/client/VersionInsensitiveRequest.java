package org.realtors.rets.client;

public abstract class VersionInsensitiveRequest extends RetsHttpRequest {
	public VersionInsensitiveRequest() {
		super();
	}

	@Override
	public void setVersion(RetsVersion version) {
		//noop - I don't care about version
	}
}
