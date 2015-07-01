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
	private JsonObject _parameters;
	private JsonObject _response;

	void setResponse(JsonObject response) {
		_response = response;
	}

	public Cypher(String statementText) {
		_request = new JsonObject();
		JsonArray statements = new JsonArray();
		_request.add("statements", statements);
		JsonObject statement = new JsonObject();
		statements.add(statement);
		statement.add("statement", statementText);
		_parameters = new JsonObject();
		statement.add("parameters", _parameters);
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

	public JsonObject getRequest() {
		return _request;
	}

	public JsonObject getResponse() {
		return _response;
	}

	public JsonArray getResultDataRows() {
		try {
			JsonArray resultDataRows = getResponse().get("results").asArray()
					.get(0).asObject().get("data").asArray().get(0).asObject()
					.get("row").asArray();
			return resultDataRows;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public int getResultDataRowCount() {
		JsonArray resultDataRows = getResultDataRows();
		if (resultDataRows == null)
			return 0;
		else
			return resultDataRows.size();
	}

	public JsonValue getResultDataRow(int index) {
		JsonArray resultDataRows = getResultDataRows();
		if (resultDataRows == null)
			return null;
		else
			return resultDataRows.get(index);
	}

}
