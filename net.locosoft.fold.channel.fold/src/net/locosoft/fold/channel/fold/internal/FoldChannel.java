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

package net.locosoft.fold.channel.fold.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.ChannelUtil;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.fold.IFoldChannel;
import net.locosoft.fold.sketch.ISketchService;
import net.locosoft.fold.sketch.SketchUtil;
import net.locosoft.fold.sketch.pad.neo4j.INeo4jCounterProperty;
import net.locosoft.fold.util.MarkdownComposer;

import org.eclipse.core.runtime.Path;

import com.github.rjeschke.txtmark.Processor;

public class FoldChannel extends AbstractChannel implements IFoldChannel {

	private long _startCount = -1;

	public long getStartCount() {
		return _startCount;
	}

	public Class<? extends IChannel> getChannelInterface() {
		return IFoldChannel.class;
	}

	public void init() {
		ISketchService sketchService = SketchUtil.getSketchService();
		INeo4jCounterProperty neo4jCounterProperty = sketchService
				.constructSketch(INeo4jCounterProperty.class);

		long channelNodeId = getChannelNodeId();
		neo4jCounterProperty.init(channelNodeId);
		_startCount = neo4jCounterProperty.incrementCounter("fold_startCount");
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if ((pathInfo == null) || ("".equals(pathInfo))
				|| ("/".equals(pathInfo))) {
			// empty channel segment (/fold)
			channelHttpDashboard(request, response);
		} else {
			Path path = new Path(pathInfo);
			String channelSegment = path.segment(0);
			if ("fold".equals(channelSegment)) {
				// fold channel segment (/fold/fold)
				channelHttpInternals(request, response);
			} else {
				// everything else
				channelHttpUndefined(request, response);
			}
		}
	}

	private void channelHttpDashboard(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		MarkdownComposer md = new MarkdownComposer();
		md.line("# fold", true);
		md.line("startCount: " + getStartCount(), true);
		md.line("### channels", true);

		md.table();
		IChannel[] channels = ChannelUtil.getChannelService().getAllChannels();
		for (IChannel channel : channels) {
			String channelLink = md.makeA("/fold/" + channel.getChannelId(),
					channel.getChannelId());
			md.tr(channelLink, "foo");
		}
		md.table(false);

		response.getWriter().println(md.getHtml());
	}

	private void channelHttpInternals(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String htmlText = Processor.process("**fold** internals: "
				+ getChannelId() + " / " + getChannelNodeId() + " * "
				+ getChannelProject().getFullPath());
		response.getWriter().println(htmlText);
	}

	private void channelHttpUndefined(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String htmlText = Processor.process("**fold** undefined: "
				+ getChannelId() + " / " + getChannelNodeId() + " * "
				+ getChannelProject().getFullPath());
		response.getWriter().println(htmlText);
	}

}
