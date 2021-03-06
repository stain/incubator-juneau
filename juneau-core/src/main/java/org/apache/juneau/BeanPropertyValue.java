// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau;

/**
 * Represents a simple bean property value and the meta-data associated with it.
 * <p>
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class BeanPropertyValue {

	private final BeanPropertyMeta pMeta;
	private final Object value;
	private final Throwable thrown;

	/**
	 * Constructor.
	 *
	 * @param pMeta The bean property metadata.
	 * @param value The bean property value.
	 * @param thrown The exception thrown by calling the property getter.
	 */
	public BeanPropertyValue(BeanPropertyMeta pMeta, Object value, Throwable thrown) {
		this.pMeta = pMeta;
		this.value = value;
		this.thrown = thrown;
	}

	/**
	 * Returns the bean property metadata.
	 * @return The bean property metadata.
	 */
	public final BeanPropertyMeta getMeta() {
		return pMeta;
	}

	/**
	 * Returns the bean property metadata.
	 * @return The bean property metadata.
	 */
	public final ClassMeta<?> getClassMeta() {
		return pMeta.getClassMeta();
	}

	/**
	 * Returns the bean property name.
	 * @return The bean property name.
	 */
	public final String getName() {
		return pMeta.getName();
	}

	/**
	 * Returns the bean property value.
	 * @return The bean property value.
	 */
	public final Object getValue() {
		return value;
	}

	/**
	 * Returns the exception thrown by calling the property getter.
	 * @return The exception thrown by calling the property getter.
	 */
	public final Throwable getThrown() {
		return thrown;
	}
}
