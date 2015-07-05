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

import com.eclipsesource.json.JsonValue;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.sketch.AbstractNodeSketch;

public class PropertyAccessNode extends AbstractNodeSketch {

	public PropertyAccessNode(long nodeId) {
		_nodeId = nodeId;
	}

	// getters

	private JsonValue getValue(String propertyName) {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = "MATCH node" //
				+ " WHERE id(node)={nodeId}" + " RETURN node.`"
				+ propertyName
				+ "`";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("nodeId", getNodeId());
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
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = "MATCH node" //
				+ " WHERE id(node)={nodeId}" //
				+ " SET node.`" + propertyName + "`={value}";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("nodeId", getNodeId());
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
