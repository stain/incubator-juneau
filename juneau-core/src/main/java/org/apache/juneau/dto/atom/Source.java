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
package org.apache.juneau.dto.atom;

import java.net.*;
import java.util.*;

/**
 * Represents an <code>atomSource</code> construct in the RFC4287 specification.
 * <p>
 * <h6 class='figure'>Schema</h6>
 * <p class='bcode'>
 * 	atomSource =
 * 		element atom:source {
 * 			atomCommonAttributes,
 * 			(atomAuthor*
 * 			& atomCategory*
 * 			& atomContributor*
 * 			& atomGenerator?
 * 			& atomIcon?
 * 			& atomId?
 * 			& atomLink*
 * 			& atomLogo?
 * 			& atomRights?
 * 			& atomSubtitle?
 * 			& atomTitle?
 * 			& atomUpdated?
 * 			& extensionElement*)
 * 		}
 * </p>
 * <p>
 * 	Refer to {@link org.apache.juneau.dto.atom} for further information about ATOM support.
 * </p>
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class Source extends CommonEntry {

	private Generator generator;
	private Icon icon;
	private Logo logo;
	private Text subtitle;


	//--------------------------------------------------------------------------------
	// Bean properties
	//--------------------------------------------------------------------------------

	/**
	 * Returns the generator info of this source.
	 *
	 * @return The generator info of this source.
	 */
	public Generator getGenerator() {
		return generator;
	}

	/**
	 * Sets the generator info of this source.
	 *
	 * @param generator The generator info of this source.
	 * @return This object (for method chaining).
	 */
	public Source setGenerator(Generator generator) {
		this.generator = generator;
		return this;
	}

	/**
	 * Returns the icon of this source.
	 *
	 * @return The icon of this source.
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Sets the icon of this source.
	 *
	 * @param icon The icon of this source.
	 * @return This object (for method chaining).
	 */
	public Source setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	/**
	 * Returns the logo of this source.
	 *
	 * @return The logo of this source.
	 */
	public Logo getLogo() {
		return logo;
	}

	/**
	 * Sets the logo of this source.
	 *
	 * @param logo The logo of this source.
	 * @return This object (for method chaining).
	 */
	public Source setLogo(Logo logo) {
		this.logo = logo;
		return this;
	}

	/**
	 * Returns the subtitle of this source.
	 *
	 * @return The subtitle of this source.
	 */
	public Text getSubtitle() {
		return subtitle;
	}

	/**
	 * Sets the subtitle of this source.
	 *
	 * @param subtitle The subtitle of this source.
	 * @return This object (for method chaining).
	 */
	public Source setSubtitle(Text subtitle) {
		this.subtitle = subtitle;
		return this;
	}


	//--------------------------------------------------------------------------------
	// Overridden setters (to simplify method chaining)
	//--------------------------------------------------------------------------------

	@Override /* CommonEntry */
	public Source setAuthors(List<Person> authors) {
		super.setAuthors(authors);
		return this;
	}

	@Override /* CommonEntry */
	public Source addAuthors(Person...authors) {
		super.addAuthors(authors);
		return this;
	}

	@Override /* CommonEntry */
	public Source setCategories(List<Category> categories) {
		super.setCategories(categories);
		return this;
	}

	@Override /* CommonEntry */
	public Source addCategories(Category...categories) {
		super.addCategories(categories);
		return this;
	}

	@Override /* CommonEntry */
	public Source setContributors(List<Person> contributors) {
		super.setContributors(contributors);
		return this;
	}

	@Override /* CommonEntry */
	public Source addContributors(Person...contributors) {
		super.addContributors(contributors);
		return this;
	}

	@Override /* CommonEntry */
	public Source setId(Id id) {
		super.setId(id);
		return this;
	}

	@Override /* CommonEntry */
	public Source setLinks(List<Link> links) {
		super.setLinks(links);
		return this;
	}

	@Override /* CommonEntry */
	public Source addLinks(Link...links) {
		super.addLinks(links);
		return this;
	}

	@Override /* CommonEntry */
	public Source setRights(Text rights) {
		super.setRights(rights);
		return this;
	}

	@Override /* CommonEntry */
	public Source setTitle(Text title) {
		super.setTitle(title);
		return this;
	}

	@Override /* CommonEntry */
	public Source setUpdated(Calendar updated) {
		super.setUpdated(updated);
		return this;
	}

	@Override /* Common */
	public Source setBase(URI base) {
		super.setBase(base);
		return this;
	}

	@Override /* Common */
	public Source setLang(String lang) {
		super.setLang(lang);
		return this;
	}
}
