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

import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.sketch.AbstractHtmlSketch;

public abstract class AbstractChannelHtmlSketch extends AbstractHtmlSketch {

	private IChannelInternal _channel;

	public AbstractChannelHtmlSketch(IChannelInternal channel) {
		_channel = channel;
	}

	protected IChannelInternal getChannel() {
		return _channel;
	}

	protected boolean isSizeEmpty(int sizeHint) {
		return (sizeHint == SIZE_EMPTY) //
				|| (sizeHint == SIZE_BLOCK_EMPTY) //
				|| (sizeHint == SIZE_INLINE_EMPTY);
	}

}
