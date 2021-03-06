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
package org.apache.juneau.transforms;

import javax.xml.datatype.*;

import org.apache.juneau.internal.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.transform.*;

/**
 * Transforms {@link XMLGregorianCalendar XMLGregorianCalendars} to ISO8601 date-time {@link String Strings}.
 * <p>
 * 	Objects are converted to strings using {@link XMLGregorianCalendar#toXMLFormat()}.
 * <p>
 * 	Strings are converted to objects using {@link DatatypeFactory#newXMLGregorianCalendar(String)}.
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class XMLGregorianCalendarSwap extends PojoSwap<XMLGregorianCalendar,String> {

	private DatatypeFactory dtf;

	/**
	 * Constructor.
	 */
	public XMLGregorianCalendarSwap() {
		try {
			this.dtf = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts the specified <code>XMLGregorianCalendar</code> to a {@link String}.
	 */
	@Override /* PojoSwap */
	public String swap(XMLGregorianCalendar b) throws SerializeException {
		return b.toXMLFormat();
	}

	/**
	 * Converts the specified {@link String} to an <code>XMLGregorianCalendar</code>.
	 */
	@Override /* PojoSwap */
	public XMLGregorianCalendar unswap(String s) throws ParseException {
		if (StringUtils.isEmpty(s))
			return null;
		return dtf.newXMLGregorianCalendar(s);
	}
}