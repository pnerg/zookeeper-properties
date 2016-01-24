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

import java.io.Closeable;
import java.util.List;

import javascalautils.Failure;
import javascalautils.None;
import javascalautils.Option;
import javascalautils.Some;
import javascalautils.Success;
import javascalautils.Try;
import javascalautils.Unit;

/**
 * The interface for managing persisted property sets.
 * @author Peter Nerg
 * @since 1.0
 */
public interface PropertiesStorage extends Closeable {

	/**
	 * Attempt to get a named property set from ZooKeeper. <br>
	 * If the operation fails due to e.g. connection issue the operation will return a {@link Failure}. <br>
	 * Else the operation returns a {@link Success} containing an optional result. <br>
	 * If there was not property set with the provided name {@link None} is returned else {@link Some} containing the property set.
	 * @param name The name of the property set
	 * @return The result
	 * @since 1.0
	 */
	Try<Option<PropertySet>> get(String name);
	
	/**
	 * Attempt to store the provided property set. <br>
     * Note: Any existing property set in ZooKeeper will be overwritten.<br>
     * In an essence the path is first removed and then re-created with the properties provided. <br>
     * This mimics the behavior of storing property sets to a file where the actual file is overwritten
	 * @param propertySet The property set to store
	 * @return The result, {@link Failure} in case there was a problem persisting the data else {@link Success}
	 * @since 1.0
	 */
	Try<Unit> store(PropertySet propertySet);

	/**
	 * Attempts to delete an existing property set. <br>
	 * Attempting to delete non-existing data will <u>not</u> yield a {@link Failure}.
	 * @param name The name of the property set to delete
	 * @return The result, {@link Failure} in case there was a problem deleting the data else {@link Success}
	 * @since 1.0
	 */
	Try<Unit> delete(String name);
	
	/**
	 * Attempt to list the names of all persisted property sets. <br> 
	 * Returns either {@link Success} containing the names (may be an empty list) or {@link Failure} in case there was issues with ZooKeeper
	 * @return The result
	 * @since 1.0
	 */
	Try<List<String>> propertySets();

	
}
