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

import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jUtil;
import net.locosoft.fold.sketch.AbstractSketch;
import net.locosoft.fold.sketch.ISketch;

public interface INeo4jNodeSketch extends ISketch {

	void init(long nodeId);

	public abstract class Impl extends AbstractSketch implements
			INeo4jNodeSketch {

		private long _nodeId;
		private INeo4jService _neo4jService;

		public void init(long nodeId) {
			_nodeId = nodeId;
		}

		public long getNodeId() {
			return _nodeId;
		}

		public INeo4jService getNeo4jService() {
			if (_neo4jService == null) {
				_neo4jService = Neo4jUtil.getNeo4jService();
			}
			return _neo4jService;
		}
	}
}
