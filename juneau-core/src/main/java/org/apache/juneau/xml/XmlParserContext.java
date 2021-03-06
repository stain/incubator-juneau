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
package org.apache.juneau.xml;

import javax.xml.stream.*;
import javax.xml.stream.util.*;

import org.apache.juneau.*;
import org.apache.juneau.parser.*;

/**
 * Configurable properties on the {@link XmlParser} class.
 * <p>
 * Context properties are set by calling {@link ContextFactory#setProperty(String, Object)} on the context factory
 * returned {@link CoreApi#getContextFactory()}.
 * <p>
 * The following convenience methods are also provided for setting context properties:
 * <ul>
 * 	<li>{@link XmlParser#setProperty(String,Object)}
 * 	<li>{@link XmlParser#setProperties(ObjectMap)}
 * 	<li>{@link XmlParser#addNotBeanClasses(Class[])}
 * 	<li>{@link XmlParser#addBeanFilters(Class[])}
 * 	<li>{@link XmlParser#addPojoSwaps(Class[])}
 * 	<li>{@link XmlParser#addToDictionary(Class[])}
 * 	<li>{@link XmlParser#addImplClass(Class,Class)}
 * </ul>
 * <p>
 * See {@link ContextFactory} for more information about context properties.
 *
 *
 * <h6 class='topic' id='ConfigProperties'>Configurable properties on the XML parser</h6>
 * <table class='styled' style='border-collapse: collapse;'>
 * 	<tr><th>Setting name</th><th>Description</th><th>Data type</th><th>Default value</th></tr>
 * 	<tr>
 * 		<td>{@link #XML_xsiNs}</td>
 * 		<td>XMLSchema-instance namespace URI.</td>
 * 		<td><code>String<code></td>
 * 		<td><js>"http://www.w3.org/2001/XMLSchema-instance"</js></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_trimWhitespace}</td>
 * 		<td>Trim whitespace from text elements.</td>
 * 		<td><code>Boolean<code></td>
 * 		<td><jk>true</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_validating}</td>
 * 		<td>Enable validation.</td>
 * 		<td><code>Boolean<code></td>
 * 		<td><jk>false</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_coalescing}</td>
 * 		<td>Enable text element coalescing.</td>
 * 		<td><code>Boolean<code></td>
 * 		<td><jk>false</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_replaceEntityReferences}</td>
 * 		<td>Replace entity references.</td>
 * 		<td><code>Boolean<code></td>
 * 		<td><jk>true</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_reporter}</td>
 * 		<td>XML reporter.</td>
 * 		<td>{@link XMLReporter}</td>
 * 		<td><jk>null</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_resolver}</td>
 * 		<td>XML resolver.</td>
 * 		<td>{@link XMLResolver}</td>
 * 		<td><jk>null</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_eventAllocator}</td>
 * 		<td>XML event allocator.</td>
 * 		<td>{@link XMLEventAllocator}</td>
 * 		<td><jk>null</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #XML_preserveRootElement}</td>
 * 		<td>Preserve root element during generalized parsing.</td>
 * 		<td><code>Boolean<code></td>
 * 		<td><jk>false</jk></td>
 * 	</tr>
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
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public final class XmlParserContext extends ParserContext {

	/**
	 * <b>Configuration property:</b>  XMLSchema-instance namespace URI.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.xsiNs"</js>
	 * 	<li><b>Data type:</b> <code>String</code>
	 * 	<li><b>Default:</b> <js>"http://www.w3.org/2001/XMLSchema-instance"</js>
	 * </ul>
	 * <p>
	 * The XMLSchema namespace.
	 */
	public static final String XML_xsiNs = "XmlParser.xsiNs";

	/**
	 * <b>Configuration property:</b>  Trim whitespace from text elements.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.trimWhitespace"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>true</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, whitespace in text elements will be automatically trimmed.
	 */
	public static final String XML_trimWhitespace = "XmlParser.trimWhitespace";

	/**
	 * <b>Configuration property:</b>  Enable validation.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.validating"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>false</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, XML document will be validated.
	 * See {@link XMLInputFactory#IS_VALIDATING} for more info.
	 */
	public static final String XML_validating = "XmlParser.validating";

	/**
	 * <b>Configuration property:</b>  Enable text element coalescing.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.coalescing"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>false</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, XML text elements will be coalesced.
	 * See {@link XMLInputFactory#IS_COALESCING} for more info.
	 */
	public static final String XML_coalescing = "XmlParser.coalescing";

	/**
	 * <b>Configuration property:</b>  Replace entity references.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.replaceEntityReferences"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>true</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, entity references will be replace during parsing.
	 * See {@link XMLInputFactory#IS_REPLACING_ENTITY_REFERENCES} for more info.
	 */
	public static final String XML_replaceEntityReferences = "XmlParser.replaceEntityReferences";

	/**
	 * <b>Configuration property:</b>  XML reporter.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.reporter"</js>
	 * 	<li><b>Data type:</b> {@link XMLReporter}
	 * 	<li><b>Default:</b> <jk>null</jk>
	 * </ul>
	 * <p>
	 * Associates an {@link XMLReporter} with this parser.
	 * <p>
	 * Note:  Reporters are not copied to new parsers during a clone.
	 */
	public static final String XML_reporter = "XmlParser.reporter";

	/**
	 * <b>Configuration property:</b>  XML resolver.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.resolver"</js>
	 * 	<li><b>Data type:</b> {@link XMLResolver}
	 * 	<li><b>Default:</b> <jk>null</jk>
	 * </ul>
	 * <p>
	 * Associates an {@link XMLResolver} with this parser.
	 */
	public static final String XML_resolver = "XmlParser.resolver";

	/**
	 * <b>Configuration property:</b>  XML event allocator.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.eventAllocator"</js>
	 * 	<li><b>Data type:</b> {@link XMLEventAllocator}
	 * 	<li><b>Default:</b> <jk>null</jk>
	 * </ul>
	 * <p>
	 * Associates an {@link XMLEventAllocator} with this parser.
	 */
	public static final String XML_eventAllocator = "XmlParser.eventAllocator";

	/**
	 * <b>Configuration property:</b>  Preserve root element during generalized parsing.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"XmlParser.preserveRootElement"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>false</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, when parsing into a generic {@link ObjectMap}, the map will
	 * 	contain a single entry whose key is the root element name.
	 *
	 * Example:
	 *	<table class='styled'>
	 *		<tr>
	 *			<td>XML</td>
	 *			<td>ObjectMap.toString(), preserveRootElement==false</td>
	 *			<td>ObjectMap.toString(), preserveRootElement==true</td>
	 *		</tr>
	 *		<tr>
	 *			<td><code><xt>&lt;root&gt;&lt;a&gt;</xt>foobar<xt>&lt;/a&gt;&lt;/root&gt;</xt><code></td>
	 *			<td><code>{ a:<js>'foobar'</js> }</code></td>
	 *			<td><code>{ root: { a:<js>'foobar'</js> }}</code></td>
	 *		</tr>
	 *	</table>
	 *
	 */
	public static final String XML_preserveRootElement = "XmlParser.preserveRootElement";

	final String xsiNs;
	final boolean
		trimWhitespace,
		validating,
		coalescing,
		replaceEntityReferences,
		preserveRootElement;
	final XMLReporter reporter;
	final XMLResolver resolver;
	final XMLEventAllocator eventAllocator;

	/**
	 * Constructor.
	 * <p>
	 * Typically only called from {@link ContextFactory#getContext(Class)}.
	 *
	 * @param cf The factory that created this context.
	 */
	public XmlParserContext(ContextFactory cf) {
		super(cf);
		xsiNs = cf.getProperty(XML_xsiNs, String.class, "http://www.w3.org/2001/XMLSchema-instance");
		trimWhitespace = cf.getProperty(XML_trimWhitespace, boolean.class, true);
		validating = cf.getProperty(XML_validating, boolean.class, false);
		coalescing = cf.getProperty(XML_coalescing, boolean.class, false);
		replaceEntityReferences = cf.getProperty(XML_replaceEntityReferences, boolean.class, true);
		preserveRootElement = cf.getProperty(XML_preserveRootElement, boolean.class, false);
		reporter = cf.getProperty(XML_reporter, XMLReporter.class, null);
		resolver = cf.getProperty(XML_resolver, XMLResolver.class, null);
		eventAllocator = cf.getProperty(XML_eventAllocator, XMLEventAllocator.class, null);
	}
}
