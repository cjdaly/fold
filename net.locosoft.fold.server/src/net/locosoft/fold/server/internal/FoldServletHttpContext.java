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
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

public class FoldServletHttpContext implements HttpContext {

	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		System.out.println("securityCheck: " + request.getRequestURL());
		return true;
	}

	public String getMimeType(String name) {
		System.out.println("FoldServletHttpContext.getMimeType");
		return null;
	}

	public URL getResource(String name) {
		System.out.println("FoldServletHttpContext.getResource");
		return null;
	}

	public String toString() {
		return "FOO";
	}
}
