/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ibm.mds;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class MobileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HttpClient httpClient = null;

	// set default db connection credentials
	String databaseHost = "user.cloudant.com";
	int port = 443;
	String databaseName = "sample_nosql_db";
	String user = "user";
	String password = "password";

	private static final String DEFAULT_KEY = "bluelist";

	private static final String RESP_SUCCESS = "1000";
	private static final String RESP_ERR_COMMAND_NOT_CORRECT = "1001";
	private static final String RESP_TXT_COMMAND_NOT_CORRECT = "Command Not In Well Format";
	private static final String RESP_ERR_DB_CONNECT_FAIL = "1002";
	private static final String RESP_TXT_DB_CONNECT_FAIL = "Database connection fails";
	private static final String RESP_ERR_IN_BLACK_LIST = "1003";
	private static final String RESP_TXT_IN_BLACK_LIST ="In Blacklist";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String cmd = req.getParameter("cmd");
		if (cmd == null) {
			doResp(formartErrJsonMsg(RESP_ERR_COMMAND_NOT_CORRECT,
					RESP_TXT_COMMAND_NOT_CORRECT), resp);
			return;
		}

		// Add a record
		if (cmd.equals("test")) {

			doResp(formartErrJsonMsg("test","xujie's first test"), resp);
//			String val = req.getParameter("value");
//			if (val == null || val.equals("")) {
//				doResp(formartErrJsonMsg(RESP_ERR_COMMAND_NOT_CORRECT,
//						RESP_TXT_COMMAND_NOT_CORRECT), resp);
//			} else {
//				doRespAdd(val, resp);
//			}
		}
		// List the records
//		else if (cmd.equals("LIST")) {
//			doRespList(resp);
//		}
//		// Update the record
//		else if (cmd.equals("UPDATE")) {
//			String id = req.getParameter("id");
//			String val = req.getParameter("value");
//			if (id == null || id.equals("") || val == null || val.equals("")) {
//				doResp(formartErrJsonMsg(RESP_ERR_COMMAND_NOT_CORRECT,
//						RESP_TXT_COMMAND_NOT_CORRECT), resp);
//			} else {
//				doRespUpdate(id, val, resp);
//			}
//		}
//		// Delete the record
//		else if (cmd.equals("DELETE")) {
//			String id = req.getParameter("id");
//			if (id == null || id.equals("")) {
//				doResp(formartErrJsonMsg(RESP_ERR_COMMAND_NOT_CORRECT,
//						RESP_TXT_COMMAND_NOT_CORRECT), resp);
//			} else {
//				doRespDel(id, resp);
//			}
//		} else {
//			doResp(formartErrJsonMsg(RESP_ERR_COMMAND_NOT_CORRECT,
//					RESP_TXT_COMMAND_NOT_CORRECT), resp);
//		}

	}

	private void doRespUpdate(String id, String value, HttpServletResponse resp)
			throws IOException {
		System.out.println("Create invoked...");
		CouchDbConnector dbConnector = null;
		try {
			dbConnector = createDbConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			doResp(formartErrJsonMsg(RESP_ERR_DB_CONNECT_FAIL,
					RESP_TXT_DB_CONNECT_FAIL), resp);
			return;
		}

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject resultObject = new JSONObject();
		// get the document object by providing doc id
		HashMap<String, Object> obj = dbConnector.get(HashMap.class, id);

		obj.put("value", value);
		dbConnector.update(obj);
		System.out.println("Update Successful...");
		jsonObject.put("id", id);
		jsonObject.put("name", DEFAULT_KEY);
		jsonObject.put("value", value);
		jsonArray.add(jsonObject);

		resultObject.put("respCode", RESP_SUCCESS);
		resultObject.put("body", jsonArray);

		closeDBConnector();

		String respJson = resultObject.toString();
		doResp(respJson, resp);
	}

	private void doRespAdd(String value, HttpServletResponse resp)
			throws IOException {
		
		//A very simple black list validation
		if(value.toLowerCase().contains("black"))
		{
			doResp(formartErrJsonMsg(RESP_ERR_IN_BLACK_LIST,
					RESP_TXT_IN_BLACK_LIST), resp);
			return;
		}
		
		System.out.println("Create invoked...");
		CouchDbConnector dbConnector = null;
		try {
			dbConnector = createDbConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			doResp(formartErrJsonMsg(RESP_ERR_DB_CONNECT_FAIL,
					RESP_TXT_DB_CONNECT_FAIL), resp);
			return;
		}

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject resultObject = new JSONObject();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", DEFAULT_KEY);
		long id = System.currentTimeMillis();
		data.put("_id", id + "");
		data.put("value", value);
		data.put("creation_date", new Date().toString());
		dbConnector.create(data);
		System.out.println("Create Successful...");
		jsonObject.put("id", id);
		jsonObject.put("name", DEFAULT_KEY);
		jsonObject.put("value", value);
		jsonArray.add(jsonObject);

		resultObject.put("respCode", RESP_SUCCESS);
		resultObject.put("body", jsonArray);

		// close the connection manager
		closeDBConnector();

		String respJson = resultObject.toString();
		doResp(respJson, resp);
	}

	private void doRespDel(String id, HttpServletResponse resp)
			throws IOException {
		System.out.println("Create invoked...");
		CouchDbConnector dbConnector = null;
		try {
			dbConnector = createDbConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			doResp(formartErrJsonMsg(RESP_ERR_DB_CONNECT_FAIL,
					RESP_TXT_DB_CONNECT_FAIL), resp);
			return;
		}

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject resultObject = new JSONObject();
		// get the document object by providing doc id
		HashMap<String, Object> obj = dbConnector.get(HashMap.class, id);

		dbConnector.delete(obj);

		System.out.println("Delete Successful...");
		jsonObject.put("id", id);
		jsonObject.put("name", DEFAULT_KEY);
		jsonObject.put("value", "");
		jsonArray.add(jsonObject);

		resultObject.put("respCode", RESP_SUCCESS);
		resultObject.put("body", jsonArray);

		// close the connection manager
		closeDBConnector();

		String respJson = resultObject.toString();
		doResp(respJson, resp);
	}

	private void doRespList(HttpServletResponse resp) throws IOException {
		CouchDbConnector dbConnector = null;
		try {
			dbConnector = createDbConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			doResp(formartErrJsonMsg(RESP_ERR_DB_CONNECT_FAIL,
					RESP_TXT_DB_CONNECT_FAIL), resp);
			return;
		}

		JSONObject resultObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		try {
			// get all the document IDs present in database
			List<String> docIds = dbConnector.getAllDocIds();

			for (String docId : docIds) {
				// get the document object by providing doc id
				HashMap<String, Object> obj = dbConnector.get(HashMap.class,
						docId);
				JSONObject jsonObject = new JSONObject();

				String name = (String) obj.get("name");
				if (name != null && name.equals(DEFAULT_KEY)) {

					jsonObject.put("id", obj.get("_id"));
					jsonObject.put("name", obj.get("name"));
					jsonObject.put("value", obj.get("value"));
					System.out.println("====> " + jsonObject);
					jsonArray.add(jsonObject);
				}
			}

		} catch (DocumentNotFoundException dnfe) {
			System.out.println("Exception thrown : " + dnfe.getMessage());

			// close the connection manager
			closeDBConnector();

			doResp(formartErrJsonMsg(RESP_ERR_DB_CONNECT_FAIL,
					RESP_TXT_DB_CONNECT_FAIL), resp);
			return;
		}

		resultObject.put("respCode", RESP_SUCCESS);
		resultObject.put("body", jsonArray);
		// close the connection manager
		closeDBConnector();

		String respJson = resultObject.toString();
		doResp(respJson, resp);
	}

	private void doResp(String jsonMsg, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(jsonMsg);
		resp.setStatus(200);
	}

	private String formartErrJsonMsg(String err, String errTxt) {
		JSONObject resultObject = new JSONObject();
		resultObject.put("respCode", err);
		resultObject.put("respText", errTxt);
		return resultObject.toString();
	}

	private CouchDbConnector createDbConnector() throws Exception {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for NoSQL DB connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		String serviceName = null;

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			JSONObject obj = JSONObject.parse(VCAP_SERVICES);
			String dbKey = null;
			Set<String> keys = obj.keySet();
			// Look for the VCAP key that holds the cloudant no sql db
			// information
			for (String eachkey : keys) {
				if (eachkey.contains("cloudantNoSQLDB")) {
					dbKey = eachkey;
					break;
				}
			}
			if (dbKey == null) {
				System.out
						.println("Could not find cloudantNoSQLDB key in VCAP_SERVICES env variable ");
				return null;
			}

			JSONArray list = (JSONArray) obj.get(dbKey);
			obj = (JSONObject) list.get(0);
			serviceName = (String) obj.get("name");
			System.out.println("Service Name - " + serviceName);

			obj = (JSONObject) obj.get("credentials");

			databaseHost = (String) obj.get("host");
			port = ((Long) obj.get("port")).intValue();
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			// url is not being used
			// url = (String) obj.get("url");
		} else {
			System.out
					.println("VCAP_SERVICES not found, using hard-coded defaults");
		}

		System.out.println("DB Credentials - " + databaseHost + " - " + port
				+ " - " + user + " - " + password + " - " + databaseName
				+ " - " + serviceName);

		return getDBConnector(databaseHost, port, user, password, databaseName,
				serviceName);

	}

	public void closeDBConnector() {
		if (httpClient != null)
			httpClient.shutdown();
	}

	public CouchDbConnector getDBConnector(String host, int port,
			String username, String password, String dbName, String serviceName) {

		CouchDbInstance dbInstance = null;
		try {
			System.out.println("Look up CouchDbInstance ...");
			dbInstance = (CouchDbInstance) new InitialContext()
					.lookup("java:comp/env/couchdb/" + serviceName);
			System.out.println("Look up successful !");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (dbInstance == null) {
			System.out.println("Creating couch db instance...");
			httpClient = new StdHttpClient.Builder().host(host).port(port)
					.username(username).password(password).enableSSL(true)
					.relaxedSSLSettings(true).build();

			dbInstance = new StdCouchDbInstance(httpClient);
		}

		CouchDbConnector dbConnector = new StdCouchDbConnector(dbName,
				dbInstance);
		dbConnector.createDatabaseIfNotExists();

		return dbConnector;
	}
}
