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

import com.eclipsesource.json.JsonObject;

import net.locosoft.fold.channel.IChannel;

public interface IChatterChannel extends IChannel {

	long createChatterItem(String message, String category, JsonObject data);

	long getLatestChatterOrdinal();

	JsonObject getChatterItem(long ordinal);

}
