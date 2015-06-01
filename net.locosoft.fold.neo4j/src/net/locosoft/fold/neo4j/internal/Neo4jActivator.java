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

import net.locosoft.fold.neo4j.INeo4jService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Neo4jActivator implements BundleActivator {

	private ServiceRegistration<INeo4jService> _serviceRegistration;

	public void start(BundleContext bundleContext) throws Exception {
		Neo4jService neo4jService = new Neo4jService();
		_serviceRegistration = bundleContext.registerService(
				INeo4jService.class, neo4jService, null);
		System.out.println("starting Neo4jActivator");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		_serviceRegistration.unregister();
		System.out.println("stopping Neo4jActivator");
	}

}
