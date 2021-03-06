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

import java.io.*;

import org.apache.juneau.serializer.*;

/**
 * Specialized writer for serializing XML.
 * <p>
 * 	<b>Note:  This class is not intended for external use.</b>
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class XmlWriter extends SerializerWriter {

	private String defaultNsPrefix;
	private boolean enableNs;

	/**
	 * Constructor.
	 *
	 * @param out The wrapped writer.
	 * @param useIndentation If <jk>true</jk> XML elements will be indented.
	 * @param trimStrings If <jk>true</jk>, strings should be trimmed before they're serialized.
	 * @param quoteChar The quote character to use for attributes.  Should be <js>'\''</js> or <js>'"'</js>.
	 * @param relativeUriBase The base (e.g. <js>https://localhost:9443/contextPath"</js>) for relative URIs (e.g. <js>"my/path"</js>).
	 * @param absolutePathUriBase The base (e.g. <js>https://localhost:9443"</js>) for relative URIs with absolute paths (e.g. <js>"/contextPath/my/path"</js>).
	 * @param enableNs Flag to indicate if XML namespaces are enabled.
	 * @param defaultNamespace The default namespace if XML namespaces are enabled.
	 */
	public XmlWriter(Writer out, boolean useIndentation, boolean trimStrings, char quoteChar, String relativeUriBase, String absolutePathUriBase, boolean enableNs, Namespace defaultNamespace) {
		super(out, useIndentation, true, trimStrings, quoteChar, relativeUriBase, absolutePathUriBase);
		this.enableNs = enableNs;
		this.defaultNsPrefix = defaultNamespace == null ? null : defaultNamespace.name;
	}

	/**
	 * Writes an opening tag to the output:  <code><xt>&lt;ns:name</xt></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oTag(String ns, String name, boolean needsEncoding) throws IOException {
		append('<');
		if (enableNs && ns != null && ! (ns.isEmpty() || ns.equals(defaultNsPrefix)))
			append(ns).append(':');
		if (needsEncoding)
			encodeElement(name);
		else
			append(name);
		return this;
	}

	/**
	 * Shortcut for <code>oTag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oTag(String ns, String name) throws IOException {
		return oTag(ns, name, false);
	}

	/**
	 * Shortcut for <code>oTag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oTag(String name) throws IOException {
		return oTag(null, name, false);
	}

	/**
	 * Shortcut for <code>i(indent).oTag(ns, name, needsEncoding);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oTag(int indent, String ns, String name, boolean needsEncoding) throws IOException {
		return i(indent).oTag(ns, name, needsEncoding);
	}

	/**
	 * Shortcut for <code>i(indent).oTag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oTag(int indent, String ns, String name) throws IOException {
		return i(indent).oTag(ns, name, false);
	}

	/**
	 * Shortcut for <code>i(indent).oTag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oTag(int indent, String name) throws IOException {
		return i(indent).oTag(null, name, false);
	}

	/**
	 * Closes a tag.
	 * Shortcut for <code>append(<js>'>'</js>);</code>
	 *
	 * @return This object (for method chaining).
	 * @throws IOException
	 */
	public XmlWriter cTag() throws IOException {
		append('>');
		return this;
	}

	/**
	 * Closes an empty tag.
	 * Shortcut for <code>append(<js>'/'</js>).append(<js>'>'</js>);</code>
	 *
	 * @return This object (for method chaining).
	 * @throws IOException
	 */
	public XmlWriter ceTag() throws IOException {
		append('/').append('>');
		return this;
	}

	/**
	 * Writes a closed tag to the output:  <code><xt>&lt;ns:name/&gt;</xt></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter tag(String ns, String name, boolean needsEncoding) throws IOException {
		append('<');
		if (enableNs && ns != null && ! (ns.isEmpty() || ns.equals(defaultNsPrefix)))
			append(ns).append(':');
		if (needsEncoding)
			encodeElement(name);
		else
			append(name);
		return append('/').append('>');
	}

	/**
	 * Shortcut for <code>tag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter tag(String ns, String name) throws IOException {
		return tag(ns, name, false);
	}

	/**
	 * Shortcut for <code>tag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter tag(String name) throws IOException {
		return tag(null, name, false);
	}

	/**
	 * Shortcut for <code>i(indent).tag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter tag(int indent, String name) throws IOException {
		return i(indent).tag(name);
	}

	/**
	 * Shortcut for <code>i(indent).tag(ns, name, needsEncoding);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter tag(int indent, String ns, String name, boolean needsEncoding) throws IOException {
		return i(indent).tag(ns, name, needsEncoding);
	}

	/**
	 * Shortcut for <code>i(indent).tag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter tag(int indent, String ns, String name) throws IOException {
		return i(indent).tag(ns, name);
	}


	/**
	 * Writes a start tag to the output:  <code><xt>&lt;ns:name&gt;</xt></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter sTag(String ns, String name, boolean needsEncoding) throws IOException {
		return oTag(ns, name, needsEncoding).append('>');
	}

	/**
	 * Shortcut for <code>sTag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter sTag(String ns, String name) throws IOException {
		return sTag(ns, name, false);
	}

	/**
	 * Shortcut for <code>sTag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter sTag(String name) throws IOException {
		return sTag(null, name);
	}

	/**
	 * Shortcut for <code>i(indent).sTag(ns, name, needsEncoding);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter sTag(int indent, String ns, String name, boolean needsEncoding) throws IOException {
		return i(indent).sTag(ns, name, needsEncoding);
	}

	/**
	 * Shortcut for <code>i(indent).sTag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter sTag(int indent, String ns, String name) throws IOException {
		return i(indent).sTag(ns, name, false);
	}

	/**
	 * Shortcut for <code>i(indent).sTag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter sTag(int indent, String name) throws IOException {
		return i(indent).sTag(null, name, false);
	}


	/**
	 * Writes an end tag to the output:  <code><xt>&lt;/ns:name&gt;</xt></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter eTag(String ns, String name, boolean needsEncoding) throws IOException {
		append('<').append('/');
		if (enableNs && ns != null && ! (ns.isEmpty() || ns.equals(defaultNsPrefix)))
			append(ns).append(':');
		if (needsEncoding)
			encodeElement(name);
		else
			append(name);
		return append('>');
	}

	/**
	 * Shortcut for <code>eTag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter eTag(String ns, String name) throws IOException {
		return eTag(ns, name, false);
	}

	/**
	 * Shortcut for <code>eTag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter eTag(String name) throws IOException {
		return eTag(null, name);
	}

	/**
	 * Shortcut for <code>i(indent).eTag(ns, name, needsEncoding);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @param needsEncoding If <jk>true</jk>, element name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter eTag(int indent, String ns, String name, boolean needsEncoding) throws IOException {
		return i(indent).eTag(ns, name, needsEncoding);
	}

	/**
	 * Shortcut for <code>i(indent).eTag(ns, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter eTag(int indent, String ns, String name) throws IOException {
		return i(indent).eTag(ns, name, false);
	}

	/**
	 * Shortcut for <code>i(indent).eTag(<jk>null</jk>, name, <jk>false</jk>);</code>
	 *
	 * @param indent The number of prefix tabs to add.
	 * @param name The element name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter eTag(int indent, String name) throws IOException {
		return i(indent).eTag(name);
	}

	/**
	 * Writes an attribute to the output:  <code><xa>ns:name</xa>=<xs>'value'</xs></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @param needsEncoding If <jk>true</jk>, attribute name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attr(String ns, String name, Object value, boolean needsEncoding) throws IOException {
		oAttr(ns, name).q();
		if (needsEncoding)
			encodeAttr(value);
		else
			append(value);
		return q();
	}

	/**
	 * Shortcut for <code>attr(<jk>null</jk>, name, value, <jk>false</jk>);</code>
	 *
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @param needsEncoding If <jk>true</jk>, attribute name will be encoded.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attr(String name, Object value, boolean needsEncoding) throws IOException {
		return attr(null, name, value, needsEncoding);
	}

	/**
	 * Shortcut for <code>attr(ns, name, value, <jk>false</jk>);</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attr(String ns, String name, Object value) throws IOException {
		return oAttr(ns, name).q().append(value).q();
	}

	/**
	 * Same as {@link #attr(String, Object, boolean)}, except pass in a {@link Namespace} object for the namespace.
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attr(Namespace ns, String name, Object value) throws IOException {
		return oAttr(ns == null ? null : ns.name, name).q().append(value).q();
	}

	/**
	 * Shortcut for <code>attr(<jk>null</jk>, name, value, <jk>false</jk>);</code>
	 *
	 * @param name The attribute name.
	 * @param value The attribute value.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attr(String name, Object value) throws IOException {
		return attr((String)null, name, value);
	}


	/**
	 * Writes an open-ended attribute to the output:  <code><xa>ns:name</xa>=</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oAttr(String ns, String name) throws IOException {
		append(' ');
		if (enableNs && ns != null && ! (ns.isEmpty() || ns.equals(defaultNsPrefix)))
			append(ns).append(':');
		append(name).append('=');
		return this;
	}

	/**
	 * Writes an open-ended attribute to the output:  <code><xa>ns:name</xa>=</code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter oAttr(Namespace ns, String name) throws IOException {
		return oAttr(ns == null ? null : ns.name, name);
	}

	/**
	 * Writes an attribute with a URI value to the output:  <code><xa>ns:name</xa>=<xs>'uri-value'</xs></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @param value The attribute value, convertable to a URI via <code>toString()</code>
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attrUri(Namespace ns, String name, Object value) throws IOException {
		oAttr(ns, name).q().appendUri(value).q();
		return this;
	}

	/**
	 * Writes an attribute with a URI value to the output:  <code><xa>ns:name</xa>=<xs>'uri-value'</xs></code>
	 *
	 * @param ns The namespace.  Can be <jk>null</jk>.
	 * @param name The attribute name.
	 * @param value The attribute value, convertable to a URI via <code>toString()</code>
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter attrUri(String ns, String name, Object value) throws IOException {
		oAttr(ns, name).q().appendUri(value).q();
		return this;
	}

	/**
	 * Serializes and encodes the specified object as valid XML text.
	 *
	 * @param o The object being serialized.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter encodeText(Object o) throws IOException {
		XmlUtils.encodeText(this, o);
		return this;
	}

	/**
	 * Serializes and encodes the specified object as valid XML text.
	 * <p>
	 * 	Does NOT encode XML characters (<js>'&lt;'</js>, <js>'&gt;'</js>, and <js>'&amp;'</js>).
	 * <p>
	 * 	Use on XML text that you just want to replace invalid XML characters with <js>"_x####_"</js> sequences.
	 *
	 * @param o The object being serialized.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter encodeTextInvalidChars(Object o) throws IOException {
		XmlUtils.encodeTextInvalidChars(this, o);
		return this;
	}

	/**
	 * Serializes and encodes the specified object as valid XML text.
	 * <p>
	 * 	Only encodes XML characters (<js>'&lt;'</js>, <js>'&gt;'</js>, and <js>'&amp;'</js>).
	 * <p>
	 * 	Use on XML text where the invalid characters have already been replaced.
	 *
	 * @param o The object being serialized.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter encodeTextXmlChars(Object o) throws IOException {
		XmlUtils.encodeTextXmlChars(this, o);
		return this;
	}

	/**
	 * Serializes and encodes the specified object as valid XML attribute name.
	 *
	 * @param o The object being serialized.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter encodeAttr(Object o) throws IOException {
		XmlUtils.encodeAttr(out, o);
		return this;
	}

	/**
	 * Serializes and encodes the specified object as valid XML element name.
	 *
	 * @param o The object being serialized.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred.
	 */
	public XmlWriter encodeElement(Object o) throws IOException {
		XmlUtils.encodeElementName(out, o);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter cr(int depth) throws IOException {
		super.cr(depth);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter appendln(int indent, String text) throws IOException {
		super.appendln(indent, text);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter appendln(String text) throws IOException {
		super.appendln(text);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter append(int indent, String text) throws IOException {
		super.append(indent, text);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter append(int indent, char c) throws IOException {
		super.append(indent, c);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter s() throws IOException {
		super.s();
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter q() throws IOException {
		super.q();
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter i(int indent) throws IOException {
		super.i(indent);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter nl() throws IOException {
		super.nl();
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter append(Object text) throws IOException {
		super.append(text);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter append(String text) throws IOException {
		super.append(text);
		return this;
	}

	@Override /* SerializerWriter */
	public XmlWriter append(char c) throws IOException {
		out.write(c);
		return this;
	}

	@Override /* Object */
	public String toString() {
		return out.toString();
	}
}
