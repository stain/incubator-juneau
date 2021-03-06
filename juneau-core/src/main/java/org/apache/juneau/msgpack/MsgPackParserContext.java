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
package org.apache.juneau.msgpack;

import org.apache.juneau.*;
import org.apache.juneau.parser.*;

/**
 * Configurable properties on the {@link MsgPackParser} class.
 * <p>
 * Context properties are set by calling {@link ContextFactory#setProperty(String, Object)} on the context factory
 * returned {@link CoreApi#getContextFactory()}.
 * <p>
 * The following convenience methods are also provided for setting context properties:
 * <ul>
 * 	<li>{@link MsgPackParser#setProperty(String,Object)}
 * 	<li>{@link MsgPackParser#setProperties(ObjectMap)}
 * 	<li>{@link MsgPackParser#addNotBeanClasses(Class[])}
 * 	<li>{@link MsgPackParser#addBeanFilters(Class[])}
 * 	<li>{@link MsgPackParser#addPojoSwaps(Class[])}
 * 	<li>{@link MsgPackParser#addToDictionary(Class[])}
 * 	<li>{@link MsgPackParser#addImplClass(Class,Class)}
 * </ul>
 * <p>
 * See {@link ContextFactory} for more information about context properties.
 *
 *
 * <h6 class='topic' id='ConfigProperties'>Configurable properties on the MessagePack parser</h6>
 * <table class='styled' style='border-collapse: collapse;'>
 * 	<tr><th>Setting name</th><th>Description</th><th>Data type</th><th>Default value</th></tr>
 * </table>
 *
 * <h6 class='topic'>Configurable properties inherited from parent classes</h6>
 * <ul class='javahierarchy'>
 * 	<li class='c'><a class='doclink' href='../BeanContext.html#ConfigProperties'>BeanContext</a> - Properties associated with handling beans on serializers and parsers.
 * 	<ul>
 * 		<li class='c'><a class='doclink' href='../parser/ParserContext.html#ConfigProperties'>ParserContext</a> - Configurable properties common to all parsers.
 * 	</ul>
 * </ul>
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public final class MsgPackParserContext extends ParserContext {

	/**
	 * Constructor.
	 * <p>
	 * Typically only called from {@link ContextFactory#getContext(Class)}.
	 *
	 * @param cf The factory that created this context.
	 */
	public MsgPackParserContext(ContextFactory cf) {
		super(cf);
	}
}
