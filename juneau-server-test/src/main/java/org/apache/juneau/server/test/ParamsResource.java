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
package org.apache.juneau.server.test;

import static org.apache.juneau.server.RestServletContext.*;
import static org.apache.juneau.urlencoding.UrlEncodingContext.*;

import java.util.*;

import javax.servlet.http.*;

import org.apache.juneau.*;
import org.apache.juneau.json.*;
import org.apache.juneau.plaintext.*;
import org.apache.juneau.samples.addressbook.*;
import org.apache.juneau.server.*;
import org.apache.juneau.server.annotation.*;
import org.apache.juneau.transforms.*;
import org.apache.juneau.urlencoding.*;

/**
 * JUnit automated testcase resource.
 */
@RestResource(
	path="/testParams",
	serializers=PlainTextSerializer.class,
	properties={
		@Property(name=REST_allowMethodParam, value="*")
	}
)
public class ParamsResource extends RestServletDefault {
	private static final long serialVersionUID = 1L;

	//====================================================================================================
	// Basic tests
	//====================================================================================================
	@RestMethod(name="GET", path="/")
	public void doGet(RestResponse res) {
		res.setOutput("GET");
	}

	@RestMethod(name="GET", path="/get1")
	public String doGet1() {
		return "GET /get1";
	}

	@RestMethod(name="GET", path="/get1/{foo}")
	public void doGet1a(RestResponse res, String foo) {
		res.setOutput("GET /get1a " + foo);
	}

	@RestMethod(name="GET", path="/get1/{foo}/{bar}")
	public void doGet1b(RestResponse res, String foo, String bar) {
		res.setOutput("GET /get1b " + foo + "," + bar);
	}

	@RestMethod(name="GET", path="/get3/{foo}/{bar}/*")
	public void doGet3(HttpServletRequest reqx, HttpServletResponse resx, String foo, int bar) {
		RestRequest req = (RestRequest)reqx;
		RestResponse res = (RestResponse)resx;
		res.setOutput("GET /get3/"+foo+"/"+bar+" remainder="+req.getPathRemainder());
	}

	// Test method name with overlapping name, remainder allowed.
	@RestMethod(name="GET2")
	public void get2(RestRequest req, RestResponse res) {
		res.setOutput("GET2 remainder="+req.getPathRemainder());
	}

	// Default POST
	@RestMethod(name="POST")
	public void doPost(RestRequest req, RestResponse res) {
		res.setOutput("POST remainder="+req.getPathRemainder());
	}

	// Bean parameter
	@RestMethod(name="POST", path="/person/{person}")
	public void doPost(RestRequest req, RestResponse res, Person p) {
		res.setOutput("POST /person/{name="+p.name+",birthDate.year="+p.birthDate.get(Calendar.YEAR)+"} remainder="+req.getPathRemainder());
	}

	// Various primitive types
	@RestMethod(name="PUT", path="/primitives/{xInt}/{xShort}/{xLong}/{xChar}/{xFloat}/{xDouble}/{xByte}/{xBoolean}")
	public void doPut1(RestResponse res, int xInt, short xShort, long xLong, char xChar, float xFloat, double xDouble, byte xByte, boolean xBoolean) {
		res.setOutput("PUT /primitives/"+xInt+"/"+xShort+"/"+xLong+"/"+xChar+"/"+xFloat+"/"+xDouble+"/"+xByte+"/"+xBoolean);
	}

	// Various primitive objects
	@RestMethod(name="PUT", path="/primitiveObjects/{xInt}/{xShort}/{xLong}/{xChar}/{xFloat}/{xDouble}/{xByte}/{xBoolean}")
	public void doPut2(RestResponse res, Integer xInt, Short xShort, Long xLong, Character xChar, Float xFloat, Double xDouble, Byte xByte, Boolean xBoolean) {
		res.setOutput("PUT /primitiveObjects/"+xInt+"/"+xShort+"/"+xLong+"/"+xChar+"/"+xFloat+"/"+xDouble+"/"+xByte+"/"+xBoolean);
	}

	// Object with forString(String) method
	@RestMethod(name="PUT", path="/uuid/{uuid}")
	public void doPut1(RestResponse res, UUID uuid) {
		res.setOutput("PUT /uuid/"+uuid);
	}

	@Override /* RestServlet */
	public Class<?>[] createPojoSwaps() {
		return new Class[]{CalendarSwap.Medium.class};
	}

	//====================================================================================================
	// @FormData annotation - GET
	//====================================================================================================
	@RestMethod(name="GET", path="/testParamGet/*")
	public String testParamGet(RestRequest req, @Query("p1") String p1, @Query("p2") int p2) throws Exception {
		return "p1=["+p1+","+req.getQueryParameter("p1")+","+req.getQueryParameter("p1", String.class)+"],p2=["+p2+","+req.getQueryParameter("p2")+","+req.getQueryParameter("p2", int.class)+"]";
	}

	//====================================================================================================
	// @FormData annotation - POST
	//====================================================================================================
	@RestMethod(name="POST", path="/testParamPost/*")
	public String testParamPost(RestRequest req, @FormData("p1") String p1, @FormData("p2") int p2) throws Exception {
		return "p1=["+p1+","+req.getFormDataParameter("p1")+","+req.getFormDataParameter("p1", String.class)+"],p2=["+p2+","+req.getFormDataParameter("p2")+","+req.getFormDataParameter("p2", int.class)+"]";
	}

	//====================================================================================================
	// @Query annotation - GET
	//====================================================================================================
	@RestMethod(name="GET", path="/testQParamGet/*")
	public String testQParamGet(RestRequest req, @Query("p1") String p1, @Query("p2") int p2) throws Exception {
		return "p1=["+p1+","+req.getQueryParameter("p1")+","+req.getQueryParameter("p1", String.class)+"],p2=["+p2+","+req.getQueryParameter("p2")+","+req.getQueryParameter("p2", int.class)+"]";
	}

	//====================================================================================================
	// @Query annotation - POST
	//====================================================================================================
	@RestMethod(name="POST", path="/testQParamPost/*")
	public String testQParamPost(RestRequest req, @Query("p1") String p1, @Query("p2") int p2) throws Exception {
		return "p1=["+p1+","+req.getQueryParameter("p1")+","+req.getQueryParameter("p1", String.class)+"],p2=["+p2+","+req.getQueryParameter("p2")+","+req.getQueryParameter("p2", int.class)+"]";
	}

	//====================================================================================================
	// @FormData(format=PLAIN) annotation - GET
	//====================================================================================================
	@RestMethod(name="GET", path="/testPlainParamGet/*")
	public String testPlainParamGet(RestRequest req, @Query(value="p1",format="PLAIN") String p1) throws Exception {
		return "p1=["+p1+","+req.getQueryParameter("p1")+","+req.getQueryParameter("p1", String.class)+"]";
	}

	//====================================================================================================
	// @FormData(format=PLAIN) annotation - POST
	//====================================================================================================
	@RestMethod(name="POST", path="/testPlainParamPost/*")
	public String testPlainParamPost(RestRequest req, @FormData(value="p1",format="PLAIN") String p1) throws Exception {
		return "p1=["+p1+","+req.getFormDataParameter("p1")+","+req.getFormDataParameter("p1", String.class)+"]";
	}

	//====================================================================================================
	// @Query(format=PLAIN) annotation - GET
	//====================================================================================================
	@RestMethod(name="GET", path="/testPlainQParamGet/*")
	public String testPlainQParamGet(RestRequest req, @Query(value="p1",format="PLAIN") String p1) throws Exception {
		return "p1=["+p1+","+req.getQueryParameter("p1")+","+req.getQueryParameter("p1", String.class)+"]";
	}

	//====================================================================================================
	// @Query(format=PLAIN) annotation - POST
	//====================================================================================================
	@RestMethod(name="POST", path="/testPlainQParamPost/*")
	public String testPlainQParamPost(RestRequest req, @Query(value="p1",format="PLAIN") String p1) throws Exception {
		return "p1=["+p1+","+req.getQueryParameter("p1")+","+req.getQueryParameter("p1", String.class)+"]";
	}

	//====================================================================================================
	// @HasQuery annotation - GET
	//====================================================================================================
	@RestMethod(name="GET", path="/testHasParamGet/*")
	public String testHasParamGet(RestRequest req, @HasQuery("p1") boolean p1, @HasQuery("p2") Boolean p2) throws Exception {
		return "p1=["+p1+","+req.hasQueryParameter("p1")+"],p2=["+p2+","+req.hasQueryParameter("p2")+"]";
	}

	//====================================================================================================
	// @HasQuery annotation - POST
	//====================================================================================================
	@RestMethod(name="POST", path="/testHasParamPost/*")
	public String testHasParamPost(RestRequest req, @HasFormData("p1") boolean p1, @HasFormData("p2") Boolean p2) throws Exception {
		return "p1=["+p1+","+req.hasFormDataParameter("p1")+"],p2=["+p2+","+req.hasFormDataParameter("p2")+"]";
	}

	//====================================================================================================
	// @HasQuery annotation - GET
	//====================================================================================================
	@RestMethod(name="GET", path="/testHasQParamGet/*")
	public String testHasQParamGet(RestRequest req, @HasQuery("p1") boolean p1, @HasQuery("p2") Boolean p2) throws Exception {
		return "p1=["+p1+","+req.hasQueryParameter("p1")+"],p2=["+p2+","+req.hasQueryParameter("p2")+"]";
	}

	//====================================================================================================
	// @HasQuery annotation - POST
	//====================================================================================================
	@RestMethod(name="POST", path="/testHasQParamPost/*")
	public String testHasQParamPost_post(RestRequest req, @HasQuery("p1") boolean p1, @HasQuery("p2") Boolean p2) throws Exception {
		return "p1=["+p1+","+req.hasQueryParameter("p1")+"],p2=["+p2+","+req.hasQueryParameter("p2")+"]";
	}

	//====================================================================================================
	// Form POSTS with @Body parameter
	//====================================================================================================
	@RestMethod(name="POST", path="/testFormPostAsContent/*")
	public String testFormPostAsContent(@Body Test6Bean bean,
			@HasQuery("p1") boolean hqp1, @HasQuery("p2") boolean hqp2,
			@Query("p1") String qp1, @Query("p2") int qp2) throws Exception {
		return "bean=["+JsonSerializer.DEFAULT_LAX.toString(bean)+"],qp1=["+qp1+"],qp2=["+qp2+"],hqp1=["+hqp1+"],hqp2=["+hqp2+"]";
	}

	public static class Test6Bean {
		public String p1;
		public int p2;
	}

	//====================================================================================================
	// Test @FormData and @Query annotations when using multi-part parameters (e.g. &key=val1,&key=val2).
	//====================================================================================================
	@RestMethod(name="GET", path="/testMultiPartParams")
	public String testMultiPartParams(
			@Query(value="p1",multipart=true) String[] p1,
			@Query(value="p2",multipart=true) int[] p2,
			@Query(value="p3",multipart=true) List<String> p3,
			@Query(value="p4",multipart=true) List<Integer> p4,
			@Query(value="p5",multipart=true) String[] p5,
			@Query(value="p6",multipart=true) int[] p6,
			@Query(value="p7",multipart=true) List<String> p7,
			@Query(value="p8",multipart=true) List<Integer> p8,
			@Query(value="p9",multipart=true) A[] p9,
			@Query(value="p10",multipart=true) List<A> p10,
			@Query(value="p11",multipart=true) A[] p11,
			@Query(value="p12",multipart=true) List<A> p12) throws Exception {
		ObjectMap m = new ObjectMap()
			.append("p1", p1)
			.append("p2", p2)
			.append("p3", p3)
			.append("p4", p4)
			.append("p5", p5)
			.append("p6", p6)
			.append("p7", p7)
			.append("p8", p8)
			.append("p9", p9)
			.append("p10", p10)
			.append("p11", p11)
			.append("p12", p12);
		return JsonSerializer.DEFAULT_LAX.toString(m);
	}

	public static class A {
		public String a;
		public int b;
		public boolean c;
	}

	//====================================================================================================
	// Test multi-part parameter keys on bean properties of type array/Collection (i.e. &key=val1,&key=val2)
	// using URLENC_expandedParams property.
	// A simple round-trip test to verify that both serializing and parsing works.
	//====================================================================================================
	@RestMethod(name="POST", path="/testFormPostsWithMultiParamsUsingProperty",
		properties={
			@Property(name=URLENC_expandedParams, value="true"),
			@Property(name=UonSerializerContext.UON_simpleMode, value="true")
		}
	)
	public DTO2s.B testFormPostsWithMultiParamsViaProperty(@Body DTO2s.B content) throws Exception {
		return content;
	}

	//====================================================================================================
	// Test multi-part parameter keys on bean properties of type array/Collection (i.e. &key=val1,&key=val2)
	// using @UrlEncoding(expandedParams=true) annotation.
	// A simple round-trip test to verify that both serializing and parsing works.
	//====================================================================================================
	@RestMethod(name="POST", path="/testFormPostsWithMultiParamsUsingAnnotation",
		properties={
			@Property(name=UonSerializerContext.UON_simpleMode, value="true")
		}
	)
	public DTO2s.C testFormPostsWithMultiParamsUsingAnnotation(@Body DTO2s.C content) throws Exception {
		return content;
	}
}
