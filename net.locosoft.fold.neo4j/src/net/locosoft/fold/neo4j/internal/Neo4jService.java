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

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;

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

	public boolean isNeo4jReady() {
		return _neo4jController.isNeo4jReady();
	}

	public int getNeo4jPID() {
		return _neo4jController.getNeo4jPID();
	}

	public ICypher constructCypher() {
		return constructCypher(null);
	}

	public ICypher constructCypher(String statement) {
		return new Cypher(statement);
	}

	public void invokeCypher(ICypher cypher) {
		invokeCypher(cypher, true);
	}

	public void invokeCypher(ICypher cypher, boolean logErrors) {
		if (_neo4jController.isNeo4jReady()) {

			try {
				Thread.sleep(getPreInvokeDelay(cypher));
			} catch (InterruptedException ex) {
				//
			}

			JsonObject response = Neo4jRestUtil.doPostJson(
					Neo4jRestUtil.CYPHER_URI, cypher.getRequest());
			((Cypher) cypher).setResponse(response);

			try {
				Thread.sleep(getPostInvokeDelay(cypher));
			} catch (InterruptedException ex) {
				//
			}

			if ((cypher.getErrorCount() > 0) && logErrors) {
				System.out.println("------");
				System.out.println("Cypher In:");
				System.out.println(cypher.getRequest().toString(
						WriterConfig.PRETTY_PRINT));
				System.out.println("------");
				System.out.println("Cypher Out:");
				System.out.println(cypher.getResponse().toString(
						WriterConfig.PRETTY_PRINT));
				System.out.println("------");
			}
		}
	}

	private long getPreInvokeDelay(ICypher cypher) {
		String cypherText = cypher.getStatementText();
		if (cypherText.contains("CREATE CONSTRAINT ON")) {
			return 2000;
		}
		if (cypherText.contains("CREATE UNIQUE")) {
			return 100;
		}

		return 0;
	}

	private long getPostInvokeDelay(ICypher cypher) {
		String cypherText = cypher.getStatementText();
		if (cypherText.contains("CREATE CONSTRAINT ON")) {
			return 8000;
		}
		if (cypherText.contains("CREATE UNIQUE")) {
			return 300;
		}

		return 0;
	}

}
