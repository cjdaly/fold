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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.neo4j.ICypherTransaction;
import net.locosoft.fold.neo4j.INeo4jService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.eclipsesource.json.WriterConfig;

@SuppressWarnings("serial")
public class FoldServlet extends HttpServlet {

	private INeo4jService _neo4jService;

	public void init() throws ServletException {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<INeo4jService> neo4jServiceReference = bundleContext
				.getServiceReference(INeo4jService.class);
		_neo4jService = bundleContext.getService(neo4jServiceReference);

		System.out.println("init FoldServlet");
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ICypherTransaction cypherTransaction = _neo4jService
				.createCypherTransaction("RETURN timestamp()");

		_neo4jService.doCypher(cypherTransaction);

		response.setContentType("text/plain");

		response.getWriter().println("Request Json");
		cypherTransaction.getRequest().writeTo(response.getWriter(),
				WriterConfig.PRETTY_PRINT);

		response.getWriter().println();
		response.getWriter().println("------");

		response.getWriter().println("Response Json");
		cypherTransaction.getResponse().writeTo(response.getWriter(),
				WriterConfig.PRETTY_PRINT);
	}

	public String getServletInfo() {
		return "fold";
	}

}
