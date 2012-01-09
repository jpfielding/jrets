package org.realtors.rets.ext;

import org.realtors.rets.client.RetsSession;

public interface RetsTransaction<T> {
    public T execute(RetsSession session) throws Exception;
}
