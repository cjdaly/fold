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

import java.net.URI;

import net.locosoft.fold.neo4j.INeo4jService;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Neo4jService implements INeo4jService {

	private BundleContext _bundleContext;
	private Neo4jController _neo4jController;
	private ServiceRegistration<INeo4jService> _serviceRegistration;

	public Neo4jService(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_neo4jController = new Neo4jController(this);
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

	public String getNeo4jHomeDir() {
		try {
			String eclipseLocation = System
					.getProperty("eclipse.home.location");
			IPath eclipsePath = new Path(new URI(eclipseLocation).getPath());
			IPath foldHomePath = eclipsePath.removeLastSegments(1)
					.removeTrailingSeparator();

			String neo4jVersion = System
					.getProperty("net.locosoft.fold.neo4j.version");
			IPath neo4jHomePath = foldHomePath.append("/neo4j/neo4j-community-"
					+ neo4jVersion);
			return neo4jHomePath.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
