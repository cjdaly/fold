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

package net.locosoft.fold.channel.times;

import net.locosoft.fold.channel.IChannel;

import com.eclipsesource.json.JsonObject;

public interface ITimesChannel extends IChannel {

	long getEpochNodeId();

	long getMinuteNodeId(long timeMillis, boolean createIfAbsent);

	void createTimesRef(long timeMillis, long refNodeId);

	long[] getTimesRefNodeIds(long timeMillis, String refNodeLabel);

	long[] getTimesRefNodeIds(long timeMillis, String refNodeLabel,
			String refNodeKey, String refNodeValue);

	JsonObject[] getTimesRefNodes(long timeMillis, String refNodeLabel,
			String refNodeKey, String refNodeValue);

}
