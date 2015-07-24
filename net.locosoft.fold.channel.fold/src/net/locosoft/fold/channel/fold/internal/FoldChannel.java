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
import net.locosoft.fold.sketch.pad.neo4j.CounterPropertyNode;
import net.locosoft.fold.util.HtmlComposer;

import org.eclipse.core.runtime.Path;

public class FoldChannel extends AbstractChannel implements IFoldChannel {

	private long _startCount = -1;

	public long getStartCount() {
		return _startCount;
	}

	public Class<? extends IChannel> getChannelInterface() {
		return IFoldChannel.class;
	}

	public void init() {
		CounterPropertyNode foldStartCount = new CounterPropertyNode(
				getChannelNodeId());
		_startCount = foldStartCount.incrementCounter("fold_startCount");
	}

	public void channelHttpGet(HttpServletRequest request,
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
				channelHttpFold(request, response);
			} else {
				// everything else
				channelHttpUndefined(request, response);
			}
		}
	}

	private void channelHttpDashboard(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String thingName = getChannelData("thing", "name");

		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("fold: " + thingName);
		html.h(2, "fold");

		html.p();
		html.text("Thing: ");
		html.b(thingName);
		html.br();
		html.text("start count: ");
		html.b(Long.toString(getStartCount()));
		html.p(false);

		html.h(3, "channels");

		html.table();
		IChannel[] channels = ChannelUtil.getChannelService().getAllChannels();
		for (IChannel channel : channels) {
			String channelLink = html.A("/fold/" + channel.getChannelId(),
					channel.getChannelId());
			html.tr(channelLink, channel.getChannelData("channel.description"));
		}
		html.table(false);
		html.html_body(false);
	}

	private void channelHttpFold(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String thingName = getChannelData("thing", "name");

		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("fold: " + thingName);
		html.h(2, "fold");

		html.p("todo...");

		html.html_body(false);
	}

	private void channelHttpUndefined(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String thingName = getChannelData("thing", "name");

		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("fold: " + thingName);
		html.h(2, "fold");

		html.p("...undefined...");

		html.html_body(false);
	}

}
