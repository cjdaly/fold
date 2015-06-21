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

package net.locosoft.fold.channel;

public abstract class AbstractChannel implements IChannel, IChannelInternal {

	private String _id;

	public String getChannelId() {
		return _id;
	}

	//
	// IChannelInternal
	//

	public void setIdFromExtensionRegistry(String id) {
		_id = id;
	}

	public void init() {
	}

	public void fini() {
	}

}
