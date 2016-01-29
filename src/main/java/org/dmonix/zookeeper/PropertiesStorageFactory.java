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

import javascalautils.Try;

import static javascalautils.TryCompanion.Try;

/**
 * Factory for creating {@link PropertiesStorage} instances.
 * @author Peter Nerg
 * @since 1.0
 */
public class PropertiesStorageFactory {

	private String connectString;
	private String rootPath = "/etc/property-sets";

	private PropertiesStorageFactory(String connectString) {
		this.connectString = connectString;
	}

	/**
	 * Creates the factory instance.
	 * @param connectString The connect string used to connect to ZooKeeper.
	 * @return The factory instance
	 * @since 1.0
	 */
	public static PropertiesStorageFactory apply(String connectString) {
		return new PropertiesStorageFactory(connectString);
	}

	/**
	 * Creates a properties storage instance.
	 * @return The result of creating the instance
	 * @since 1.0
	 */
	public Try<PropertiesStorage> create() {
		return Try(() -> {
			ZooKeeperStorage storage = new ZooKeeperStorage(connectString, rootPath);
			storage.connect();
			return storage;
		});
	}
}
