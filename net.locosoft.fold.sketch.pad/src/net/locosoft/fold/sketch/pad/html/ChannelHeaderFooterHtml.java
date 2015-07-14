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
import java.io.PrintWriter;

import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.sketch.IHtmlSketch;
import net.locosoft.fold.util.MarkdownComposer;

public class ChannelHeaderFooterHtml extends AbstractChannelHtmlSketch {

	public ChannelHeaderFooterHtml(IChannelInternal channel) {
		super(channel);
	}

	public void writeHeader(PrintWriter writer) throws IOException {
		writeHeader(writer, IHtmlSketch.SIZE_BLOCK);
	}

	public void writeHeader(PrintWriter writer, int sizeHint)
			throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		writer.append("<html>\n<head>\n<title>fold channel: ");
		writer.append(getChannel().getChannelId());
		writer.append("</title>\n</head>\n<body>\n");

		MarkdownComposer md = new MarkdownComposer();
		String channelId = getChannel().getChannelId();
		String thingName = getChannel().getChannelData("thing", "name");
		md.line("fold / channel: **" + channelId + //
				"** / thing: **" + thingName + "**", true);
		writer.append(md.getHtml());
	}

	public void writeFooter(PrintWriter writer) throws IOException {
		writeFooter(writer, IHtmlSketch.SIZE_BLOCK);
	}

	public void writeFooter(PrintWriter writer, int sizeHint)
			throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		writer.append("\n</body>\n</html>\n");
	}

}
