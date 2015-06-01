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

package net.locosoft.fold.neo4j;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Neo4jActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Neo4jActivator.context = bundleContext;
		System.out.println("starting Neo4jActivator");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Neo4jActivator.context = null;
		System.out.println("stopping Neo4jActivator");
	}

}
