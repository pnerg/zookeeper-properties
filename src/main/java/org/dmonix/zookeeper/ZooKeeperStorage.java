/**
 *  Copyright 2015 Peter Nerg
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

import static javascalautils.OptionCompanion.None;
import static javascalautils.OptionCompanion.Option;
import static javascalautils.TryCompanion.Try;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import javascalautils.Option;
import javascalautils.Try;
import javascalautils.Unit;

/**
 * @author Peter Nerg
 *
 */
class ZooKeeperStorage implements PropertiesStorage {

	private String connectString;
	private String rootPath;
	private Option<ZooKeeper> zooKeeper = None();

	/**
	 * @param connectString
	 * @param rootPath
	 */
	ZooKeeperStorage(String connectString, String rootPath) {
		this.connectString = connectString;
		this.rootPath = rootPath;
	}

	void connect() throws IOException, InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		ZooKeeper zk = new ZooKeeper(connectString, 10000, event -> {
			if (event.getState() == KeeperState.SyncConnected) {
				latch.countDown();
			}
		});
		if (!latch.await(10, TimeUnit.SECONDS)) {
			throw new IOException("Failed to connect to ZooKeeper");
		}
		zooKeeper = Option(zk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertiesStorage#getPropertySet(java.lang.String)
	 */
	@Override
	public Try<Option<PropertySet>> getPropertySet(String name) {
		return Try(() -> {
			ZooKeeper zk = zooKeeper.get();
			Option<List<String>> children = children(zk, propertySetPath(name));
			//not so functional but as match/case constructs don't exist in Java this will have to do..:(
			if(children.isEmpty()) {
				return None();
			}
			
			PropertySet propertySet = PropertySet.apply(name);
			//orNull will never happen as we know the Option to be Some(...)
			for(String child : children.orNull()) {
				zk.getData(propertySetPath(name)+"/"+child, null, null);
			}
			
			return Option(propertySet);
		});
	}

	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertiesStorage#storePropertySet(java.lang.String, org.dmonix.zookeeper.PropertySet)
	 */
	@Override
	public Try<Unit> storePropertySet(PropertySet propertySet) {
		return Try(() -> {
			ZooKeeper zk = zooKeeper.get();
			String path = propertySetPath(propertySet.name());
			deletePropertySet(zk, path); //first delete the old property set
			createRecursive(zk, path, new byte[0]); //recreate the property set znode
			for(String prop : propertySet.properties()) { // write the propertues one by one
				createRecursive(zk, path+"/"+prop, propertySet.property(prop).get().getBytes());
			}
		});
	}
	
	private String propertySetPath(String name) {
		return rootPath + "/" + name;
	}

	
	private static Option<List<String>> children(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
		try {
			return Option(zk.getChildren(path, null));
		} catch (NoNodeException ex) {
			return None();
		}
	}

	private static boolean createRecursive(ZooKeeper zooKeeper, String path, byte[] data) throws KeeperException, InterruptedException {
		try {
			return createIfNotExist(zooKeeper, path, data);
		} catch (KeeperException.NoNodeException ex) {
			int pos = path.lastIndexOf("/");
			String parentPath = path.substring(0, pos);
			createRecursive(zooKeeper, parentPath, new byte[0]);
			return createIfNotExist(zooKeeper, path, data);
		}
	}
	
	private static boolean createIfNotExist(ZooKeeper zooKeeper, String path, byte[] data) throws KeeperException, InterruptedException {
		boolean result = false;
		try {
			// check if the path exists before trying to create
			if (!exists(zooKeeper, path)) {
				zooKeeper.create(path, data, OPEN_ACL_UNSAFE, PERSISTENT);
				result = true;
			}
		}
		// this may still happen in a concurrent world some other process/thread may end up creating the path
		// after we did the exists(...) operation, so just in case we need to manage the exception
		catch (KeeperException.NodeExistsException ex) {
		}

		return result;
	}
	
	private static boolean exists(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
		return zooKeeper.exists(path, null) != null;
	}
	
	/**
	 * Deletes a property set
	 * @param path
	 *            The path to the property set
	 * @return The result of the operation
	 */
	private static Try<Unit> deletePropertySet(ZooKeeper zk, String path) {
		return Try(() -> {
			Option<List<String>> children = children(zk, path);
			//attempt to delete all children recursively
			for(String child : children.getOrElse(Collections::emptyList)) {
				zk.delete(path+"/"+child, -1); //-1 for any version
			}
			zk.delete(path, -1); //-1 for any version
		});
		
	}
}
