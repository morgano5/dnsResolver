/*
 * Copyright 2016 Rafael Villar Villar
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

import au.id.villar.dns.DNSException;
import au.id.villar.dns.TestUtils;
import au.id.villar.dns.engine.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

public class TCPDNSQueryClientTest {

    public static void main(String[] args) throws IOException, DNSException, InterruptedException {
        try(Selector selector = Selector.open();
            DNSQueryClient client = new TCPDNSQueryClient(53, selector)) {

            DNSEngine engine = new DNSEngine();
            Question question = engine.createQuestion("villar.me", DNSType.ALL, DNSClass.IN);
            DNSMessage message = engine.createSimpleQueryMessage((short)15, question);
            ByteBuffer rawMessage = engine.createBufferFromMessage(message);

            boolean done = client.startQuery(rawMessage, "8.8.8.8", 10_000);
            while(!done) {
                System.out.println("waiting...");
                Thread.sleep(1000);
                done = client.doIO(1000);
            }

            DNSMessage response = engine.createMessageFromBuffer(client.getResult().array(), 0);

            System.out.println("\n\n" + TestUtils.messageToString(response) + "\n\n");

            Thread.sleep(100);
        }
    }

}