package org.realtors.rets.retsexplorer.wirelog;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class TeeInputStream extends ProxyInputStream {

    /**
     * The output stream that will receive a copy of all bytes read from the
     * proxied input stream.
     */
    private final OutputStream branch;

    /**
     * Flag for closing also the associated output stream when this
     * stream is closed.
     */
    private final boolean closeBranch;

    /**
     * Creates a TeeInputStream that proxies the given {@link InputStream}
     * and copies all read bytes to the given {@link OutputStream}. The given
     * output stream will not be closed when this stream gets closed.
     *
     * @param input input stream to be proxied
     * @param branch output stream that will receive a copy of all bytes read
     */
    public TeeInputStream(InputStream input, OutputStream branch) {
        this(input, branch, false);
    }

    /**
     * Creates a TeeInputStream that proxies the given {@link InputStream}
     * and copies all read bytes to the given {@link OutputStream}. The given
     * output stream will be closed when this stream gets closed if the
     * closeBranch parameter is <code>true</code>.
     *
     * @param input input stream to be proxied
     * @param branch output stream that will receive a copy of all bytes read
     * @param closeBranch flag for closing also the output stream when this
     *                    stream is closed
     */
    public TeeInputStream(
            InputStream input, OutputStream branch, boolean closeBranch) {
        super(input);
        this.branch = branch;
        this.closeBranch = closeBranch;
    }

    /**
     * Closes the proxied input stream and, if so configured, the associated
     * output stream. An exception thrown from one stream will not prevent
     * closing of the other stream.
     *
     * @throws IOException if either of the streams could not be closed
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (this.closeBranch) {
                this.branch.close();
            }
        }
    }

    /**
     * Reads a single byte from the proxied input stream and writes it to
     * the associated output stream.
     *
     * @return next byte from the stream, or -1 if the stream has ended
     * @throws IOException if the stream could not be read (or written) 
     */
    @Override
    public int read() throws IOException {
        int ch = super.read();
        if (ch != -1) {
            this.branch.write(ch);
        }
        return ch;
    }

    /**
     * Reads bytes from the proxied input stream and writes the read bytes
     * to the associated output stream.
     *
     * @param bts byte buffer
     * @param st start offset within the buffer
     * @param end maximum number of bytes to read
     * @return number of bytes read, or -1 if the stream has ended
     * @throws IOException if the stream could not be read (or written) 
     */
    @Override
    public int read(byte[] bts, int st, int end) throws IOException {
        int n = super.read(bts, st, end);
        if (n != -1) {
            this.branch.write(bts, st, n);
        }
        return n;
    }

    /**
     * Reads bytes from the proxied input stream and writes the read bytes
     * to the associated output stream.
     *
     * @param bts byte buffer
     * @return number of bytes read, or -1 if the stream has ended
     * @throws IOException if the stream could not be read (or written) 
     */
    @Override
    public int read(byte[] bts) throws IOException {
        int n = super.read(bts);
        if (n != -1) {
            this.branch.write(bts, 0, n);
        }
        return n;
    }

}

class ProxyInputStream extends FilterInputStream {

    /**
     * Constructs a new ProxyInputStream.
     *
     * @param proxy  the InputStream to delegate to
     */
    public ProxyInputStream(InputStream proxy) {
        super(proxy);
        // the proxy is stored in a protected superclass variable named 'in'
    }

    /**
     * Invokes the delegate's <code>read()</code> method.
     * @return the byte read or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        try {
            beforeRead(1);
            int b = this.in.read();
            afterRead(b != -1 ? 1 : -1);
            return b;
        } catch (IOException e) {
            handleIOException(e);
            return -1;
        }
    }

    /**
     * Invokes the delegate's <code>read(byte[])</code> method.
     * @param bts the buffer to read the bytes into
     * @return the number of bytes read or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] bts) throws IOException {
        try {
            beforeRead(bts != null ? bts.length : 0);
            int n = this.in.read(bts);
            afterRead(n);
            return n;
        } catch (IOException e) {
            handleIOException(e);
            return -1;
        }
    }

    /**
     * Invokes the delegate's <code>read(byte[], int, int)</code> method.
     * @param bts the buffer to read the bytes into
     * @param off The start offset
     * @param len The number of bytes to read
     * @return the number of bytes read or -1 if the end of stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] bts, int off, int len) throws IOException {
        try {
            beforeRead(len);
            int n = this.in.read(bts, off, len);
            afterRead(n);
            return n;
        } catch (IOException e) {
            handleIOException(e);
            return -1;
        }
    }

    /**
     * Invokes the delegate's <code>skip(long)</code> method.
     * @param ln the number of bytes to skip
     * @return the actual number of bytes skipped
     * @throws IOException if an I/O error occurs
     */
    @Override
    public long skip(long ln) throws IOException {
        try {
            return this.in.skip(ln);
        } catch (IOException e) {
            handleIOException(e);
            return 0;
        }
    }

    /**
     * Invokes the delegate's <code>available()</code> method.
     * @return the number of available bytes
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int available() throws IOException {
        try {
            return super.available();
        } catch (IOException e) {
            handleIOException(e);
            return 0;
        }
    }

    /**
     * Invokes the delegate's <code>close()</code> method.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /**
     * Invokes the delegate's <code>mark(int)</code> method.
     * @param readlimit read ahead limit
     */
    @Override
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    /**
     * Invokes the delegate's <code>reset()</code> method.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized void reset() throws IOException {
        try {
            this.in.reset();
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /**
     * Invokes the delegate's <code>markSupported()</code> method.
     * @return true if mark is supported, otherwise false
     */
    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    /**
     * Invoked by the read methods before the call is proxied. The number
     * of bytes that the caller wanted to read (1 for the {@link #read()}
     * method, buffer length for {@link #read(byte[])}, etc.) is given as
     * an argument.
     * <p>
     * Subclasses can override this method to add common pre-processing
     * functionality without having to override all the read methods.
     * The default implementation does nothing.
     * <p>
     * Note this method is <em>not</em> called from {@link #skip(long)} or
     * {@link #reset()}. You need to explicitly override those methods if
     * you want to add pre-processing steps also to them.
     *
     * @since Commons IO 2.0
     * @param n number of bytes that the caller asked to be read
     * @throws IOException if the pre-processing fails
     */
    protected void beforeRead(int n) throws IOException {
    	// noop
    }

    /**
     * Invoked by the read methods after the proxied call has returned
     * successfully. The number of bytes returned to the caller (or -1 if
     * the end of stream was reached) is given as an argument.
     * <p>
     * Subclasses can override this method to add common post-processing
     * functionality without having to override all the read methods.
     * The default implementation does nothing.
     * <p>
     * Note this method is <em>not</em> called from {@link #skip(long)} or
     * {@link #reset()}. You need to explicitly override those methods if
     * you want to add post-processing steps also to them.
     *
     * @since Commons IO 2.0
     * @param n number of bytes read, or -1 if the end of stream was reached
     * @throws IOException if the post-processing fails
     */
    protected void afterRead(int n) throws IOException {
    	// noop
    }

    /**
     * Handle any IOExceptions thrown.
     * <p>
     * This method provides a point to implement custom exception
     * handling. The default behaviour is to re-throw the exception.
     * @param e The IOException thrown
     * @throws IOException if an I/O error occurs
     * @since Commons IO 2.0
     */
    protected void handleIOException(IOException e) throws IOException {
        throw e;
    }

}
