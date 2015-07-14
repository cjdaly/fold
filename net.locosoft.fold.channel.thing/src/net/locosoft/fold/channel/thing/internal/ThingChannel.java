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

public class ThingChannel extends AbstractChannel implements IThingChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IThingChannel.class;
	}

	private String _thingProfileId;
	private String _thingName;
	private String _thingDescription;

	public void init() {
		_thingProfileId = System.getProperty(
				"net.locosoft.fold.channel.thing.profile.id", "default");
		Properties config = getChannelConfigProperties(_thingProfileId);
		_thingName = config.getProperty("thing.name");
		_thingDescription = config.getProperty("thing.description");

		System.out.println("Thing profileId: " + _thingProfileId //
				+ ", name: " + _thingName);
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
			return null;
		}
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.channelHttp(request, response);
	}

}
