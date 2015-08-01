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

package net.locosoft.fold.channel.thing.internal;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.thing.IThingChannel;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.PropertyAccessNode;
import net.locosoft.fold.util.HtmlComposer;

import org.eclipse.core.runtime.Path;

public class ThingChannel extends AbstractChannel implements IThingChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IThingChannel.class;
	}

	private String _thingProfileId;
	private String _thingName;
	private String _thingDescription;

	public void init() {

		PropertyAccessNode sketch = new PropertyAccessNode(getChannelNodeId());
		String savedProfileId = sketch.getStringValue("Thing_profileId");

		String startProfileId = System
				.getProperty("net.locosoft.fold.channel.thing.profile.id");

		String profileStatusMessage;
		if (startProfileId != null && !"".equals(startProfileId)
				&& !"default".equals(startProfileId)) {
			sketch.setValue("Thing_profileId", startProfileId);
			_thingProfileId = startProfileId;
			profileStatusMessage = "saved ";
		} else if (savedProfileId == null) {
			_thingProfileId = "default";
			profileStatusMessage = "";
		} else {
			_thingProfileId = savedProfileId;
			profileStatusMessage = "loaded ";
		}

		Properties config = getChannelConfigProperties(_thingProfileId);
		_thingName = config.getProperty("thing.name");
		_thingDescription = config.getProperty("thing.description");

		System.out.println("fold Thing: " + getThingName() + " ("
				+ profileStatusMessage + "profileId: " + _thingProfileId + ")");
	}

	public String getThingProfileId() {
		return _thingProfileId;
	}

	public String getThingName() {
		return _thingName;
	}

	public String getThingDescription() {
		return _thingDescription;
	}

	public String getChannelData(String key) {
		switch (key) {
		case "name":
			return getThingName();
		case "description":
			return getThingDescription();
		default:
			return super.getChannelData(key);
		}
	}

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			new ChannelHttpGetThing().composeHtmlResponse(request, response);
		} else {
			super.channelHttpGet(request, response);
		}
	}

	private class ChannelHttpGetThing extends ChannelHeaderFooterHtml {
		public ChannelHttpGetThing() {
			super(ThingChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.h(4, "Thing details");
			html.table();
			html.tr("profileId", getThingProfileId());
			html.tr("name", getThingName());
			html.tr("description", getThingDescription());
			html.table(false);
		}
	}
}
