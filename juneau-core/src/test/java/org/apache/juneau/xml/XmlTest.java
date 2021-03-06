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

import static org.apache.juneau.TestUtils.*;
import static org.apache.juneau.serializer.SerializerContext.*;
import static org.apache.juneau.xml.XmlSerializerContext.*;
import static org.apache.juneau.xml.annotation.XmlFormat.*;
import static org.junit.Assert.*;

import java.net.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.jena.annotation.*;
import org.apache.juneau.json.*;
import org.apache.juneau.xml.annotation.*;
import org.apache.juneau.xml.xml1a.*;
import org.apache.juneau.xml.xml1b.*;
import org.apache.juneau.xml.xml1c.*;
import org.junit.*;

@SuppressWarnings({"serial","javadoc"})
public class XmlTest {

	//====================================================================================================
	// Simple comparison test with JSON serializer
	//====================================================================================================
	@Test
	public void testComparisonWithJson() throws Exception {
		String json1 = readFile(getClass().getResource("/xml/testComparisonWithJson.json").getPath());
		String xml1 = readFile(getClass().getResource("/xml/testComparisonWithJson.xml").getPath());

		ObjectMap m = (ObjectMap) XmlParser.DEFAULT.parse(xml1, Object.class);
		String json2 = new JsonSerializer.SimpleReadable().setProperty(SERIALIZER_quoteChar, '"').setProperty(SERIALIZER_trimNullProperties, false).serialize(m);
		assertEquals(json1, json2);

		m = (ObjectMap) JsonParser.DEFAULT.parse(json1, Object.class);
		String xml2 = new XmlSerializer.SimpleXmlJsonSq().setProperty(SERIALIZER_useIndentation, true).setProperty(SERIALIZER_trimNullProperties, false).serialize(m);
		assertEquals(xml1, xml2);
	}

	//====================================================================================================
	// Test namespacing
	//====================================================================================================
	@Test
	public void testNamespaces() throws Exception {
		String e = readFile(getClass().getResource("/xml/testNamespaces.xml").getPath());
		ObjectMap m = (ObjectMap) JsonParser.DEFAULT.parse(readFile(getClass().getResource("/xml/testComparisonWithJson.json").getPath()), Object.class);
		String r = new XmlSerializer.XmlJsonSq()
			.setProperty(XML_addNamespaceUrisToRoot, true)
			.setProperty(XML_defaultNamespaceUri, "http://www.apache.org")
			.setProperty(SERIALIZER_useIndentation, true)
			.setProperty(SERIALIZER_trimNullProperties, false)
			.serialize(m);
		assertEquals(e, r);
	}

	//====================================================================================================
	// Test bean name annotation
	//====================================================================================================
	@Test
	public void testBeanNameAnnotation() throws Exception {
		String e =
			  "<Person1 _type='object'>\n"
			+ "	<name>John Smith</name>\n"
			+ "	<age _type='number'>123</age>\n"
			+ "</Person1>\n";
		String r = new XmlSerializer.SimpleXmlJsonSq().setProperty(SERIALIZER_useIndentation, true).serialize(new Person1("John Smith", 123));
		assertEquals(e, r);
	}

	/** Class with explicitly specified properties */
	@Bean(typeName="Person1", properties="name,age")
	public static class Person1 {
		public int age;
		private String name;
		protected Person1(String name, int age) {
			this.name = name;
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

	//====================================================================================================
	// Test trimNulls property.
	//====================================================================================================
	@Test
	public void testTrimNulls() throws Exception {
		String e =
			  "<Person1 _type='object'>\n"
			+ "	<age _type='number'>123</age>\n"
			+ "</Person1>\n";
		String r = new XmlSerializer.SimpleXmlJsonSq().setProperty(SERIALIZER_useIndentation, true).serialize(new Person1(null, 123));
		assertEquals(e, r);
	}

	//====================================================================================================
	// Element name.
	//====================================================================================================
	@Test
	public void testElementName() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		A t = new A();
		String r = s.serialize(t);
		assertEquals("<foo><f1>1</f1></foo>", r);
		validateXml(t);
	}

	@Bean(typeName="foo")
	public static class A {
		public int f1 = 1;
	}

	//====================================================================================================
	// Element name on superclass.
	//====================================================================================================
	@Test
	public void testElementNameOnSuperclass() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		B2 t = new B2();
		String r = s.serialize(t);
		assertEquals("<foo><f1>1</f1></foo>", r);
		validateXml(t);
	}

	public static class B1 extends A {}
	public static class B2 extends B1 {}

	//====================================================================================================
	// Element name on interface.
	//====================================================================================================
	@Test
	public void testElementNameOnInterface() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		C3 t = new C3();
		String r = s.serialize(t);
		assertEquals("<foo><f1>1</f1></foo>", r);
		validateXml(t);
	}

	@Bean(typeName="foo")
	public static interface C1 {}
	public static class C2 implements C1 {}
	public static class C3 extends C2 {
		public int f1 = 1;
	}

	//====================================================================================================
	// Element name with invalid XML characters.
	//====================================================================================================
	@Test
	public void testElementNameWithInvalidChars() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		XmlParser p = XmlParser.DEFAULT;
		D t = new D();
		String r = s.serialize(t);
		assertEquals("<_x007E__x0021__x0040__x0023__x0024__x0025__x005E__x0026__x002A__x0028__x0029___x002B__x0060_-_x003D__x007B__x007D__x007C__x005B__x005D__x005C__x003A__x0022__x003B__x0027__x003C__x003E__x003F__x002C_._x000A__x000D__x0009__x0008_><f1>1</f1></_x007E__x0021__x0040__x0023__x0024__x0025__x005E__x0026__x002A__x0028__x0029___x002B__x0060_-_x003D__x007B__x007D__x007C__x005B__x005D__x005C__x003A__x0022__x003B__x0027__x003C__x003E__x003F__x002C_._x000A__x000D__x0009__x0008_>", r);
		t = p.parse(r, D.class);
		validateXml(t);
	}

	@Bean(typeName="~!@#$%^&*()_+`-={}|[]\\:\";'<>?,.\n\r\t\b")
	public static class D {
		public int f1 = 1;
	}

	//====================================================================================================
	// Field of type collection with element name.
	// Element name should be ignored.
	//====================================================================================================
	@Test
	public void testIgnoreCollectionFieldWithElementName() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		XmlParser p = XmlParser.DEFAULT;
		G t = new G();
		t.f1.add("bar");
		String r = s.serialize(t);
		assertEquals("<bar><f1><string>bar</string></f1></bar>", r);
		t = p.parse(r, G.class);
		validateXml(t);
	}

	@Bean(typeName="foo")
	public static class F extends LinkedList<String>{}

	@Bean(typeName="bar")
	public static class G {
		public F f1 = new F();
	}

	//====================================================================================================
	// Element name on beans of a collection.
	//====================================================================================================
	@Test
	public void testElementNameOnBeansOfCollection() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		Object o = new J1();
		String r = s.serialize(o);
		assertEquals("<foo><f1><bar><f2>2</f2></bar></f1></foo>", r);
	}

	@Bean(typeName="foo")
	public static class J1 {
		@BeanProperty(properties="f2") public List<J2> f1 = new LinkedList<J2>() {{
			add(new J2());
		}};}

	@Bean(typeName="bar")
	public static class J2 {
		public int f2 = 2;
		public int f3 = 3;
	}

	//====================================================================================================
	// @Xml.ns without matching nsUri.
	//====================================================================================================
	@Test
	public void testXmlNsWithoutMatchingNsUri() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		K t = new K();
		try {
			s.serialize(t);
			fail("Exception not thrown");
		} catch (Exception e) {
			assertEquals("Found @Xml.prefix annotation with no matching URI.  prefix='foo'", e.getLocalizedMessage());
		}
	}

	@Xml(prefix="foo")
	public static class K {
		public int f1;
	}

	//====================================================================================================
	// @Xml.format=ATTR.
	//====================================================================================================
	@Test
	public void testXmlFormatAttr() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		XmlParser p = XmlParser.DEFAULT;
		L t = new L();
		String r = s.serialize(t);
		assertEquals("<object f2='2'><f1>1</f1><f3>3</f3></object>", r);
		t.f1 = 4; t.f2 = 5; t.f3 = 6;
		t = p.parse(s.serialize(t), L.class);
		assertEquals(4, t.f1);
		assertEquals(5, t.f2);
		assertEquals(6, t.f3);
		validateXml(t);
	}

	public static class L {
		public int f1 = 1;
		@Xml(format=ATTR)
		public int f2 = 2;
		public int f3 = 3;
	}

	//====================================================================================================
	// @Xml.format=ATTR with namespaces.
	//====================================================================================================
	@Test
	public void testXmlFormatAttrWithNs() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq();
		XmlParser p = XmlParser.DEFAULT;
		M t = new M();
		String r = null;
		r = s.serialize(t);
		assertEquals("<object f1='1' f2='2' f3='3'/>", r);
		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, true).setProperty(XML_autoDetectNamespaces, true).setProperty(SERIALIZER_trimNullProperties, false);
		t.f1 = 4; t.f2 = 5; t.f3 = 6;
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:bar='http://bar' xmlns:foo='http://foo' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' bar:f1='4' foo:f2='5' baz:f3='6'/>", r);
		t = p.parse(r, M.class);
		assertEquals(4, t.f1);
		assertEquals(5, t.f2);
		assertEquals(6, t.f3);
		validateXml(t, s);
	}

	@Xml(prefix="bar", namespace="http://bar")
	public static class M {
		@Xml(format=ATTR)
		public int f1 = 1;
		@Xml(prefix="foo", format=ATTR, namespace="http://foo")
		public int f2 = 2;
		@Xml(prefix="baz", namespace="http://baz", format=ATTR)
		public int f3 = 3;
	}

	//====================================================================================================
	// _xXXXX_ notation.
	//====================================================================================================
	@Test
	public void testXXXXNotation() throws Exception {
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;
		XmlParser p = XmlParser.DEFAULT;
		String in, r;

		in = "\u0001";
		r = s.serialize(in);
		assertEquals("<string>_x0001_</string>", r);
		in = p.parse(r, String.class);
		assertEquals("\u0001", in);

		in = "_x0001_";
		r = s.serialize(in);
		assertEquals("<string>_x005F_x0001_</string>", r);
		in = p.parse(r, String.class);
		assertEquals("_x0001_", in);

		in = "_x001_";
		r = s.serialize(in);
		assertEquals("<string>_x001_</string>", r);
		in = p.parse(r, String.class);
		assertEquals("_x001_", in);

		in = "_x00001_";
		r = s.serialize(in);
		assertEquals("<string>_x00001_</string>", r);
		in = p.parse(r, String.class);
		assertEquals("_x00001_", in);

		in = "_xx001_";
		r = s.serialize(in);
		assertEquals("<string>_xx001_</string>", r);
		in = p.parse(r, String.class);
		assertEquals("_xx001_", in);
	}

	//====================================================================================================
	// @Bean.uri annotation formatted as element
	//====================================================================================================
	@Test
	public void testBeanUriAnnotationFormattedAsElement() throws Exception {
		XmlParser p = XmlParser.DEFAULT;
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;

		N t = new N("http://foo",123, "bar");
		String r = s.serialize(t);
		assertEquals("<object><url>http://foo</url><id>123</id><name>bar</name></object>", r);

		t = p.parse(r, N.class);
		assertEquals("http://foo", t.url.toString());
		assertEquals(123, t.id);
		assertEquals("bar", t.name);

		validateXml(t, s);
	}

	public static class N {
		@Rdf(beanUri=true) @Xml(format=ELEMENT) public URL url;
		public int id;
		public String name;
		public N() {}
		public N(String url, int id, String name) throws Exception {
			this.url = new URL(url);
			this.id = id;
			this.name = name;
		}
	}

	//====================================================================================================
	// @Bean.uri as elements, overridden element names
	//====================================================================================================
	@Test
	public void testOverriddenBeanUriAsElementNames() throws Exception {
		XmlParser p = XmlParser.DEFAULT;
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;

		O t = new O("http://foo", 123, "bar");
		String r = s.serialize(t);
		assertEquals("<object><url2>http://foo</url2><id2>123</id2><name>bar</name></object>", r);

		t = p.parse(r, O.class);
		assertEquals("http://foo", t.url.toString());
		assertEquals(123, t.id);
		assertEquals("bar", t.name);

		validateXml(t, s);
	}

	public static class O {
		@BeanProperty(name="url2") @Xml(format=ELEMENT) public URL url;
		@BeanProperty(name="id2") public int id;
		public String name;
		public O() {}
		public O(String url, int id, String name) throws Exception {
			this.url = new URL(url);
			this.id = id;
			this.name = name;
		}
	}

	//====================================================================================================
	// @Bean.uri and @Bean.id annotations, overridden attribute names
	//====================================================================================================
	@Test
	public void testOverriddenBeanUriAndIdAnnotations() throws Exception {
		XmlParser p = XmlParser.DEFAULT;
		XmlSerializer s = XmlSerializer.DEFAULT_SIMPLE_SQ;

		P t = new P("http://foo", 123, "bar");
		String r = s.serialize(t);
		assertEquals("<object url2='http://foo' id2='123'><name>bar</name></object>", r);

		t = p.parse(r, P.class);
		assertEquals("http://foo", t.url.toString());
		assertEquals(123, t.id);
		assertEquals("bar", t.name);

		validateXml(t, s);
	}

	public static class P {
		@BeanProperty(name="url2") @Xml(format=ATTR) public URL url;
		@BeanProperty(name="id2") @Xml(format=ATTR) public int id;
		public String name;
		public P() {}
		public P(String url, int id, String name) throws Exception {
			this.url = new URL(url);
			this.id = id;
			this.name = name;
		}
	}

	//====================================================================================================
	// Namespace on class
	//====================================================================================================
	@Test
	public void testNsOnClass() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T1 t = new T1();
		String r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T1.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<object><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T1.class)));
		validateXml(t, s);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T1.class)));

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T1.class)));

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T1.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// Namespace on class with element name.
	//====================================================================================================
	@Test
	public void testNsOnClassWithElementName() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T2 t = new T2();
		String r = s.serialize(t);
		assertEquals("<T2><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T2>", r);
		assertTrue(t.equals(p.parse(r, T2.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<foo:T2><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T2>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<foo:T2 xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T2>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<foo:T2 xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T2>", r);
		assertTrue(t.equals(p.parse(r, T2.class)));
		validateXml(t, s);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<T2><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T2>", r);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<T2><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T2>", r);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<foo:T2 xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T2>", r);
		assertTrue(t.equals(p.parse(r, T2.class)));
		validateXml(t, s);
	}


	//====================================================================================================
	// Namespace on package, no namespace on class.
	//====================================================================================================
	@Test
	public void testNsOnPackageNoNsOnClass() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq();
		XmlParser p = XmlParser.DEFAULT;

		T3 t = new T3();
		String r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T3.class)));
		validateXml(t, s);

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<object><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></object>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true).setProperty(XML_autoDetectNamespaces, false);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></object>", r);

		// Manually set namespaces
		s.setProperty(XML_autoDetectNamespaces, false);
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("p1","http://p1"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:p1='http://p1' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T3.class)));
		validateXml(t, s);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:p1='http://p1' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T3.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// Namespace on package, no namespace on class, element name on class.
	//====================================================================================================
	@Test
	public void testNsOnPackageNoNsOnClassElementNameOnClass() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T4 t = new T4();
		String r = s.serialize(t);
		assertEquals("<T4><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T4>", r);
		assertTrue(t.equals(p.parse(r, T4.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<p1:T4><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></p1:T4>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<p1:T4 xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></p1:T4>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz"),
				NamespaceFactory.get("p1","http://p1")
			}
		);
		r = s.serialize(t);
		assertEquals("<p1:T4 xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:p1='http://p1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></p1:T4>", r);
		assertTrue(t.equals(p.parse(r, T4.class)));
		validateXml(t, s);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<T4><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T4>", r);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<T4><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T4>", r);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<p1:T4 xmlns='http://www.apache.org/2013/Juneau' xmlns:p1='http://p1' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1><bar:f2>2</bar:f2><p1:f3>3</p1:f3><baz:f4>4</baz:f4></p1:T4>", r);
		assertTrue(t.equals(p.parse(r, T4.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// Namespace on package, namespace on class, element name on class.
	//====================================================================================================
	@Test
	public void testNsOnPackageNsOnClassElementNameOnClass() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq();
		XmlParser p = XmlParser.DEFAULT;

		T5 t = new T5();
		String r = s.serialize(t);
		assertEquals("<T5><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T5>", r);
		assertTrue(t.equals(p.parse(r, T5.class)));
		validateXml(t, s);

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false).setProperty(XML_autoDetectNamespaces, false);
		r = s.serialize(t);
		assertEquals("<foo:T5><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T5>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<foo:T5 xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T5>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<foo:T5 xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T5>", r);
		assertTrue(t.equals(p.parse(r, T5.class)));
		validateXml(t, s);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<T5><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T5>", r);
		validateXml(t, s);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<T5><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></T5>", r);
		validateXml(t, s);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<foo:T5 xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></foo:T5>", r);
		assertTrue(t.equals(p.parse(r, T5.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// Namespace on package, namespace on class, no element name on class.
	//====================================================================================================
	@Test
	public void testNsOnPackageNsOnClassNoElementNameOnClass() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T6 t = new T6();
		String r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T6.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<object><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true).setProperty(XML_autoDetectNamespaces, false);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T6.class)));
		validateXml(t, s);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		validateXml(t, s);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		validateXml(t, s);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><foo:f1>1</foo:f1><bar:f2>2</bar:f2><foo:f3>3</foo:f3><baz:f4>4</baz:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T6.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// Combination of namespaces and overridden bean property names.
	//====================================================================================================
	@Test
	public void testComboOfNsAndOverriddenBeanPropertyNames() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T7 t = new T7();
		String r = s.serialize(t);
		assertEquals("<object><g1>1</g1><g2>2</g2><g3>3</g3><g4>4</g4></object>", r);
		assertTrue(t.equals(p.parse(r, T7.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<object><p1:g1>1</p1:g1><bar:g2>2</bar:g2><p1:g3>3</p1:g3><baz:g4>4</baz:g4></object>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true).setProperty(XML_autoDetectNamespaces, false);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:g1>1</p1:g1><bar:g2>2</bar:g2><p1:g3>3</p1:g3><baz:g4>4</baz:g4></object>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz"),
				NamespaceFactory.get("p1","http://p1")
			}
		);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:p1='http://p1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:g1>1</p1:g1><bar:g2>2</bar:g2><p1:g3>3</p1:g3><baz:g4>4</baz:g4></object>", r);
		assertTrue(t.equals(p.parse(r, T7.class)));

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object><g1>1</g1><g2>2</g2><g3>3</g3><g4>4</g4></object>", r);

		s.setProperty(XML_enableNamespaces, false);
		r = s.serialize(t);
		assertEquals("<object><g1>1</g1><g2>2</g2><g3>3</g3><g4>4</g4></object>", r);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:p1='http://p1' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:g1>1</p1:g1><bar:g2>2</bar:g2><p1:g3>3</p1:g3><baz:g4>4</baz:g4></object>", r);
		assertTrue(t.equals(p.parse(r, T7.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// @XmlNs annotation
	//====================================================================================================
	@Test
	public void testXmlNsAnnotation() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T8 t = new T8();
		String r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T8.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, false).setProperty(XML_autoDetectNamespaces, false);
		r = s.serialize(t);
		assertEquals("<object><p2:f1>1</p2:f1><p1:f2>2</p1:f2><c1:f3>3</c1:f3><f1:f4>4</f1:f4></object>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p2:f1>1</p2:f1><p1:f2>2</p1:f2><c1:f3>3</c1:f3><f1:f4>4</f1:f4></object>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p2:f1>1</p2:f1><p1:f2>2</p1:f2><c1:f3>3</c1:f3><f1:f4>4</f1:f4></object>", r);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		assertTrue(t.equals(p.parse(r, T8.class)));
		validateXml(t, s);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1><f2>2</f2><f3>3</f3><f4>4</f4></object>", r);
		validateXml(t, s);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:p2='http://p2' xmlns:p1='http://p1' xmlns:c1='http://c1' xmlns:f1='http://f1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p2:f1>1</p2:f1><p1:f2>2</p1:f2><c1:f3>3</c1:f3><f1:f4>4</f1:f4></object>", r);
		assertTrue(t.equals(p.parse(r, T8.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// @Xml.ns on package, @Xml.nsUri not on package but in @XmlNs.
	//====================================================================================================
	@Test
	public void testXmlNsOnPackageNsUriInXmlNs() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, false);
		XmlParser p = XmlParser.DEFAULT;

		T9 t = new T9();
		String r = s.serialize(t);
		assertEquals("<object><f1>1</f1></object>", r);
		assertTrue(t.equals(p.parse(r, T9.class)));

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_autoDetectNamespaces, false).setProperty(XML_addNamespaceUrisToRoot, false);
		r = s.serialize(t);
		assertEquals("<object><p1:f1>1</p1:f1></object>", r);

		// Add namespace URIs to root, but don't auto-detect.
		// Only xsi should be added to root.
		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1></object>", r);

		// Manually set namespaces
		s.setProperty(XML_namespaces,
			new Namespace[] {
				NamespaceFactory.get("foo","http://foo"),
				NamespaceFactory.get("bar","http://bar"),
				NamespaceFactory.get("baz","http://baz")
			}
		);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:foo='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1></object>", r);

		// Auto-detect namespaces.
		s = new XmlSerializer.SimpleSq().setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1></object>", r);
		assertTrue(t.equals(p.parse(r, T9.class)));
		validateXml(t, s);

		s.setProperty(XML_addNamespaceUrisToRoot, true);
		r = s.serialize(t);
		assertEquals("<object><f1>1</f1></object>", r);
		validateXml(t, s);

		s.setProperty(XML_enableNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:p1='http://p1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><p1:f1>1</p1:f1></object>", r);
		assertTrue(t.equals(p.parse(r, T9.class)));
		validateXml(t, s);
	}

	//====================================================================================================
	// @Xml.format=ATTR
	//====================================================================================================
	@Test
	public void testXmlAttrs() throws Exception {
		XmlSerializer s = new XmlSerializer.SimpleSq();
		XmlParser p = XmlParser.DEFAULT;
		String r;

		Q t = new Q();
		t.f1 = new URL("http://xf1");
		t.f2 = "xf2";
		t.f3 = "xf3";
		r = s.serialize(t);
		assertEquals("<object f1='http://xf1' f2='xf2' x3='xf3'/>", r);
		t = p.parse(r, Q.class);
		assertEquals("http://xf1", t.f1.toString());
		assertEquals("xf2", t.f2);
		assertEquals("xf3", t.f3);

		s.setProperty(XML_enableNamespaces, true).setProperty(XML_addNamespaceUrisToRoot, true).setProperty(XML_autoDetectNamespaces, true);
		r = s.serialize(t);
		assertEquals("<object xmlns='http://www.apache.org/2013/Juneau' xmlns:ns='http://ns' xmlns:nsf1='http://nsf1' xmlns:nsf3='http://nsf3' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' nsf1:f1='http://xf1' ns:f2='xf2' nsf3:x3='xf3'/>", r);
		validateXml(t, s);

		t = p.parse(r, Q.class);
		assertEquals("http://xf1", t.f1.toString());
		assertEquals("xf2", t.f2);
		assertEquals("xf3", t.f3);
	}

	@Xml(prefix="ns", namespace="http://ns")
	public static class Q {

		@Xml(format=ATTR, prefix="nsf1", namespace="http://nsf1")
		public URL f1;

		@Xml(format=ATTR)
		public String f2;

		@BeanProperty(name="x3")
		@Xml(format=ATTR, prefix="nsf3", namespace="http://nsf3")
		public String f3;

		public Q() throws Exception {
			f1 = new URL("http://f1");
			f2 = "f2";
			f3 = "f3";
		}
	}
}