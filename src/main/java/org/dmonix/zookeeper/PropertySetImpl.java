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

import static javascalautils.OptionCompanion.Option;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javascalautils.Option;


/**
 * The implementation of the property set.
 * 
 * @author Peter Nerg
 * @since 1.0
 */
final class PropertySetImpl implements PropertySet {

	private final String name;
	private final Map<String, String> properties = new HashMap<>();

	PropertySetImpl(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertySet#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertySet#property(java.lang.String)
	 */
	@Override
	public Option<String> property(String name) {
		return Option(properties.get(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertySet#properties()
	 */
	@Override
	public Set<String> properties() {
		return new HashSet<>(properties.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmonix.zookeeper.PropertySet#set(java.lang.String, java.lang.String)
	 */
	@Override
	public Option<String> set(String name, String value) {
		return Option(properties.put(name, value));
	}

	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertySet#asMap()
	 */
	@Override
	public Map<String, String> asMap() {
		return Collections.unmodifiableMap(properties);
	}
	/**
	 * Provides a meaningful string representation of the property set
	 * @since 1.2
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("{");
		properties.forEach((k,v) -> sb.append(k).append(":").append(v).append(","));
		sb.append("}");
		return sb.toString();
	}
}
