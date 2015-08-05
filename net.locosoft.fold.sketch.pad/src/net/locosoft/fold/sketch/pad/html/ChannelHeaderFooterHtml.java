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

package net.locosoft.fold.sketch.pad.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.util.HtmlComposer;

public abstract class ChannelHeaderFooterHtml extends AbstractChannelHtmlSketch {

	public ChannelHeaderFooterHtml(IChannelInternal channel) {
		super(channel);
	}

	public void composeHtmlResponse(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String thingName = getChannel().getChannelService().getChannelData(
				"thing", "name");

		HtmlComposer html = new HtmlComposer(response.getWriter());
		html.html_head("fold: " + thingName + " / "
				+ getChannel().getChannelId());

		composeHtmlResponseBody(request, response, html);

		html.html_body(false);
	}

	protected abstract void composeHtmlResponseBody(HttpServletRequest request,
			HttpServletResponse response, HtmlComposer html)
			throws ServletException, IOException;
}
