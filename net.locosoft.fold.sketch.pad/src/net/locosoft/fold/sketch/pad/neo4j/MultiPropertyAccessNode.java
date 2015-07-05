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
import net.locosoft.fold.sketch.AbstractNodeSketch;

public class MultiPropertyAccessNode extends AbstractNodeSketch {

	private String _propertyNamePrefix;

	private ICypher _cypher;
	private StringBuilder _cypherStatement;
	private boolean _firstProperty;

	public MultiPropertyAccessNode(long nodeId, String propertyNamePrefix) {
		_nodeId = nodeId;
		_propertyNamePrefix = propertyNamePrefix == null ? ""
				: propertyNamePrefix;
		reset();
	}

	public void reset() {
		INeo4jService neo4jService = getNeo4jService();
		_cypher = neo4jService.constructCypher();
		_cypherStatement = new StringBuilder(
				"MATCH node WHERE id(node)={nodeId} SET ");
		_firstProperty = true;
	}

	private void addCypherSet(String name) {
		if (_firstProperty)
			_firstProperty = false;
		else
			_cypherStatement.append(",");

		_cypherStatement.append("node.`" + _propertyNamePrefix + name + "`={"
				+ name + "}");
	}

	public void addProperty(String name, int value) {
		addCypherSet(name);
		_cypher.addParameter(name, value);
	}

	public void addProperty(String name, long value) {
		addCypherSet(name);
		_cypher.addParameter(name, value);
	}

	public void addProperty(String name, float value) {
		addCypherSet(name);
		_cypher.addParameter(name, value);
	}

	public void addProperty(String name, double value) {
		addCypherSet(name);
		_cypher.addParameter(name, value);
	}

	public void addProperty(String name, boolean value) {
		addCypherSet(name);
		_cypher.addParameter(name, value);
	}

	public void addProperty(String name, String value) {
		addCypherSet(name);
		_cypher.addParameter(name, value);
	}

	public void setProperties() {
		INeo4jService neo4jService = getNeo4jService();
		_cypher.addParameter("nodeId", getNodeId());
		_cypher.addStatement(_cypherStatement.toString());
		neo4jService.invokeCypher(_cypher);
	}
}
