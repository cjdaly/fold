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

import com.eclipsesource.json.JsonValue;

public class HierarchyNode extends AbstractNodeSketch {

	public HierarchyNode(long nodeId) {
		_nodeId = nodeId;
	}

	public void setNodeId(long nodeId) {
		_nodeId = nodeId;
	}

	public long getSupId() {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = "MATCH (sup)-[:fold_Hierarchy]->(sub)"
				+ " WHERE ID(sub)={subId}" //
				+ " RETURN ID(sup)";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("subId", getNodeId());
		neo4jService.invokeCypher(cypher);
		JsonValue jsonValue = cypher.getResultDataRow(0);
		if (jsonValue != null)
			return jsonValue.asLong();
		else
			return -1;
	}

	public long getSubId(String segment, boolean createIfAbsent) {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText;
		if (createIfAbsent) {
			cypherText = "MATCH sup" //
					+ " WHERE id(sup)={supId}" //
					+ " CREATE UNIQUE (sup)-[:fold_Hierarchy]->"
					+ "(sub{ fold_Hierarchy_segment:{segment} }) "
					+ " RETURN ID(sub)";
		} else {
			cypherText = "MATCH (sup)-[:fold_Hierarchy]->(sub)"
					+ " WHERE ID(sup)={supId} AND sub.fold_Hierarchy_segment={segment}" //
					+ " RETURN ID(sub)";
		}
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("supId", getNodeId());
		cypher.addParameter("segment", segment);
		neo4jService.invokeCypher(cypher);
		JsonValue jsonValue = cypher.getResultDataRow(0);
		if (jsonValue != null)
			return jsonValue.asLong();
		else
			return -1;
	}

	public long[] getSubIds() {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = "MATCH (sup)-[:fold_Hierarchy]->(sub)"
				+ " WHERE ID(sup)={supId}" //
				+ " RETURN ID(sub)";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("supId", getNodeId());
		neo4jService.invokeCypher(cypher);

		long[] subIds = new long[cypher.getResultDataRowCount()];
		for (int i = 0; i < cypher.getResultDataRowCount(); i++) {
			JsonValue jsonValue = cypher.getResultDataRow(i);
			subIds[i] = jsonValue.asLong();
		}
		return subIds;
	}

	public String[] getSubSegments() {
		INeo4jService neo4jService = getNeo4jService();
		String cypherText = "MATCH (sup)-[:fold_Hierarchy]->(sub)"
				+ " WHERE ID(sup)={supId}"
				+ " RETURN sub.fold_Hierarchy_segment";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("supId", getNodeId());
		neo4jService.invokeCypher(cypher);

		String[] subSegments = new String[cypher.getResultDataRowCount()];
		for (int i = 0; i < cypher.getResultDataRowCount(); i++) {
			JsonValue jsonValue = cypher.getResultDataRow(i);
			subSegments[i] = jsonValue.asString();
		}
		return subSegments;
	}

	// TODO:
	// public long[] getPathNodeIds(Path path) {
	//
	// }
	//
	// TODO:
	// public long getPathEndNodeId(Path path) {
	// return -1;
	// }

}
