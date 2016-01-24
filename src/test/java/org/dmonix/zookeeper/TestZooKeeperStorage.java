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

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javascalautils.Option;
import javascalautils.Try;
import junitextensions.OptionAssert;
import zookeeperjunit.ZKFactory;
import zookeeperjunit.ZKInstance;
import zookeeperjunit.ZooKeeperAssert;

/**
 * @author Peter Nerg
 *
 */
public class TestZooKeeperStorage extends BaseAssert implements ZooKeeperAssert, OptionAssert {
	private static ZKInstance instance = ZKFactory.apply().create();
	
	private final AtomicLong counter = new AtomicLong(1);
	private final String rootPath = "/TestZooKeeperStorage-"+counter.getAndIncrement();
	
	private final ZooKeeperStorage storage = new ZooKeeperStorage(instance.connectString().get(), rootPath);
	
	@BeforeClass
	public static void startZooKeeper() throws TimeoutException, Throwable {
		instance.start().result(duration);
	}
	
	@AfterClass
	public static void stopZooKeeper() throws TimeoutException, Throwable {
		instance.destroy().result(duration);
	}

	@Before
	public void before() throws IOException, InterruptedException {
		storage.connect();
	}
	
	/* (non-Javadoc)
	 * @see zookeeperjunit.ZooKeeperAssert#instance()
	 */
	@Override
	public ZKInstance instance() {
		return instance;
	}

	@Test
	public void getPropertySet_nonSuchSet() {
		Try<Option<PropertySet>> propertySet = storage.getPropertySet("no-such-set");
		assertSuccess(propertySet);
		assertNone(propertySet.orNull()); //orNull will never happen, just to avoid exception mgmt
	}
	
}
