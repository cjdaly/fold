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

package net.locosoft.fold.sketch.pad.neo4j;

import com.eclipsesource.json.JsonObject;

import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.sketch.IChannelItemDetails;

public abstract class ChannelItemDetails implements IChannelItemDetails {

	private ChannelItemNode _channelItemNode;
	private long _ordinal;
	private JsonObject _jsonObject;

	public ChannelItemDetails(IChannelInternal channel, String itemLabel) {
		_channelItemNode = new ChannelItemNode(channel, itemLabel);
	}

	public String getItemLabel() {
		return _channelItemNode.getItemLabel();
	}

	public long getOrdinal() {
		return _ordinal;
	}

	public JsonObject getJson() {
		return _jsonObject;
	}

	public boolean load(long ordinal) {
		if (ordinal == -1) {
			_ordinal = _channelItemNode.getLatestOrdinal();
		} else {
			_ordinal = ordinal;
		}
		_jsonObject = _channelItemNode.getOrdinalNode(_ordinal);
		return _jsonObject != null;
	}

	public abstract String getUrlPath();
}
