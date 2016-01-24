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
 * @author Peter Nerg
 *
 */
final class PropertySetImpl implements PropertySet {

	private final String name;

	PropertySetImpl(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertySet#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertySet#property(java.lang.String)
	 */
	@Override
	public Option<String> property(String name) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertySet#set(java.lang.String, java.lang.String)
	 */
	@Override
	public Option<String> set(String name, String value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dmonix.zookeeper.PropertySet#store()
	 */
	@Override
	public Try<Unit> store() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	

}
