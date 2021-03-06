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
package org.apache.juneau.parser;

import org.apache.juneau.*;
import org.apache.juneau.json.*;

/**
 * Configurable properties common to all parsers.
 *
 *
 * <h6 class='topic' id='ConfigProperties'>Configurable properties common to all parsers</h6>
 * <table class='styled' style='border-collapse: collapse;'>
 * 	<tr><th>Setting name</th><th>Description</th><th>Data type</th><th>Default value</th></tr>
 * 	<tr>
 * 		<td>{@link #PARSER_debug}</td>
 * 		<td>Debug mode.</td>
 * 		<td><code>Boolean</code></td>
 * 		<td><jk>false</jk></td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #PARSER_trimStrings}</td>
 * 		<td>Trim parsed strings.</td>
 * 		<td><code>Boolean</code></td>
 * 		<td><jk>false</jk></td>
 * 	</tr>
 * </table>
 *
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class ParserContext extends Context {

	/**
	 * <b>Configuration property:</b>  Debug mode.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"Parser.debug"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>false</jk>
	 * </ul>
	 * <p>
	 * Enables the following additional information during parsing:
	 * <ul class='spaced-list'>
	 * 	<li>When bean setters throws exceptions, the exception includes the object stack information
	 * 		in order to determine how that method was invoked.
	 * </ul>
	 */
	public static final String PARSER_debug = "Parser.debug";

	/**
	 * <b>Configuration property:</b>  Trim parsed strings.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"Parser.trimStrings"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>false</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, string values will be trimmed of whitespace using {@link String#trim()} before being added to the POJO.
	 */
	public static final String PARSER_trimStrings = "Parser.trimStrings";

	/**
	 * <b>Configuration property:</b>  Strict mode.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"Parser.strict"</js>
	 * 	<li><b>Data type:</b> <code>Boolean</code>
	 * 	<li><b>Default:</b> <jk>false</jk>
	 * </ul>
	 * <p>
	 * If <jk>true</jk>, strict mode for the parser is enabled.
	 * <p>
	 * Strict mode can mean different things for different parsers.
	 * However, all reader-based parsers will
	 * <p>
	 * <table class='styled'>
	 * 	<tr><th>Parser class</th><th>Strict behavior</td></tr>
	 * 	<tr>
	 * 		<td>All reader-based parsers</td>
	 * 		<td>
	 * 			When enabled, throws {@link ParseException ParseExceptions} on malformed charset input.
	 * 			Otherwise, malformed input is ignored.
	 * 		</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>{@link JsonParser}</td>
	 * 		<td>
	 * 			When enabled, throws exceptions on the following invalid JSON syntax:
	 * 			<ul>
	 * 				<li>Unquoted attributes.
	 * 				<li>Missing attribute values.
	 * 				<li>Concatenated strings.
	 * 				<li>Javascript comments.
	 * 				<li>Numbers and booleans when Strings are expected.
	 * 				<li>Numbers valid in Java but not JSON (e.g. octal notation, etc...)
	 * 			</ul>
	 * 		</td>
	 * 	</tr>
	 * </table>
	 */
	public static final String PARSER_strict = "Parser.strict";

	/**
	 * <b>Configuration property:</b>  Input stream charset.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"Parser.inputStreamCharset"</js>
	 * 	<li><b>Data type:</b> <code>String</code>
	 * 	<li><b>Default:</b> <js>"UTF-8"</js>
	 * </ul>
	 * <p>
	 * The character set to use for converting <code>InputStreams</code> and byte arrays to readers.
	 * <p>
	 * Used when passing in input streams and byte arrays to {@link Parser#parse(Object, Class)}.
	 */
	public static final String PARSER_inputStreamCharset = "Parser.inputStreamCharset";

	/**
	 * <b>Configuration property:</b>  File charset.
	 * <p>
	 * <ul>
	 * 	<li><b>Name:</b> <js>"Parser.fileCharset"</js>
	 * 	<li><b>Data type:</b> <code>String</code>
	 * 	<li><b>Default:</b> <js>"default"</js>
	 * </ul>
	 * <p>
	 * The character set to use for reading <code>Files</code> from the file system.
	 * <p>
	 * Used when passing in files to {@link Parser#parse(Object, Class)}.
	 * <p>
	 * <js>"default"</js> can be used to indicate the JVM default file system charset.
	 */
	public static final String PARSER_fileCharset = "Parser.fileCharset";


	final boolean debug, trimStrings, strict;
	final String inputStreamCharset, fileCharset;

	/**
	 * Constructor.
	 *
	 * @param cf The factory that created this context.
	 */
	public ParserContext(ContextFactory cf) {
		super(cf);
		this.debug = cf.getProperty(PARSER_debug, boolean.class, false);
		this.trimStrings = cf.getProperty(PARSER_trimStrings, boolean.class, false);
		this.strict = cf.getProperty(PARSER_strict, boolean.class, false);
		this.inputStreamCharset = cf.getProperty(PARSER_inputStreamCharset, String.class, "UTF-8");
		this.fileCharset = cf.getProperty(PARSER_fileCharset, String.class, "default");
	}
}
