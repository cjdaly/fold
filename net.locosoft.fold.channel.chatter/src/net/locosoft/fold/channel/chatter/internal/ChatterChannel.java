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

package net.locosoft.fold.channel.chatter.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Path;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.chatter.IChatterChannel;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;
import net.locosoft.fold.util.HtmlComposer;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

public class ChatterChannel extends AbstractChannel implements IChatterChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IChatterChannel.class;
	}

	public long createChatterItem(String message, String category,
			JsonObject data) {
		if (message == null)
			return -1;
		if (category == null)
			category = "";

		ChannelItemNode chatterItemNode = new ChannelItemNode(this, "Chatter");
		long chatterNodeId = chatterItemNode.nextOrdinalNodeId();
		long chatterNodeOrdinal = chatterItemNode
				.getOrdinalIndex(chatterNodeId);

		MultiPropertyAccessNode chatterNode = new MultiPropertyAccessNode(
				chatterNodeId);
		long chatterTime = System.currentTimeMillis();
		chatterNode.addProperty("Chatter_time", chatterTime);
		chatterNode.addProperty("Chatter_message", message);
		chatterNode.addProperty("Chatter_category", category);
		if (data != null) {
			for (String name : data.names()) {
				JsonValue jsonValue = data.get(name);
				if (jsonValue.isBoolean() || jsonValue.isNumber()
						|| jsonValue.isString()) {
					chatterNode.addProperty(name, jsonValue);
				}
			}
		}
		chatterNode.setProperties();

		ITimesChannel timesChannel = getChannelService().getChannel(
				ITimesChannel.class);
		timesChannel.createTimesRef(chatterTime, chatterNodeId);

		return chatterNodeOrdinal;
	}

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			channelHttpGetChatter(request, response);
		} else if (path.segmentCount() == 2) {
			channelHttpGetChatterItem(path.segment(1), request, response);
		} else {
			super.channelHttpGet(request, response);
		}

	}

	private void channelHttpGetChatter(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String thingName = getChannelData("thing", "name");

		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("fold: " + thingName + " / " + getChannelId());

		ChannelItemNode chatterItemNode = new ChannelItemNode(this, "Chatter");
		long latestChatterOrdinal = chatterItemNode.getLatestOrdinal();

		html.p();
		html.text("latest chatter: ");
		html.a("/fold/chatter/" + latestChatterOrdinal,
				Long.toString(latestChatterOrdinal));
		html.p(false);
		html.html_body(false);
	}

	private void channelHttpGetChatterItem(String chatterItemSegment,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		String thingName = getChannelData("thing", "name");

		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("fold: " + thingName + " / " + getChannelId());

		html.h(3, "chatter " + chatterItemSegment);

		JsonObject chatterItemJson = null;
		try {
			long chatterItemOrdinal = Long.parseLong(chatterItemSegment);
			ChannelItemNode chatterItemNode = new ChannelItemNode(this,
					"Chatter");
			chatterItemJson = chatterItemNode
					.getOrdinalNode(chatterItemOrdinal);
		} catch (NumberFormatException ex) {
			//
		}
		if (chatterItemJson == null) {
			html.p("item not found.");
		} else {
			html.pre(chatterItemJson.toString(WriterConfig.PRETTY_PRINT));
		}

		html.html_body(false);
	}
}
