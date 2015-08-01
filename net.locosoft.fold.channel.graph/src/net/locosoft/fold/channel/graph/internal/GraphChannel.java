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

package net.locosoft.fold.channel.graph.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.graph.IGraphChannel;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.util.FoldUtil;
import net.locosoft.fold.util.HtmlComposer;

import org.eclipse.core.runtime.Path;

public class GraphChannel extends AbstractChannel implements IGraphChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IGraphChannel.class;
	}

	public void graphPngImage(String dotInput, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");

		FoldUtil.execCommand("/usr/bin/dot -Tpng", dotInput,
				response.getOutputStream());
	}

	public void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			new ChannelHttpGetGraph().composeHtmlResponse(request, response);
		} else if ((path.segmentCount() == 2)
				&& "test.png".equals(path.segment(1))) {
			String dotInput = "digraph G { hello -> world; world -> foo; world -> bar; }";
			graphPngImage(dotInput, request, response);
		} else {
			super.channelHttpGet(request, response);
		}
	}

	private class ChannelHttpGetGraph extends ChannelHeaderFooterHtml {
		public ChannelHttpGetGraph() {
			super(GraphChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.p("test graph:");
			html.img("/fold/graph/test.png", "test graph");
		}
	}

}
