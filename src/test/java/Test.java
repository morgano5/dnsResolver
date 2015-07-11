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
import au.id.villar.dns.DnsException;
import au.id.villar.dns.Resolver;
import au.id.villar.dns.cache.SimpleDnsCache;
import au.id.villar.dns.engine.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {

	public static void main(String[] args) {

		Resolver resolver = Resolver
				.withCache(new SimpleDnsCache())//.usingIPv4(true).usingIPv6(false)
				.withRootServers(Arrays.asList(
					"198.41.0.4",
					"192.228.79.201",
					"192.33.4.12",
					"199.7.91.13",
					"192.203.230.10",
					"192.5.5.241",
					"192.112.36.4",
					"128.63.2.53",
					"192.36.148.17",
					"192.58.128.30",
					"193.0.14.129",
					"199.7.83.42",
					"202.12.27.33"
				))
				.build();

		Resolver.AnswerProcess process = resolver.lookup("villar.me", DnsType.ALL);

		int timeout = 100;
		boolean done;
		do {

			done = process.doIO(timeout);

			// do something else...

		} while (!done);

		List<ResourceRecord> result = Collections.emptyList();
		try {
			result = process.getResult();
		} catch (DnsException e) {
			e.printStackTrace();
		}

		for(ResourceRecord rr: result) {
			System.out.println("Result: " + rr.getDnsName() + " - " + rr.getDnsType() + " - " + rr.getData(Object.class));
		}

	}

}
