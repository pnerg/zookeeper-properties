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

import static javascalautils.TryCompanion.Try;

import javascalautils.Try;

/**
 * The implementation of the factory
 * @author Peter Nerg
 */
final class PropertiesStorageFactoryImpl implements PropertiesStorageFactory {

	private final String connectString;
	
	private String rootPath = "/etc/property-sets";

	PropertiesStorageFactoryImpl(String connectString) {
		this.connectString = connectString;
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
