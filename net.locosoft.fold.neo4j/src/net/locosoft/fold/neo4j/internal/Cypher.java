/*****************************************************************************
 * Copyright (c) 2015 Chris J Daly (github user cjdaly)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   cjdaly - initial API and implementation
 ****************************************************************************/

package net.locosoft.fold.neo4j.internal;

import net.locosoft.fold.neo4j.ICypher;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Cypher implements ICypher {

	private JsonObject _request;
	private JsonObject _statement;
	private JsonObject _parameters;
	private JsonObject _response;

	void setResponse(JsonObject response) {
		_response = response;
	}

	public Cypher(String statementText) {
		_request = new JsonObject();
		JsonArray statements = new JsonArray();
		_request.add("statements", statements);
		_statement = new JsonObject();
		statements.add(_statement);
		if (statementText != null) {
			_statement.add("statement", statementText);
		}
		_parameters = new JsonObject();
		_statement.add("parameters", _parameters);
	}

	public void addStatement(String statementText) {
		_statement.add("statement", statementText);
	}

	public void addParameter(String name, JsonValue value) {
		_parameters.add(name, value);
	}

	public void addParameter(String name, int value) {
		_parameters.add(name, value);
	}

	public void addParameter(String name, long value) {
		_parameters.add(name, value);
	}

	public void addParameter(String name, float value) {
		_parameters.add(name, value);
	}

	public void addParameter(String name, double value) {
		_parameters.add(name, value);
	}

	public void addParameter(String name, boolean value) {
		_parameters.add(name, value);
	}

	public void addParameter(String name, String value) {
		_parameters.add(name, value);
	}

	public String getStatementText() {
		return _statement.getString("statement", null);
	}

	public JsonObject getRequest() {
		return _request;
	}

	public JsonObject getResponse() {
		return _response;
	}

	public JsonArray getResultData() {
		try {
			return getResponse().get("results").asArray().get(0).asObject()
					.get("data").asArray();
		} catch (IndexOutOfBoundsException ex) {
			// ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new JsonArray();
	}

	public JsonArray getErrors() {
		try {
			return getResponse().get("errors").asArray();
		} catch (IndexOutOfBoundsException ex) {
			// ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new JsonArray();
	}

	public int getErrorCount() {
		return getErrors().size();
	}

	public int getResultDataRowCount() {
		JsonArray resultData = getResultData();
		return resultData.size();
	}

	public JsonValue getResultDataRow(int index) {
		JsonArray resultData = getResultData();
		if ((index >= 0) && (index < resultData.size())) {
			try {
				return resultData.get(index).asObject().get("row").asArray()
						.get(0);
			} catch (IndexOutOfBoundsException ex) {
				// ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

}
