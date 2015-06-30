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

package net.locosoft.fold.channel.internal;

import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.neo4j.Neo4jUtil;

public class ChannelController implements Runnable {

	private ChannelService _channelService;

	private Thread _thread;
	private boolean _stopping = false;
	private boolean _stopped = true;

	private boolean _channelInit = false;

	public ChannelController(ChannelService channelService) {
		_channelService = channelService;
		_thread = new Thread(this);
	}

	public boolean isChannelReady() {
		return !(_stopped || _stopping) && _channelInit;
	}

	public void start() {
		_thread.start();
	}

	public void stop() {
		_stopping = true;
	}

	public void run() {
		while (!_stopping) {
			_stopped = false;
			try {
				if (!_channelInit) {
					if (Neo4jUtil.getNeo4jService().isNeo4jReady()) {
						initChannels();
						_channelInit = true;
					}
				}
				Thread.sleep(5 * 1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (_channelInit) {
			finiChannels();
			_channelInit = false;
		}

		_stopped = true;
	}

	private void initChannels() {
		for (IChannel channel : _channelService.getAllChannels()) {
			IChannelInternal channelInternal = (IChannelInternal) channel;
			channelInternal.init();
		}
	}

	private void finiChannels() {
		for (IChannel channel : _channelService.getAllChannels()) {
			IChannelInternal channelInternal = (IChannelInternal) channel;
			channelInternal.fini();
		}
	}

}
