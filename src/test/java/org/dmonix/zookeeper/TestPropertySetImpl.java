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
	private final PropertySetImpl propertySet = new PropertySetImpl(TestPropertySetImpl.class.getSimpleName());
	
	@Test
	public void name() {
		assertEquals(TestPropertySetImpl.class.getSimpleName(), propertySet.name());
	}
	
	@Test
	public void property_nonExisting() {
		assertNone(propertySet.property("NO-SUCH-PROPERTY"));
	}
	
	@Test
	public void set_nonExisting() {
		assertNone(propertySet.set("new-key", "new-value"));
		assertSome("new-value", propertySet.property("new-key"));
	}

	@Test
	public void set_owerwriteExisting() {
		set_nonExisting();
		assertSome("new-value", propertySet.set("new-key", "overwritten-value"));
		assertSome("overwritten-value", propertySet.property("new-key"));
	}
	
	@Test
	public void properties_empty() {
		assertTrue(propertySet.properties().isEmpty());
	}

	@Test
	public void properties_nonEmpty() {
		set_nonExisting();
		assertEquals(1, propertySet.properties().size());
	}
	
	@Test
	public void t_toString() {
		set_nonExisting();
		System.out.println(propertySet.toString());
	}
}
