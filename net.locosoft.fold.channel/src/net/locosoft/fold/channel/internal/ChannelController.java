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
import net.locosoft.fold.util.MonitorThread;

public class ChannelController {

	private ChannelService _channelService;

	private boolean _channelInit = false;

	public ChannelController(ChannelService channelService) {
		_channelService = channelService;
	}

	public boolean isChannelReady() {
		return _channelMonitor.isRunning() && _channelInit;
	}

	public void start() {
		_channelMonitor.start();
	}

	public void stop() {
		_channelMonitor.stop();

		if (_channelInit) {
			finiChannels();
			_channelInit = false;
		}
	}

	private MonitorThread _channelMonitor = new MonitorThread() {
		protected long getSleepTimePreCycle() {
			return 2 * 1000;
		}

		protected long getSleepTimePostCycle() {
			return 2 * 1000;
		}

		public boolean cycle() {
			if (!_channelInit) {
				if (Neo4jUtil.getNeo4jService().isNeo4jReady()) {
					initChannels();
					_channelInit = true;
				}
			}
			return _channelInit;
		}
	};

	private void initChannels() {
		for (IChannel channel : _channelService.getAllChannels()) {
			IChannelInternal channelInternal = (IChannelInternal) channel;
			try {
				channelInternal.init();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void finiChannels() {
		for (IChannel channel : _channelService.getAllChannels()) {
			IChannelInternal channelInternal = (IChannelInternal) channel;
			try {
				channelInternal.fini();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
