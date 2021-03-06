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
package org.apache.juneau.internal;

/**
 * Stores a set of ASCII characters for quick lookup.
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public final class AsciiSet {
	final boolean[] store = new boolean[128];

	/**
	 * Constructor.
	 *
	 * @param chars The characters to keep in this store.
	 */
	public AsciiSet(String chars) {
		for (int i = 0; i < chars.length(); i++) {
			char c = chars.charAt(i);
			if (c < 128)
				store[c] = true;
		}
	}

	/**
	 * Returns <jk>true</jk> if the specified character is in this store.
	 *
	 * @param c The character to check.
	 * @return <jk>true</jk> if the specified character is in this store.
	 */
	public boolean contains(char c) {
		if (c > 127)
			return false;
		return store[c];
	}

	/**
	 * Returns <jk>true</jk> if the specified character is in this store.
	 *
	 * @param c The character to check.
	 * @return <jk>true</jk> if the specified character is in this store.
	 */
	public boolean contains(int c) {
		if (c < 0 || c > 127)
			return false;
		return store[c];
	}
}
