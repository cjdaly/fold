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

public class CounterPropertyNode extends AbstractNodeSketch {

	public CounterPropertyNode(long nodeId) {
		_nodeId = nodeId;
	}

	private long crementCounter(String counterPropertyName, boolean isIncrement) {
		INeo4jService neo4jService = getNeo4jService();
		char crementOp = isIncrement ? '+' : '-';
		String cypherText = "MATCH node WHERE id(node)={nodeId}"
				+ " SET node.`" + counterPropertyName + "`=COALESCE(node.`"
				+ counterPropertyName + "`, 0) " + crementOp + " 1"
				+ " RETURN node.`" + counterPropertyName + "`";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("nodeId", getNodeId());
		neo4jService.invokeCypher(cypher);
		JsonValue jsonValue = cypher.getResultDataRow(0);
		return jsonValue.asLong();
	}

	public long incrementCounter(String counterPropertyName) {
		return crementCounter(counterPropertyName, true);
	}

	public long decrementCounter(String counterPropertyName) {
		return crementCounter(counterPropertyName, false);
	}

}
