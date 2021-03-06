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
package org.apache.juneau.transform;

import java.beans.*;
import java.util.*;

import org.apache.juneau.*;

/**
 * Builder class for {@link BeanFilter} objects.
 * <p>
 * 	Bean filter builders must have a public no-arg constructor.
 * 	Builder settings should be set in the constructor using the provided setters on this class.
 *
 * <h6 class='topic'>Example:</h6>
 * <p class='bcode'>
 * 	<jc>// Create our serializer with a bean filter.</jc>
 * 	WriterSerializer s = <jk>new</jk> JsonSerializer().addBeanFilters(AddressFilter.<jk>class</jk>);
 *
 * 	Address a = <jk>new</jk> Address();
 * 	String json = s.serialize(a); <jc>// Serializes only street, city, state.</jc>
 *
 * 	<jc>// Filter class defined via setters</jc>
 * 	<jk>public class</jk> AddressFilter <jk>extends</jk> BeanFilterBuilder {
 * 		<jk>public</jk> AddressFilter() {
 * 			super(Address.<jk>class</jk>);
 * 			setProperties(<js>"street"</js>,<js>"city"</js>,<js>"state"</js>);
 * 		}
 * 	}
 * </p>
 *
 * <h6 class='topic'>Additional information</h6>
 * 	See {@link org.apache.juneau.transform} for more information.
 *
 * @author james.bognar
 */
public abstract class BeanFilterBuilder {

	Class<?> beanClass;
	String typeName;
	String[] properties, excludeProperties;
	Class<?> interfaceClass, stopClass;
	boolean sortProperties;
	PropertyNamer propertyNamer;
	String subTypeProperty;
	Map<Class<?>,String> subTypes;

	/**
	 * Constructor.
	 *
	 * @param beanClass The bean class that this filter applies to.
	 */
	public BeanFilterBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Specifies the type name for this bean.
	 *
	 * @param typeName The dictionary name associated with this bean.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setTypeName(String typeName) {
		this.typeName = typeName;
		return this;
	}

	/**
	 * Specifies the set and order of names of properties associated with the bean class.
	 * The order specified is the same order that the entries will be returned by the {@link BeanMap#entrySet()} and related methods.
	 * Entries in the list can also contain comma-delimited lists that will be split.
	 *
	 * @param properties The properties associated with the bean class.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setProperties(String...properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Specifies the list of properties to ignore on a bean.
	 *
	 * @param excludeProperties The list of properties to ignore on a bean.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setExcludeProperties(String...excludeProperties) {
		this.excludeProperties = excludeProperties;
		return this;
	}

	/**
	 * Identifies a class to be used as the interface class for this and all subclasses.
	 * <p>
	 * When specified, only the list of properties defined on the interface class will be used during serialization.
	 * Additional properties on subclasses will be ignored.
	 * <p class='bcode'>
	 * 	<jc>// Parent class</jc>
	 * 	<jk>public abstract class</jk> A {
	 * 		<jk>public</jk> String <jf>f0</jf> = <js>"f0"</js>;
	 * 	}
	 *
	 * 	<jc>// Sub class</jc>
	 * 	<jk>public class</jk> A1 <jk>extends</jk> A {
	 * 		<jk>public</jk> String <jf>f1</jf> = <js>"f1"</js>;
	 * 	}
	 *
	 * 	<jc>// Filter class</jc>
	 * 	<jk>public class</jk> AFilter <jk>extends</jk> BeanFilterBuilder {
	 * 		<jk>public</jk> AFilter() {
	 * 			super(A.<jk>class</jk>);
	 * 			setInterfaceClass(A.<jk>class</jk>);
	 * 		}
	 * 	}
	 *
	 * 	JsonSerializer s = new JsonSerializer().addBeanFilters(AFilter.<jk>class</jk>);
	 * 	A1 a1 = <jk>new</jk> A1();
	 * 	String r = s.serialize(a1);
	 * 	<jsm>assertEquals</jsm>(<js>"{f0:'f0'}"</js>, r);  <jc>// Note f1 is not serialized</jc>
	 * </p>
	 *	<p>
	 * Note that this filter can be used on the parent class so that it filters to all child classes,
	 * 	or can be set individually on the child classes.
	 *
	 * @param interfaceClass The interface class to use for this bean class.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
		return this;
	}

	/**
	 * Identifies a stop class for this class and all subclasses.
	 * <p>
	 * Identical in purpose to the stop class specified by {@link Introspector#getBeanInfo(Class, Class)}.
	 * Any properties in the stop class or in its base classes will be ignored during analysis.
	 * <p>
	 * For example, in the following class hierarchy, instances of <code>C3</code> will include property <code>p3</code>, but
	 * 	not <code>p1</code> or <code>p2</code>.
	 * <p class='bcode'>
	 * 	<jk>public class</jk> C1 {
	 * 		<jk>public int</jk> getP1();
	 * 	}
	 *
	 * 	<jk>public class</jk> C2 <jk>extends</jk> C1 {
	 * 		<jk>public int</jk> getP2();
	 * 	}
	 *
	 * 	<ja>@Bean</ja>(stopClass=C2.<jk>class</jk>)
	 * 	<jk>public class</jk> C3 <jk>extends</jk> C2 {
	 * 		<jk>public int</jk> getP3();
	 * 	}
	 * </p>
	 *
	 * @param stopClass
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setStopClass(Class<?> stopClass) {
		this.stopClass = stopClass;
		return this;
	}

	/**
	 * Sort properties in alphabetical order.
	 *
	 * @param sortProperties
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setSortProperties(boolean sortProperties) {
		this.sortProperties = sortProperties;
		return this;
	}

	/**
	 * The property namer to use to name bean properties.
	 *
	 * @param propertyNamer The property namer instance.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setPropertyNamer(PropertyNamer propertyNamer) {
		this.propertyNamer = propertyNamer;
		return this;
	}

	/**
	 * The property namer to use to name bean properties.
	 *
	 * @param c The property namer class.  Must have a public no-arg constructor.
	 * @return This object (for method chaining).
	 * @throws Exception Thrown from constructor method.
	 */
	public BeanFilterBuilder setPropertyNamer(Class<? extends PropertyNamer> c) throws Exception {
		this.propertyNamer = c.newInstance();
		return this;
	}

	/**
	 * Defines a virtual property on a superclass that identifies bean subtype classes.
	 * <p>
	 * In the following example, the abstract class has two subclasses that are differentiated
	 * 	by a property called <code>subType</code>
	 *
	 * <p class='bcode'>
	 * 	<jc>// Abstract superclass</jc>
	 * 	<jk>public abstract class</jk> A {
	 * 		<jk>public</jk> String <jf>f0</jf> = <js>"f0"</js>;
	 * 	}
	 *
	 * 	<jc>// Subclass 1</jc>
	 * 	<jk>public class</jk> A1 <jk>extends</jk> A {
	 * 		<jk>public</jk> String <jf>f1</jf>;
	 * 	}
	 *
	 * 	<jc>// Subclass 2</jc>
	 * 	<jk>public class</jk> A2 <jk>extends</jk> A {
	 * 		<jk>public</jk> String <jf>f2</jf>;
	 * 	}
	 *
	 * 	<jc>// Filter for defining subtypes</jc>
	 * 	<jk>public class</jk> AFilter <jk>extends</jk> BeanFilterBuilder {
	 * 		<jk>public</jk> AFilter() {
	 * 			super(A.<jk>class</jk>);
	 * 			setSubTypeProperty(<js>"subType"</js>);
	 *				addSubType("A1", A1.<jk>class</jk>);
	 *				addSubType("A2", A2.<jk>class</jk>);
	 * 		}
	 * 	}
	 * </p>
	 * <p>
	 * The following shows what happens when serializing a subclassed object to JSON:
	 * <p class='bcode'>
	 * 	JsonSerializer s = <jk>new</jk> JsonSerializer().addBeanFilters(AFilter.<jk>class</jk>);
	 * 	A1 a1 = <jk>new</jk> A1();
	 * 	a1.<jf>f1</jf> = <js>"f1"</js>;
	 * 	String r = s.serialize(a1);
	 * 	<jsm>assertEquals</jsm>(<js>"{subType:'A1',f1:'f1',f0:'f0'}"</js>, r);
	 *	</p>
	 * <p>
	 * The following shows what happens when parsing back into the original object.
	 * <p class='bcode'>
	 * 	JsonParser p = <jk>new</jk> JsonParser().addBeanFilters(AFilter.<jk>class</jk>);
	 * 	A a = p.parse(r, A.<jk>class</jk>);
	 * 	<jsm>assertTrue</jsm>(a <jk>instanceof</jk> A1);
	 * </p>
	 *
	 * @param subTypeProperty The name of the subtype property for this bean.
	 * 	Default is <js>"_subtype"</js>.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setSubTypeProperty(String subTypeProperty) {
		this.subTypeProperty = subTypeProperty;
		return this;
	}

	/**
	 * Specifies the subtype mappings for this bean class.
	 * See {@link #setSubTypeProperty(String)}.
	 *
	 * @param subTypes The mappings of subtype classes to subtype names.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder setSubTypes(Map<Class<?>,String> subTypes) {
		this.subTypes = subTypes;
		return this;
	}

	/**
	 * Adds an entry to the subtype mappings for this bean class.
	 * See {@link #setSubTypeProperty(String)}.
	 *
	 * @param name The subtype name.
	 * @param c The subtype class.
	 * @return This object (for method chaining).
	 */
	public BeanFilterBuilder addSubType(String name, Class<?> c) {
		if (subTypes == null)
			subTypes = new LinkedHashMap<Class<?>,String>();
		subTypes.put(c, name);
		return this;
	}

	/**
	 * Creates a {@link BeanFilter} with settings in this builder class.
	 *
	 * @return A new {@link BeanFilter} instance.
	 */
	public BeanFilter build() {
		return new BeanFilter(this);
	}
}
