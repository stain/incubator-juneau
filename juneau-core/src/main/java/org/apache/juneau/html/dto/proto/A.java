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
package org.apache.juneau.html.dto.proto;

import static org.apache.juneau.xml.annotation.XmlFormat.*;

import org.apache.juneau.annotation.*;
import org.apache.juneau.xml.annotation.*;

/**
 * DTO for an HTML <a href='https://www.w3.org/TR/html5/text-level-semantics.html#the-a-element'>&lt;a&gt;</a> element.
 * <p>
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
@Bean(typeName="a")
@SuppressWarnings("javadoc")
public class A extends HtmlElement {

	/** <code>name</code> attribute */
	@Xml(format=ATTR)
	public String name;

	/** <code>href</code> attribute */
	@Xml(format=ATTR)
	public String href;

	/** Content */
	@Xml(format=CONTENT)
	public String text;

	public A setName(String name) {
		this.name = name;
		return this;
	}

	public A setHref(String href) {
		this.href = href;
		return this;
	}

	public A setText(String text) {
		this.text = text;
		return this;
	}
}
