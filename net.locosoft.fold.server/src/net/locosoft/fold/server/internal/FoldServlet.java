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

import net.locosoft.fold.channel.ChannelServiceUtil;
import net.locosoft.fold.channel.IChannelService;

@SuppressWarnings("serial")
public class FoldServlet extends HttpServlet {

	private IChannelService _channelService;

	public String getServletInfo() {
		return "fold";
	}

	public void init() throws ServletException {
		_channelService = ChannelServiceUtil.getChannelService();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_channelService.doHttp(request, response);
	}

	protected void doHead(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_channelService.doHttp(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_channelService.doHttp(request, response);
	}

	protected void doPut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_channelService.doHttp(request, response);
	}

	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_channelService.doHttp(request, response);
	}

}
