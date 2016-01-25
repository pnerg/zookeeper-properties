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

import java.util.List;
import java.util.concurrent.TimeoutException;
import static javascalautils.OptionCompanion.Option;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

import org.apache.zookeeper.KeeperException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javascalautils.Try;
import zookeeperjunit.CloseableZooKeeper;
import zookeeperjunit.ZKFactory;
import zookeeperjunit.ZKInstance;
import zookeeperjunit.ZooKeeperAssert;

/**
 * @author Peter Nerg
 *
 */
public class TestZooKeeperUtil extends BaseAssert implements ZooKeeperAssert {

	private static ZKInstance instance = ZKFactory.apply().create();
	
	private CloseableZooKeeper zooKeeper;
	
	@Before
	public void startZooKeeper() throws TimeoutException, Throwable {
		instance.start().result(duration);
		zooKeeper = instance.connect().get();
		
		createZNode("/empty");
		createZNode("/parent");
		createZNode("/parent/child1");
		createZNode("/parent/child1/child11");
		createZNode("/parent/child1/child12");
		createZNode("/parent/child2");
		createZNode("/parent/child2/child21");
		createZNode("/parent/child2/child22");
	}
	
	@After
	public void stopZooKeeper() throws TimeoutException, Throwable {
		Option(zooKeeper).forEach(CloseableZooKeeper::close);
		instance.destroy().result(duration);
	}

	/* (non-Javadoc)
	 * @see zookeeperjunit.ZooKeeperAssert#instance()
	 */
	@Override
	public ZKInstance instance() {
		return instance;
	}

	@Test
	public void exists() throws KeeperException, InterruptedException {
		assertTrue(ZooKeeperUtil.exists(zooKeeper, "/empty"));
	}

	@Test
	public void exists_nonExisting() throws KeeperException, InterruptedException {
		assertFalse(ZooKeeperUtil.exists(zooKeeper, "/no-such-path"));
	}
	
	@Test
	public void createIfNotExist_newPath() throws KeeperException, InterruptedException {
		assertTrue(ZooKeeperUtil.createIfNotExist(zooKeeper, "/new-path", new byte[0]));
		assertExists("/new-path");
	}

	@Test
	public void createIfNotExist_existingPath() throws KeeperException, InterruptedException {
		assertFalse(ZooKeeperUtil.createIfNotExist(zooKeeper, "/empty", new byte[0]));
		assertExists("/empty");
	}
	
	@Test
	public void createRecursive() throws KeeperException, InterruptedException {
		ZooKeeperUtil.createRecursive(zooKeeper, "/empty/child", new byte[0]);
		assertExists("/empty/child");
	}

	@Test
	public void children_exitingPath() {
		Try<List<String>> children = ZooKeeperUtil.children(zooKeeper, "/parent");
		assertSuccess(children);
		assertEquals(2, children.orNull().size());
	}

	@Test
	public void children_noSuchPath() {
		assertFailure(ZooKeeperUtil.children(zooKeeper, "/no-such-path"));
	}
	
	@Test
	public void delete_existing() {
		assertSuccess(ZooKeeperUtil.delete(zooKeeper, "/empty"));
	}
	
	@Test
	public void delete_nonExisting() {
		assertSuccess(ZooKeeperUtil.delete(zooKeeper, "/empty"));
	}
	
	@Test
	public void deleteRecursive_leafNode() {
		assertSuccess(ZooKeeperUtil.deleteRecursive(zooKeeper, "/empty"));
		assertNotExists("/empty");
	}

	@Test
	public void deleteRecursive_withChildren() {
		assertSuccess(ZooKeeperUtil.deleteRecursive(zooKeeper, "/parent"));
		assertNotExists("/parent");
	}
	
	@Test
	public void deleteRecursive_nonExisting() {
		assertSuccess(ZooKeeperUtil.deleteRecursive(zooKeeper, "/no-such-path"));
	}

	private void createZNode(String path) throws KeeperException, InterruptedException {
		zooKeeper.create(path, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
	}
}
