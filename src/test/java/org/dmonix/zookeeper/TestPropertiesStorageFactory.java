/**
 *  Copyright 2016 Peter Nerg
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

import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junitextensions.TryAssert;
import zookeeperjunit.ZKFactory;
import zookeeperjunit.ZKInstance;

/**
 * Test the class {@link PropertiesStorageFactory}
 * @author Peter Nerg
 */
public class TestPropertiesStorageFactory extends BaseAssert implements TryAssert {
	private static ZKInstance instance = ZKFactory.apply().create();
	
//	private final AtomicLong counter = new AtomicLong(1);
//	private final String rootPath = "/TestZooKeeperStorageFactory-"+counter.getAndIncrement();
	
	private final PropertiesStorageFactory factory = PropertiesStorageFactory.apply(instance.connectString().get());
	
	@BeforeClass
	public static void startZooKeeper() throws TimeoutException, Throwable {
		instance.start().result(duration);
	}
	
	@AfterClass
	public static void stopZooKeeper() throws TimeoutException, Throwable {
		instance.destroy().result(duration);
	}
	
	@Test
	public void create() {
		assertSuccess(factory.create());
	}
}
