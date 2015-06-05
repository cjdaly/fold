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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Neo4jActivator implements BundleActivator {

	private Neo4jService _neo4jService;

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("starting Neo4jActivator");
		_neo4jService = new Neo4jService(bundleContext);
		_neo4jService.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("stopping Neo4jActivator");
		_neo4jService.stop();
	}

}
