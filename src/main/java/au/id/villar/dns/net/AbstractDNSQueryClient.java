/*
 * Copyright 2015 Rafael Villar Villar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.id.villar.dns.net;

import au.id.villar.dns.DnsException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

abstract class AbstractDNSQueryClient implements DNSQueryClient {

    protected enum Status {
        OPENING,
        CONNECTING,
        SENDING,
        RECEIVING,
        RESULT,
        ERROR,
        CLOSED
    }

    protected final int dnsPort;
    protected final Selector selector;

    protected SelectableChannel channel;
    protected String address;
    protected ByteBuffer query;
    protected ByteBuffer result;
    protected Status status;

    AbstractDNSQueryClient(int dnsPort, Selector selector) throws IOException {
        this.dnsPort = dnsPort;
        this.selector = selector;
    }

    @Override
    public boolean startQuery(ByteBuffer query, String address, int timeoutMillis) throws DnsException {

        if(status == Status.CLOSED) throw new DnsException("Already closed");

        try {

            IOException exception = close(channel);
            if(exception != null) throw exception;

            this.result = null;
            this.address = address;
            this.status = Status.OPENING;
            this.query = query;
            return doIO(timeoutMillis);
        } catch (IOException e) {
            throw new DnsException(e);
        }
    }

    @Override
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "Duplicates"})
    public boolean doIO(int timeoutMillis) throws DnsException {
        try {

            switch(status) {
                case CLOSED: throw new DnsException("Already closed");
                case ERROR: throw new DnsException("Invalid state");
                case RESULT: return true;
            }

            return internalDoIO(timeoutMillis);
        } catch(IOException | DnsException e) {
            close(channel);
            status = Status.ERROR;
            throw e instanceof DnsException? (DnsException)e: new DnsException(e);
        }
    }

    @Override
    public ByteBuffer getResult() {
        return result;
    }

    @Override
    public void close() throws IOException {
        IOException exChannel = close(channel);
        channel = null;
        status = Status.CLOSED;
        if(exChannel != null) throw exChannel;
    }

    protected abstract boolean internalDoIO(int timeoutMillis) throws IOException, DnsException;

    protected boolean sendDataAndPrepareForReceiving(int timeoutMillis, Channel channel) throws IOException {
        if(selector.select(timeoutMillis) == 0) return false;
        Iterator iterator = selector.selectedKeys().iterator();
        iterator.next();
        iterator.remove();
        ((SelectableChannel)channel).register(selector, SelectionKey.OP_READ);
        ((WritableByteChannel)channel).write(query);
        return true;
    }

    private IOException close(Channel channel) {
        try {
            if(channel != null && channel.isOpen()) channel.close();
            return null;
        } catch(IOException e) {
            return e;
        }
    }

}
