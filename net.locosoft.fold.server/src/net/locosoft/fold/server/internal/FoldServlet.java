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
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.IChannelService;
import net.locosoft.fold.neo4j.ICypherTransaction;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jServiceUtil;
import net.locosoft.fold.sketch.ISketch;
import net.locosoft.fold.sketch.ISketchService;
import net.locosoft.fold.sketch.SketchServiceUtil;

import com.eclipsesource.json.WriterConfig;

@SuppressWarnings("serial")
public class FoldServlet extends HttpServlet {

	private INeo4jService _neo4jService;
	private IChannelService _channelService;
	private ISketchService _sketchService;

	public void init() throws ServletException {
		_neo4jService = Neo4jServiceUtil.getNeo4jService();
		_channelService = ChannelServiceUtil.getChannelService();
		_sketchService = SketchServiceUtil.getSketchService();

		System.out.println("init FoldServlet");
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ICypherTransaction cypherTransaction = _neo4jService
				.createCypherTransaction("RETURN timestamp()");

		_neo4jService.doCypher(cypherTransaction);

		response.setContentType("text/plain");

		IChannel[] channels = _channelService.getAllChannels();
		for (IChannel channel : channels) {
			response.getWriter().println("channel: " + channel.getChannelId());
		}

		response.getWriter().println("------");

		ISketch[] sketches = _sketchService.getAllSketches();
		for (ISketch sketch : sketches) {
			response.getWriter().println("sketch: " + sketch.getSketchId());
		}

		response.getWriter().println("------");

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
