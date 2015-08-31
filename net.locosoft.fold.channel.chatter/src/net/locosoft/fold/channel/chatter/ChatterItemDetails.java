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

package net.locosoft.fold.channel.chatter;

import net.locosoft.fold.channel.chatter.internal.ChatterChannel;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemDetails;

public class ChatterItemDetails extends ChannelItemDetails {
	public ChatterItemDetails(ChatterChannel channel) {
		super(channel, "Chatter");
	}

	public String getItemKind() {
		return getCategory();
	}

	public long getTimestamp() {
		return getJson().getLong("Chatter_time", Long.MIN_VALUE);
	}

	public String getUrlPath() {
		return "/fold/chatter/" + getOrdinal();
	}

	public String getCategory() {
		return getJson().getString("Chatter_category", null);
	}

	public String getMessage() {
		return getJson().getString("Chatter_message", null);
	}

}
