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

import net.locosoft.fold.neo4j.ICypherTransaction;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class CypherTransaction implements ICypherTransaction {

	private JsonObject _request;

	public JsonObject getRequest() {
		return _request;
	}

	private JsonObject _response;

	void setResponse(JsonObject response) {
		_response = response;
	}

	public JsonObject getResponse() {
		return _response;
	}

	public CypherTransaction(String statementText) {
		_request = new JsonObject();
		JsonArray statements = new JsonArray();
		_request.add("statements", statements);
		JsonObject statement = new JsonObject();
		statements.add(statement);
		statement.add("statement", statementText);
		statement.add("parameters", new JsonObject());
	}

	public void setParameter(String name, JsonValue value) {
		JsonObject parameters = _request.get("statement").asArray().get(0)
				.asObject().get("parameters").asObject();
		parameters.set(name, value);
	}

}
