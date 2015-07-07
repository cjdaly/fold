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

public class OrdinalNode extends AbstractNodeSketch {

	private String _ordinalLabel;
	private CounterPropertyNode _ordinalCounter;

	public OrdinalNode(long nodeId, String ordinalLabel) {
		_nodeId = nodeId;
		_ordinalLabel = ordinalLabel;
		_ordinalCounter = new CounterPropertyNode(nodeId);
	}

	protected String getOrdinalLabel() {
		return _ordinalLabel;
	}

	protected String getCounterPropertyName() {
		return "fold_OrdinalCounter_" + getOrdinalLabel();
	}

	protected String getIndexPropertyName() {
		return "fold_OrdinalIndex_" + getOrdinalLabel();
	}

	public long nextOrdinalNodeId() {
		long ordinalIndex = _ordinalCounter
				.incrementCounter(getCounterPropertyName());
		INeo4jService neo4jService = getNeo4jService();

		if (ordinalIndex == 1) {
			String constraintCypherText = "CREATE CONSTRAINT ON (ordinal:"
					+ getOrdinalLabel() + ") ASSERT ordinal.`"
					+ getIndexPropertyName() + "` IS UNIQUE";
			ICypher constraintCypher = neo4jService
					.constructCypher(constraintCypherText);
			neo4jService.invokeCypher(constraintCypher);
		}

		String cypherText = "CREATE (ordinal:" + getOrdinalLabel() + " { `"
				+ getIndexPropertyName() + "`: {ordinalIndex} })"
				+ " RETURN ID(ordinal)";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("ordinalIndex", ordinalIndex);

		neo4jService.invokeCypher(cypher);
		JsonValue jsonValue = cypher.getResultDataRow(0);
		return jsonValue.asLong();
	}

	public long getOrdinalNodeId(long ordinalIndex) {
		INeo4jService neo4jService = getNeo4jService();

		String cypherText = "MATCH (ordinal:" + getOrdinalLabel() + " { `"
				+ getIndexPropertyName() + "`: {ordinalIndex} })"
				+ " RETURN ID(ordinal)";
		ICypher cypher = neo4jService.constructCypher(cypherText);
		cypher.addParameter("ordinalIndex", ordinalIndex);

		neo4jService.invokeCypher(cypher);
		JsonValue jsonValue = cypher.getResultDataRow(0);
		return jsonValue.asLong();
	}

}
