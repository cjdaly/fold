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
import net.locosoft.fold.neo4j.Neo4jUtil;
import net.locosoft.fold.sketch.AbstractSketch;
import net.locosoft.fold.sketch.ISketch;

public interface INeo4jCounterProperty extends ISketch {

	void init(long nodeId);

	long incrementCounter(String counterPropertyName);

	class Impl extends AbstractSketch implements INeo4jCounterProperty {

		private long _nodeId;

		public Class<? extends ISketch> getSketchInterface() {
			return INeo4jCounterProperty.class;
		}

		public void init(long nodeId) {
			_nodeId = nodeId;
		}

		public long incrementCounter(String counterPropertyName) {
			INeo4jService neo4jService = Neo4jUtil.getNeo4jService();
			String cypherText = "MATCH node WHERE id(node)={nodeId} SET node.`"
					+ counterPropertyName + "`=COALESCE(node.`"
					+ counterPropertyName + "`, 0) + 1 RETURN node.`"
					+ counterPropertyName + "`";
			ICypher cypher = neo4jService.constructCypher(cypherText);
			cypher.addParameter("nodeId", _nodeId);
			neo4jService.invokeCypher(cypher);
			JsonValue jsonValue = cypher.getResultDataRow(0);
			return jsonValue.asLong();
		}
	}
}
