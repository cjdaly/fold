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

import net.locosoft.fold.channel.IChannelInternal;

public class ChannelItemNode extends OrdinalNode {

	public ChannelItemNode(IChannelInternal channel, String itemLabel) {
		super(channel.getChannelNodeId(), channel.getChannelId() + "_"
				+ itemLabel);
	}

}
