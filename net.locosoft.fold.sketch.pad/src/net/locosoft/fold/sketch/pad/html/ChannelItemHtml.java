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

public abstract class ChannelItemHtml extends AbstractChannelHtmlSketch {

	private String _itemLabel;

	public ChannelItemHtml(IChannelInternal channel, String itemLabel) {
		super(channel);
		_itemLabel = itemLabel;
	}

	public String getItemLabel() {
		return _itemLabel;
	}

	public void writeBlock(PrintWriter writer) throws IOException {
		writeBlock(writer, IHtmlSketch.SIZE_BLOCK);
	}

	public abstract void writeBlock(PrintWriter writer, int sizeHint)
			throws IOException;

	public void writeInline(PrintWriter writer) throws IOException {
		writeInline(writer, IHtmlSketch.SIZE_INLINE);
	}

	public abstract void writeInline(PrintWriter writer, int sizeHint)
			throws IOException;
}
