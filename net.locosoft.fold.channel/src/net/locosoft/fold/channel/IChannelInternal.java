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

package net.locosoft.fold.channel;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IProject;

public interface IChannelInternal extends IChannel {

	IChannelService getChannelService();

	void init(String channelId, IChannelService channelService);

	void init();

	void fini();

	boolean channelSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	void channelHttp(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	String getChannelData(String key);

	String getChannelData(String channelId, String key);

	Properties getChannelConfigProperties(String propertiesFilePrefix);

	IProject getChannelProject();

	long getChannelNodeId();
}
