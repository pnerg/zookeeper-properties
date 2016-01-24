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
import static javascalautils.OptionCompanion.Some;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import javascalautils.Option;
import javascalautils.Try;

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
		if(!latch.await(10, TimeUnit.SECONDS)) {
			throw new IOException("Failed to connect to ZooKeeper");
		}
		zooKeeper = Some(zk);
	}
	
	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertiesStorage#getPropertySet(java.lang.String)
	 */
	@Override
	public Try<Option<PropertySet>> getPropertySet(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
