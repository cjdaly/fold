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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class Neo4jServiceUtil {

	public static INeo4jService getNeo4jService() {
		Bundle bundle = FrameworkUtil.getBundle(Neo4jServiceUtil.class);
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<INeo4jService> serviceReference = bundleContext
				.getServiceReference(INeo4jService.class);
		INeo4jService neo4jService = bundleContext.getService(serviceReference);
		return neo4jService;
	}
}
