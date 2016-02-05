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
import javascalautils.Validator;

/**
 * Factory for creating {@link PropertiesStorage} instances.
 * @author Peter Nerg
 * @since 1.0
 */
public interface PropertiesStorageFactory {

	/**
	 * Creates the factory instance.
	 * @param connectString The connect string used to connect to ZooKeeper.
	 * @return The factory instance
	 * @since 1.0
	 */
	static PropertiesStorageFactory apply(String connectString) {
		return new PropertiesStorageFactoryImpl(Validator.requireNonNull(connectString));
	}

	/**
	 * Provides the root path where to store all the property sets.
	 * @param rootPath The root path
	 * @return The factory instance
	 * @since 1.1
	 */
	PropertiesStorageFactory withRootPath(String rootPath);
	
	/**
	 * Creates a properties storage instance.
	 * @return The result of creating the instance
	 * @since 1.0
	 */
	Try<PropertiesStorage> create();

}
