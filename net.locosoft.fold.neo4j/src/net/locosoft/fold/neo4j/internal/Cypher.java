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

	private boolean _parallelInvocation = false;
	private JsonObject _request;
	private JsonArray _statements;
	private int _currentStatement = -1;
	private JsonObject _response;

	void setResponse(JsonObject response) {
		_response = response;
	}

	public Cypher(String statementText) {
		_request = new JsonObject();
		_statements = new JsonArray();
		_request.add("statements", _statements);
		addStatement(statementText);
	}

	public boolean allowParallelInvocation() {
		return _parallelInvocation;
	}

	public void allowParallelInvocation(boolean parallelInvocation) {
		_parallelInvocation = parallelInvocation;
	}

	public void addStatement(String statementText) {
		if (statementText != null) {
			JsonObject statement = new JsonObject();
			_statements.add(statement);
			statement.add("statement", statementText);
			JsonObject parameters = new JsonObject();
			statement.add("parameters", parameters);
			_currentStatement++;
		}
	}

	public void addParameter(String name, JsonValue value) {
		getCurrentParameters().add(name, value);
	}

	public void addParameter(String name, int value) {
		getCurrentParameters().add(name, value);
	}

	public void addParameter(String name, long value) {
		getCurrentParameters().add(name, value);
	}

	public void addParameter(String name, float value) {
		getCurrentParameters().add(name, value);
	}

	public void addParameter(String name, double value) {
		getCurrentParameters().add(name, value);
	}

	public void addParameter(String name, boolean value) {
		getCurrentParameters().add(name, value);
	}

	public void addParameter(String name, String value) {
		getCurrentParameters().add(name, value);
	}

	public JsonObject getCurrentStatement() {
		return _statements.get(_currentStatement).asObject();
	}

	public JsonObject getCurrentParameters() {
		return getCurrentStatement().get("parameters").asObject();
	}

	public String getStatementText() {
		return getCurrentStatement().getString("statement", null);
	}

	public void setStatementText(String statementText) {
		getCurrentStatement().set("statement", statementText);
	}

	public JsonObject getRequest() {
		return _request;
	}

	public JsonObject getResponse() {
		return _response;
	}

	public JsonArray getResultData() {
		return getResultData(0);
	}

	public JsonArray getResultData(int resultSet) {
		try {
			return getResponse().get("results").asArray().get(resultSet)
					.asObject().get("data").asArray();
		} catch (IndexOutOfBoundsException ex) {
			// ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new JsonArray();
	}

	public int getResultDataRowCount() {
		JsonArray resultData = getResultData(0);
		return resultData.size();
	}

	public int getResultDataRowCount(int resultSet) {
		JsonArray resultData = getResultData(resultSet);
		return resultData.size();
	}

	public JsonValue getResultDataRow(int index) {
		return getResultDataRow(0, index);
	}

	public JsonValue getResultDataRow(int resultSet, int index) {
		JsonArray resultData = getResultData(resultSet);
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

}
