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

import com.eclipsesource.json.JsonValue;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.sketch.AbstractNodeSketch;

public class RefTimesNode extends AbstractNodeSketch {

	public RefTimesNode(long timesNodeId) {
		_nodeId = timesNodeId;
	}

	public void createTimesRef(long refNodeId) {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = "MATCH timesNode, refNode" //
				+ " WHERE ID(timesNode)={timesNodeId} AND ID(refNode)={refNodeId}"
				+ " CREATE (refNode)-[:times_Ref]->(timesNode)";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("timesNodeId", getNodeId());
		cypher.addParameter("refNodeId", refNodeId);
		neo4jService.invokeCypher(cypher);
	}

	public long[] getTimesRefNodeIds(String refNodeLabel) {
		INeo4jService neo4jService = getNeo4jService();

		StringBuilder cypherText = new StringBuilder();
		cypherText.append("MATCH (refNode");
		if (refNodeLabel != null) {
			cypherText.append(":");
			cypherText.append(refNodeLabel);
		}
		cypherText.append(")-[:times_Ref]->(timesNode) ");
		cypherText.append("WHERE ID(timesNode)={timesNodeId} ");
		cypherText.append("RETURN ID(refNode)");

		ICypher cypher = neo4jService.constructCypher(cypherText.toString());
		cypher.addParameter("timesNodeId", getNodeId());
		neo4jService.invokeCypher(cypher);

		long[] refIds = new long[cypher.getResultDataRowCount()];
		for (int i = 0; i < cypher.getResultDataRowCount(); i++) {
			JsonValue jsonValue = cypher.getResultDataRow(i);
			refIds[i] = jsonValue.asLong();
		}
		return refIds;
	}

}
