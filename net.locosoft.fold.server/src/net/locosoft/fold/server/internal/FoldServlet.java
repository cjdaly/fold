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

package net.locosoft.fold.server.internal;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.locosoft.fold.neo4j.ICypherTransaction;
import net.locosoft.fold.neo4j.INeo4jService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class FoldServlet implements Servlet {

	private ServletConfig _servletConfig;
	private INeo4jService _neo4jService;

	public void init(ServletConfig servletConfig) throws ServletException {
		_servletConfig = servletConfig;

		Bundle bundle = FrameworkUtil.getBundle(getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<INeo4jService> neo4jServiceReference = bundleContext
				.getServiceReference(INeo4jService.class);
		_neo4jService = bundleContext.getService(neo4jServiceReference);

		System.out.println("init FoldServlet");
	}

	public ServletConfig getServletConfig() {
		return _servletConfig;
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {

		ICypherTransaction cypherTransaction = _neo4jService
				.createCypherTransaction("RETURN timestamp()");
		_neo4jService.doCypher(cypherTransaction);
		System.out.println("CYPHER: " + cypherTransaction.getResponse());

		System.out.println("service FoldServlet");
	}

	public String getServletInfo() {
		return "fold";
	}

	public void destroy() {
		System.out.println("destroy FoldServlet");
	}

}
