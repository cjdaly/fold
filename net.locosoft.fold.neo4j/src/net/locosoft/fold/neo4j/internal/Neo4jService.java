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

package net.locosoft.fold.neo4j.internal;

import net.locosoft.fold.neo4j.ICypherTransaction;
import net.locosoft.fold.neo4j.INeo4jService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eclipsesource.json.JsonObject;

public class Neo4jService implements INeo4jService {

	private BundleContext _bundleContext;
	private Neo4jController _neo4jController;
	private ServiceRegistration<INeo4jService> _serviceRegistration;

	public Neo4jService(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_neo4jController = new Neo4jController();
	}

	public void start() {
		_serviceRegistration = _bundleContext.registerService(
				INeo4jService.class, this, null);
		_neo4jController.start();
	}

	public void stop() {
		_serviceRegistration.unregister();
		_neo4jController.stop();
	}

	public ICypherTransaction createCypherTransaction(String statement) {
		return new CypherTransaction(statement);
	}

	public void doCypher(ICypherTransaction cypher) {
		if (_neo4jController.isNeo4jReady()) {
			JsonObject response = Neo4jRestUtil.doPostJson(
					Neo4jRestUtil.CYPHER_URI, cypher.getRequest());
			((CypherTransaction) cypher).setResponse(response);
		}
	}

}
