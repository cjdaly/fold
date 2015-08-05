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

package net.locosoft.fold.channel.times.internal;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.sketch.AbstractNodeSketch;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class RefTimesNode extends AbstractNodeSketch {

	public RefTimesNode(long timesNodeId) {
		_nodeId = timesNodeId;
	}

	private static final String CYPHER_CREATE_TIMES_REF = "MATCH timesNode, refNode" //
			+ " WHERE ID(timesNode)={timesNodeId} AND ID(refNode)={refNodeId}"
			+ " CREATE (refNode)-[:times_Ref]->(timesNode)";

	public void createTimesRef(long refNodeId) {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = CYPHER_CREATE_TIMES_REF;
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("timesNodeId", getNodeId());
		cypher.addParameter("refNodeId", refNodeId);
		neo4jService.invokeCypher(cypher);
	}

	public long[] getTimesRefNodeIds(String refNodeLabel) {
		return getTimesRefNodeIds(refNodeLabel, null, null);
	}

	public long[] getTimesRefNodeIds(String refNodeLabel, String refNodeKey,
			String refNodeValue) {
		INeo4jService neo4jService = getNeo4jService();

		StringBuilder cypherText = new StringBuilder();
		cypherText.append("MATCH (refNode");
		if ((refNodeLabel != null) && !"".equals(refNodeLabel)) {
			cypherText.append(":");
			cypherText.append(refNodeLabel);
		}
		cypherText.append(")-[:times_Ref]->(timesNode) ");
		cypherText.append("WHERE ID(timesNode)={timesNodeId} ");
		if ((refNodeKey != null) && (refNodeValue != null)) {
			cypherText.append(" AND refNode.`" + refNodeKey
					+ "`={refNodeValue} ");
		}
		cypherText.append("RETURN ID(refNode)");

		ICypher cypher = neo4jService.constructCypher(cypherText.toString());
		cypher.addParameter("timesNodeId", getNodeId());
		if ((refNodeKey != null) && (refNodeValue != null)) {
			cypher.addParameter("refNodeValue", refNodeValue);
		}
		neo4jService.invokeCypher(cypher);

		long[] refIds = new long[cypher.getResultDataRowCount()];
		for (int i = 0; i < cypher.getResultDataRowCount(); i++) {
			JsonValue jsonValue = cypher.getResultDataRow(i);
			refIds[i] = jsonValue.asLong();
		}
		return refIds;
	}

	public JsonObject[] getTimesRefNodes() {
		INeo4jService neo4jService = getNeo4jService();
		StringBuilder cypherText = new StringBuilder();
		cypherText.append("MATCH (refNodes)-[:times_Ref]->(timesNode) ");
		cypherText.append("WHERE ID(timesNode)={timesNodeId} ");
		cypherText.append("RETURN refNodes");

		ICypher cypher = neo4jService.constructCypher(cypherText.toString());
		cypher.addParameter("timesNodeId", getNodeId());
		neo4jService.invokeCypher(cypher);

		JsonObject[] jsonNodes = new JsonObject[cypher.getResultDataRowCount()];
		for (int i = 0; i < cypher.getResultDataRowCount(); i++) {
			JsonValue jsonValue = cypher.getResultDataRow(i);
			jsonNodes[i] = jsonValue.asObject();
		}
		return jsonNodes;
	}

}
