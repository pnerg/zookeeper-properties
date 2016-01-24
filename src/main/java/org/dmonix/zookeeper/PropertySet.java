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

import javascalautils.Option;
import javascalautils.Try;
import javascalautils.Unit;

/**
 * Represents a single property (key/value) set.
 * @author Peter Nerg
 * @since 1.0
 */
interface PropertySet {

	/**
	 * Get the name of the property set
	 * @return The name
	 * @since 1.0
	 */
	String name();

	/**
	 * Get the named property.
	 * @param name The name of the property
	 * @return If exists then Some containing the value, else None.
	 */
	Option<String> property(String name);
	
	Try<Unit> store();
}
