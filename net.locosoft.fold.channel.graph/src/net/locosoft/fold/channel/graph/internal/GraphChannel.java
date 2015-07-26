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
import net.locosoft.fold.util.FoldUtil;

public class GraphChannel extends AbstractChannel implements IGraphChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IGraphChannel.class;
	}

	public void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String dotInput = "digraph G { hello -> world; world -> foo; world -> bar; }";
		graphPngImage(dotInput, request, response);
	}

	public void graphPngImage(String dotInput, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");

		FoldUtil.execCommand("/usr/bin/dot -Tpng", dotInput,
				response.getOutputStream());
	}

}
