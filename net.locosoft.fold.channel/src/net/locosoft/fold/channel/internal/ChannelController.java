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
import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jUtil;
import net.locosoft.fold.util.FoldUtil;
import net.locosoft.fold.util.MonitorThread;

public class ChannelController {

	private ChannelService _channelService;

	private boolean _channelInit = false;
	private boolean _channelFini = false;

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
	}

	private MonitorThread _channelMonitor = new MonitorThread() {
		protected long getSleepTimePreCycle() {
			return 1000;
		}

		protected long getSleepTimePostCycle() {
			return 1000;
		}

		public boolean cycle() throws Exception {
			if (!_channelInit) {
				if (Neo4jUtil.getNeo4jService().isNeo4jReady()) {
					preInitChannels();
					initChannels();
					_channelInit = true;
				}
			} else if (!_channelFini) {
				int foldPid = FoldUtil.getFoldPid();
				if (foldPid == -1) {
					System.out.println("fold stopping...");
					finiChannels();
					_channelFini = true;
					_channelService.getBundleContext().getBundle(0).stop();
				}
			}

			return !_channelFini;
		}
	};

	private void preInitChannels() {
		// see AbstractChannel.getChannelNodeId()
		INeo4jService neo4jService = Neo4jUtil.getNeo4jService();
		ICypher cypherConstraint = neo4jService
				.constructCypher("CREATE CONSTRAINT ON (channel:fold_Channel) ASSERT channel.fold_channelId IS UNIQUE");
		neo4jService.invokeCypher(cypherConstraint);
	}

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
