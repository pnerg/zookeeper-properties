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

import static org.dmonix.zookeeper.ZooKeeperUtil.children;
import static org.dmonix.zookeeper.ZooKeeperUtil.createRecursive;
import static org.dmonix.zookeeper.ZooKeeperUtil.deleteRecursive;
import static org.dmonix.zookeeper.ZooKeeperUtil.exists;
import static org.dmonix.zookeeper.ZooKeeperUtil.getData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
	public Try<Option<PropertySet>> get(String name) {
		
		 Try<PropertySet> flatMap = Try(() -> zooKeeper.get()).flatMap(zk -> {
			String path = propertySetPath(name);
			return children(zk, propertySetPath(name)).
				flatMap(children -> {
					return Try(() -> {
						PropertySet propertySet = PropertySet.apply(name);
						for (String child : children) {
							propertySet.set(child, getData(zk, path+"/"+child));
						}
						return propertySet;
					});
			});
		});
			
		return Try(() -> {
			ZooKeeper zk = zooKeeper.get();
			String path = propertySetPath(name);
			children(zk, propertySetPath(name)).
				flatMap(children -> {
					return Try(() -> {
						PropertySet propertySet = PropertySet.apply(name);
						for (String child : children) {
							propertySet.set(child, getData(zk, path+"/"+child));
						}
						return propertySet;
					});
			});
			
		return null;
		});		
//			children.failed().filter
//			// not so functional but as match/case constructs don't exist in Java this will have to do..:(
//			if (children.isEmpty()) {
//				return None();
//			}
//
//			PropertySet propertySet = PropertySet.apply(name);
//			// orNull will never happen as we know the Option to be Some(...)
//			for (String child : children.orNull()) {
//				// getData may return null, hence the Option
//				Option(zk.getData(propertySetPath(name) + "/" + child, null, null)).map(data -> new String(data)).forEach(value -> {
//					propertySet.set(child, value);
//				});
//			}
//
//			return Option(propertySet);
//		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertiesStorage#storePropertySet(java.lang.String, org.dmonix.zookeeper.PropertySet)
	 */
	@Override
	public Try<Unit> store(PropertySet propertySet) {
		String path = propertySetPath(propertySet.name());

		// if that fails there's no point to continue with the rest of the operation, hence the flatMap
		return delete(propertySet.name()).flatMap(u -> {
			return Try(() -> {
				ZooKeeper zk = zooKeeper.get();
				ZooKeeperUtil.createRecursive(zk, path, new byte[0]); // recreate the property set znode
				for (String prop : propertySet.properties()) { // write the properties one by one
					createRecursive(zk, path + "/" + prop, propertySet.property(prop).get().getBytes());
				}
			});
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertiesStorage#propertySets()
	 */
	@Override
	public Try<List<String>> propertySets() {
		return connection().flatMap(zk -> children(zk, rootPath));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertiesStorage#delete(java.lang.String)
	 */
	@Override
	public Try<Unit> delete(String name) {
		String path = propertySetPath(name);
		return connection().flatMap(zk -> deleteRecursive(zk, path));
	}

	private Try<ZooKeeper> connection() {
		return Try(() -> zooKeeper.get());
	}

	/**
	 * Closes any open ZooKeeper connection
	 */
	@Override
	public void close() {
		zooKeeper.forEach(zk -> Try(() -> zk.close()));
	}

	/**
	 * Overridden to make sure we close the ZooKeeper connection held by this instance.
	 */
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	private String propertySetPath(String name) {
		return rootPath + "/" + name;
	}

}
