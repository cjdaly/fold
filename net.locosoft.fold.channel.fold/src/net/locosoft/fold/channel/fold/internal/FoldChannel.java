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
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.CounterPropertyNode;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.util.HtmlComposer;

import org.eclipse.core.runtime.Path;

import com.eclipsesource.json.JsonObject;

public class FoldChannel extends AbstractChannel implements IFoldChannel {

	private FoldFinder _foldFinder = new FoldFinder(this);

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

		_foldFinder.start();
	}

	public void fini() {
		_foldFinder.stop();
	}

	public void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if ((pathInfo == null) || ("".equals(pathInfo))
				|| ("/".equals(pathInfo))) {
			// empty channel segment (/fold)
			new ChannelHttpGetDashboard()
					.composeHtmlResponse(request, response);
		} else {
			Path path = new Path(pathInfo);
			String channelSegment = path.segment(0);
			if ("fold".equals(channelSegment)) {
				// fold channel segment (/fold/fold)
				new ChannelHttpGetFold().composeHtmlResponse(request, response);
			} else {
				// everything else
				new ChannelHttpGetUndefined().composeHtmlResponse(request,
						response);
			}
		}
	}

	private class ChannelHttpGetDashboard extends ChannelHeaderFooterHtml {
		ChannelHttpGetDashboard() {
			super(FoldChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.h(2, "fold");

			html.p();
			html.text("Thing: ");
			String thingName = getChannelService().getChannelData("thing",
					"name");
			html.b(thingName);
			html.br();
			html.text("start count: ");
			html.b(Long.toString(getStartCount()));
			html.p(false);

			html.h(3, "channels");

			html.table();
			IChannel[] channels = ChannelUtil.getChannelService()
					.getAllChannels();
			for (IChannel channel : channels) {
				String channelLink = html.A("/fold/" + channel.getChannelId(),
						channel.getChannelId());
				html.tr(channelLink,
						channel.getChannelData("channel.description"));
			}
			html.table(false);
		}
	}

	private class ChannelHttpGetFold extends ChannelHeaderFooterHtml {
		public ChannelHttpGetFold() {
			super(FoldChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.h(2, "fold finder");

			HierarchyNode foldChannelNode = new HierarchyNode(
					getChannelNodeId());
			long subnetsNodeId = foldChannelNode.getSubId("subnets", true);
			HierarchyNode subnetsNode = new HierarchyNode(subnetsNodeId);

			for (String subSegment : subnetsNode.getSubSegments()) {
				html.h(4, "subnet prefix: " + subSegment);
				long prefixNodeId = subnetsNode.getSubId(subSegment, false);
				HierarchyNode prefixNode = new HierarchyNode(prefixNodeId);

				html.table();
				for (int i = 0; i < 32; i++) {
					html.tr();
					for (int j = 0; j < 8; j++) {
						String segment = Integer.toString(i * 8 + j);
						JsonObject jsonObject = prefixNode.getSubNode(segment);
						if (jsonObject == null) {
							html.td("?");
						} else {
							StringBuilder sb = new StringBuilder();
							long lastSend_chatterItemOrdinal = jsonObject
									.getLong("lastSend_chatterItemOrdinal", -1);
							if (lastSend_chatterItemOrdinal == -1) {
								sb.append("out");
							} else {
								sb.append(html.A("/fold/chatter/"
										+ lastSend_chatterItemOrdinal, "out"));
							}

							sb.append(",");

							long lastReceive_chatterItemOrdinal = jsonObject
									.getLong("lastReceive_chatterItemOrdinal",
											-1);
							if (lastReceive_chatterItemOrdinal == -1) {
								sb.append("in");
							} else {
								sb.append(html.A("/fold/chatter/"
										+ lastReceive_chatterItemOrdinal, "in"));
							}

							html.td(sb.toString());
						}
					}
					html.tr(false);
				}
				html.table(false);
			}
		}
	}

	private class ChannelHttpGetUndefined extends ChannelHeaderFooterHtml {
		public ChannelHttpGetUndefined() {
			super(FoldChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.h(2, "fold");
			html.p("...undefined...");
		}
	}

	protected void channelHttpPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");

		response.getWriter().println("Posting to fold...");

		switch (request.getPathInfo()) {
		case "/fold":
			_foldFinder.receiveFoldPost(request, response);
			break;
		default:
			super.channelHttpPost(request, response);
			break;
		}
	}

}
