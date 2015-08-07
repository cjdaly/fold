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

package net.locosoft.fold.channel.example.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.example.IExampleChannel;
import net.locosoft.fold.util.HtmlComposer;

public class ExampleChannel extends AbstractChannel implements IExampleChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IExampleChannel.class;
	}

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("EXAMPLE");
		html.p("Hello from the example channel!");
		html.html_body(false);
	}

}
