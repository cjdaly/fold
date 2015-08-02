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

package net.locosoft.fold.neo4j;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public interface ICypher {

	JsonObject getRequest();

	void addStatement(String statementText);

	void addParameter(String name, JsonValue value);

	void addParameter(String name, int value);

	void addParameter(String name, long value);

	void addParameter(String name, float value);

	void addParameter(String name, double value);

	void addParameter(String name, boolean value);

	void addParameter(String name, String value);

	JsonObject getResponse();

	JsonArray getResultData();

	int getResultDataRowCount();

	JsonValue getResultDataRow(int index);

	JsonArray getErrors();

	int getErrorCount();
}
