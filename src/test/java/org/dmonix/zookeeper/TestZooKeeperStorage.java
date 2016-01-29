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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javascalautils.Option;
import javascalautils.Try;
import junitextensions.OptionAssert;
import zookeeperjunit.CloseableZooKeeper;
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
	private final String propertySetName = "test-set";
	private final String propertySetPath = rootPath+"/"+propertySetName;
	
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
	
	@After
	public void after() {
		storage.close();
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
		Try<Option<PropertySet>> propertySet = storage.get("no-such-set");
		assertSuccess(propertySet);
		assertNone(propertySet.orNull()); //orNull will never happen, just to avoid exception mgmt
	}
	
	@Test
	public void getPropertySet() {
		storePropertySet();
		
		Try<Option<PropertySet>> propertySet = storage.get(propertySetName);
		assertSuccess(propertySet);
		assertSome(propertySet.orNull()); //orNull will never happen, just to avoid exception mgmt
		PropertySet set = propertySet.orNull().orNull(); ////orNull will never happen, just to avoid exception mgmt
		
		//assert we got the properties stored in "storePropertySet"
		assertEquals(2, set.properties().size());
		assertSome("localhost", set.property("host"));
		assertSome("6969", set.property("port"));
	}
	
	@Test
	public void storePropertySet() {
		PropertySet set = PropertySet.apply(propertySetName);
		set.set("host", "localhost");
		set.set("port", "6969");
		
		assertSuccess(storage.store(set));

		//assert the paths exist as expected
		try(CloseableZooKeeper zk = connection()) {
			assertSuccess(true, zk.exists(propertySetPath));
			assertSuccess(true, zk.exists(propertySetPath+"/host"));
			assertSuccess(true, zk.exists(propertySetPath+"/port"));
		}
	}
	
	@Test
	public void storePropertyPath_overwrite() {
		storePropertySet();
		
		PropertySet set = PropertySet.apply(propertySetName);
		set.set("host", "localhost");
		
		assertSuccess(storage.store(set));

		//assert the paths exist as expected
		try(CloseableZooKeeper zk = connection()) {
			assertSuccess(true, zk.exists(propertySetPath));
			assertSuccess(true, zk.exists(propertySetPath+"/host"));
			assertSuccess(false, zk.exists(propertySetPath+"/port")); //there shall be no port node anymore
		}
	}
	
	@Test
	public void delete_nonExisting() {
		assertSuccess(storage.delete("no-such-set"));
	}

	@Test
	public void delete() {
		storePropertySet();
		assertSuccess(storage.delete(propertySetName));
		
		//assert the paths exist as expected
		try(CloseableZooKeeper zk = connection()) {
			assertSuccess(false, zk.exists(propertySetPath+"/port"));
		}
		
	}
	
	@Test
	public void finalize_t() throws Throwable {
		storage.finalize();
	}
}
