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

package net.locosoft.fold.channel.vitals.internal;

import java.io.IOException;
import java.io.PrintWriter;

import com.eclipsesource.json.JsonObject;

import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.sketch.pad.html.ChannelItemHtml;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.util.MarkdownComposer;

public class VitalsHtml extends ChannelItemHtml {

	private ChannelItemNode _nodeSketch;
	private long _itemOrdinal;
	private JsonObject _itemJson;

	public VitalsHtml(IChannelInternal channel, String itemLabel,
			long itemOrdinal) {
		super(channel, "Vitals");
		_nodeSketch = new ChannelItemNode(channel, itemLabel);

		if (itemOrdinal == -1) {
			_itemOrdinal = _nodeSketch.getOrdinal();
		} else {
			_itemOrdinal = itemOrdinal;
		}
		_itemJson = _nodeSketch.getOrdinalNode(_itemOrdinal);
	}

	public void writeBlock(PrintWriter writer, int sizeHint) throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		MarkdownComposer md = new MarkdownComposer();
		if (_itemJson == null) {
			md.line("_(no data)_", true);
		} else {
			md.json(_itemJson);
		}
		writer.append(md.getHtml());
	}

	public void writeInline(PrintWriter writer, int sizeHint)
			throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		if (_itemJson == null) {
			writer.append("<em>(no data)</em>");
		} else {
			writer.append(_itemJson.toString());
		}
	}

}
