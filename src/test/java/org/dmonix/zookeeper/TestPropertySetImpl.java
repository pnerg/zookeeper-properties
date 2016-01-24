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

import org.junit.Test;

import junitextensions.OptionAssert;

/**
 * Test the class {@link PropertySetImpl}
 * @author Peter Nerg
 */
public class TestPropertySetImpl extends BaseAssert implements OptionAssert {
	private final PropertySetImpl propertySet = new PropertySetImpl(TestPropertySetImpl.class.getName());
	
	@Test
	public void name() {
		assertEquals(TestPropertySetImpl.class.getName(), propertySet.name());
	}
	
	
}