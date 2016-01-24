/**
 * Copyright 2016 Peter Nerg
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dmonix.zookeeper;

import java.time.Duration;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.Assert;

import javascalautils.Try;

/**
 * Base test class.
 * 
 * @author Peter Nerg
 */
public class BaseAssert extends Assert {
	static final long Timeout = 5000;
	static final Duration duration = Duration.ofMillis(Timeout);

    static {
        // Configure language for proper logging outputs
        Locale.setDefault(Locale.US);
        System.setProperty("user.country", Locale.US.getCountry());
        System.setProperty("user.language", Locale.US.getLanguage());
        System.setProperty("user.variant", Locale.US.getVariant());
    }

    private static Try<Integer> divide(int x, int y) {
    	return Try.apply(() -> x/y);
    }
    
    public static void main(String[] args) {
//		Stream<Try<Integer>> stream = Stream.of(divide(10,5), divide(10,2), divide(10,0));
		Stream<Try<Integer>> stream = Stream.of(divide(10,5), divide(10,2), divide(10,1));
		Try<Integer> reduce = stream.reduce(Try.apply(0), (t1, t2) -> t1.flatMap(v -> t2));
		System.out.println(reduce);
	}
    
}
