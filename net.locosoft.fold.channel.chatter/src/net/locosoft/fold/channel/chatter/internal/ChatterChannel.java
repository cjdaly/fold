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

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.chatter.ChatterItemDetails;
import net.locosoft.fold.channel.chatter.IChatterChannel;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.IChannelItemDetails;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;
import net.locosoft.fold.util.HtmlComposer;

import org.eclipse.core.runtime.Path;

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

	public long getLatestChatterOrdinal() {
		ChannelItemNode chatterItemNode = new ChannelItemNode(this, "Chatter");
		return chatterItemNode.getLatestOrdinal();
	}

	public ChatterItemDetails getChatterItemDetails(long ordinal) {
		ChatterItemDetails chatterItemDetails = new ChatterItemDetails(this);
		if (chatterItemDetails.load(ordinal))
			return chatterItemDetails;
		else
			return null;
	}

	public IChannelItemDetails getChannelItemDetails(String itemLabel,
			long itemOrdinal) {
		if ("Chatter".equals(itemLabel)) {
			return getChatterItemDetails(itemOrdinal);
		}
		return null;
	}

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			new ChannelHttpGetChatter().composeHtmlResponse(request, response);
		} else if (path.segmentCount() == 2) {
			new ChannelHttpGetChatterItem(path.segment(1)).composeHtmlResponse(
					request, response);
		} else {
			super.channelHttpGet(request, response);
		}

	}

	private class ChannelHttpGetChatter extends ChannelHeaderFooterHtml {
		public ChannelHttpGetChatter() {
			super(ChatterChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			long latestChatterOrdinal = getLatestChatterOrdinal();

			html.p();
			html.text("latest chatter: ");
			html.a("/fold/chatter/" + latestChatterOrdinal,
					Long.toString(latestChatterOrdinal));
			html.p(false);
		}
	}

	private class ChannelHttpGetChatterItem extends ChannelHeaderFooterHtml {
		public ChannelHttpGetChatterItem(String chatterItemSegment) {
			super(ChatterChannel.this);
			_chatterItemSegment = chatterItemSegment;
		}

		private String _chatterItemSegment;

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {

			html.h(3, "chatter " + _chatterItemSegment);

			JsonObject chatterItemJson = null;
			try {
				long chatterItemOrdinal = Long.parseLong(_chatterItemSegment);
				ChatterItemDetails chatterItemDetails = getChatterItemDetails(chatterItemOrdinal);
				chatterItemJson = chatterItemDetails.getJson();
			} catch (NumberFormatException ex) {
				//
			}
			if (chatterItemJson == null) {
				html.p("item not found.");
			} else {
				html.pre(chatterItemJson.toString(WriterConfig.PRETTY_PRINT));
			}
		}
	}

}
