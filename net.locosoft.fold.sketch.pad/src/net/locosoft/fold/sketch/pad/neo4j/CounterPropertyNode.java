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

	private static final String _cypherTextLock = "MATCH node " //
			+ " WHERE id(node)={nodeId}" //
			+ " SET node.__lock=true" //
			+ " RETURN node.__lock";

	private long crementCounter(String counterPropertyName, boolean isIncrement) {
		INeo4jService neo4jService = getNeo4jService();

		// lock statement
		ICypher cypher = neo4jService.constructCypher(_cypherTextLock);
		cypher.addParameter("nodeId", getNodeId());

		// increment/decrement statement
		char crementOp = isIncrement ? '+' : '-';
		String cypherText = "MATCH node WHERE id(node)={nodeId}"
				+ " SET node.`" + counterPropertyName + "`=COALESCE(node.`"
				+ counterPropertyName + "`, 0) " + crementOp + " 1"
				+ " SET node.__lock=false" //
				+ " RETURN node.`" + counterPropertyName + "`";
		cypher.addStatement(cypherText);
		cypher.addParameter("nodeId", getNodeId());

		neo4jService.invokeCypher(cypher, false);

		while (cypher.getErrorCount() > 0) {
			int retryCount = 1;
			if (retryCount == 4)
				break;

			System.out.println("CounterPropertyNode.crementCounter retry: "
					+ retryCount);
			neo4jService.invokeCypher(cypher, false);

			retryCount++;
		}

		JsonValue jsonValue = cypher.getResultDataRow(1, 0);
		return jsonValue.asLong();
	}

	public long incrementCounter(String counterPropertyName) {
		return crementCounter(counterPropertyName, true);
	}

	public long decrementCounter(String counterPropertyName) {
		return crementCounter(counterPropertyName, false);
	}

	public long getCounter(String counterPropertyName) {
		PropertyAccessNode sketch = new PropertyAccessNode(getNodeId());
		JsonValue jsonValue = sketch.getValue(counterPropertyName);
		if ((jsonValue == null) || !jsonValue.isNumber())
			return 0;
		else
			return jsonValue.asLong();
	}

}
