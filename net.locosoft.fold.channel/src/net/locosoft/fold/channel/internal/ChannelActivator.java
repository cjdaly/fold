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

import net.locosoft.fold.util.FoldUtil;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ChannelActivator implements BundleActivator {

	private ChannelService _channelService;

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("fold internal version: "
				+ FoldUtil.getFoldInternalVersion());

		System.out.println("apparent fold URLs:");
		String[] foldUrls = FoldUtil.getFoldUrls();
		for (String foldUrl : foldUrls) {
			System.out.println("  " + foldUrl);
		}

		_channelService = new ChannelService(bundleContext);
		_channelService.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("stopping ChannelActivator");
		_channelService.stop();
	}

}
