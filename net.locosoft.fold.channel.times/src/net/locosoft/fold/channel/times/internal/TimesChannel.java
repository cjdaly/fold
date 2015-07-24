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

package net.locosoft.fold.channel.times.internal;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;

public class TimesChannel extends AbstractChannel implements ITimesChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return ITimesChannel.class;
	}

	private long _epochNodeId = -1;

	public long getEpochNodeId() {
		if (_epochNodeId == -1) {
			HierarchyNode sketch = new HierarchyNode(getChannelNodeId());
			_epochNodeId = sketch.getSubId("ce", true);
		}
		return _epochNodeId;
	}

	public long getMinuteNodeId(long timeMillis, boolean createIfAbsent) {
		GetTimesNode sketch = new GetTimesNode(getEpochNodeId());
		return sketch.getMinuteNodeId(timeMillis, createIfAbsent);
	}

	public void createTimesRef(long timeMillis, long refNodeId) {
		long minuteNodeId = getMinuteNodeId(timeMillis, true);
		RefTimesNode sketch = new RefTimesNode(minuteNodeId);
		sketch.createTimesRef(refNodeId);
	}

	public long[] getTimesRefNodeIds(long timeMillis, String refNodeLabel) {
		return getTimesRefNodeIds(timeMillis, refNodeLabel, null, null);
	}

	public long[] getTimesRefNodeIds(long timeMillis, String refNodeLabel,
			String refNodeKey, String refNodeValue) {
		long minuteNodeId = getMinuteNodeId(timeMillis, false);
		if (minuteNodeId == -1) {
			return new long[0];
		} else {
			RefTimesNode sketch = new RefTimesNode(minuteNodeId);
			return sketch.getTimesRefNodeIds(refNodeLabel, refNodeKey,
					refNodeValue);
		}
	}

}
