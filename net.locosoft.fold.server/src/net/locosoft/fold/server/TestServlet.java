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

package net.locosoft.fold.server;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TestServlet implements Servlet {

	public void init(ServletConfig config) throws ServletException {
		System.out.println("init TestServlet");
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		System.out.println("service TestServlet");
	}

	public String getServletInfo() {
		return null;
	}

	public void destroy() {
		System.out.println("destroy TestServlet");
	}

}
