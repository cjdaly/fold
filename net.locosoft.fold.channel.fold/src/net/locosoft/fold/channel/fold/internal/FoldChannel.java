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
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.fold.IFoldChannel;

import org.eclipse.core.runtime.Path;

import com.github.rjeschke.txtmark.Processor;

public class FoldChannel extends AbstractChannel implements IFoldChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IFoldChannel.class;
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		String tag;
		if ((pathInfo == null) || ("".equals(pathInfo))
				|| ("/".equals(pathInfo))) {
			// empty channel segment (/fold)
			tag = "(main)";
		} else {
			Path path = new Path(pathInfo);
			String channelSegment = path.segment(0);
			if ("fold".equals(channelSegment)) {
				// fold channel segment (/fold/fold)
				tag = "(fold)";
			} else {
				// everything else
				tag = "(default)";
			}
		}

		response.setContentType("text/html");
		String htmlText = Processor.process("**fold** channel: "
				+ getChannelId() + " / " + getChannelNodeId() + " * "
				+ getChannelProject().getFullPath() + " " + tag);
		response.getWriter().println(htmlText);
	}

}
