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

package net.locosoft.fold.sketch.pad.neo4j;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jUtil;
import net.locosoft.fold.sketch.AbstractSketch;
import net.locosoft.fold.sketch.ISketch;

import com.eclipsesource.json.JsonValue;

public interface INeo4jPropertyAccess extends ISketch {

	void init(long nodeId);

	// getters

	int getIntValue(String propertyName);

	long getLongValue(String propertyName);

	float getFloatValue(String propertyName);

	double getDoubleValue(String propertyName);

	boolean getBooleanValue(String propertyName);

	String getStringValue(String propertyName);

	// setters

	void setValue(String propertyName, int value);

	void setValue(String propertyName, long value);

	void setValue(String propertyName, float value);

	void setValue(String propertyName, double value);

	void setValue(String propertyName, boolean value);

	void setValue(String propertyName, String value);

	class Impl extends AbstractSketch implements INeo4jPropertyAccess {

		private long _nodeId;

		public Class<? extends ISketch> getSketchInterface() {
			return INeo4jPropertyAccess.class;
		}

		public void init(long nodeId) {
			_nodeId = nodeId;
		}

		// getters

		private JsonValue getValue(String propertyName) {
			INeo4jService neo4jService = Neo4jUtil.getNeo4jService();
			String cypherText = "MATCH node WHERE id(node)={nodeId} RETURN node.`"
					+ propertyName + "`";
			ICypher cypher = neo4jService.constructCypher(cypherText);
			cypher.addParameter("nodeId", _nodeId);
			neo4jService.invokeCypher(cypher);
			return cypher.getResultDataRow(0);
		}

		public int getIntValue(String propertyName) {
			return getValue(propertyName).asInt();
		}

		public long getLongValue(String propertyName) {
			return getValue(propertyName).asLong();
		}

		public float getFloatValue(String propertyName) {
			return getValue(propertyName).asFloat();
		}

		public double getDoubleValue(String propertyName) {
			return getValue(propertyName).asDouble();
		}

		public boolean getBooleanValue(String propertyName) {
			return getValue(propertyName).asBoolean();
		}

		public String getStringValue(String propertyName) {
			return getValue(propertyName).asString();
		}

		// setters

		private void setValue(String propertyName, JsonValue value) {
			INeo4jService neo4jService = Neo4jUtil.getNeo4jService();
			String cypherText = "MATCH node WHERE id(node)={nodeId} SET node.`"
					+ propertyName + "`={value}";
			ICypher cypher = neo4jService.constructCypher(cypherText);
			cypher.addParameter("nodeId", _nodeId);
			cypher.addParameter("value", value);
			neo4jService.invokeCypher(cypher);
		}

		public void setValue(String propertyName, int value) {
			setValue(propertyName, JsonValue.valueOf(value));
		}

		public void setValue(String propertyName, long value) {
			setValue(propertyName, JsonValue.valueOf(value));
		}

		public void setValue(String propertyName, float value) {
			setValue(propertyName, JsonValue.valueOf(value));
		}

		public void setValue(String propertyName, double value) {
			setValue(propertyName, JsonValue.valueOf(value));
		}

		public void setValue(String propertyName, boolean value) {
			setValue(propertyName, JsonValue.valueOf(value));
		}

		public void setValue(String propertyName, String value) {
			setValue(propertyName, JsonValue.valueOf(value));
		}
	}
}
