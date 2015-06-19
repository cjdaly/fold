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

import net.locosoft.fold.channel.IChannelService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ChannelService implements IChannelService {

	// public ChannelService() {
	// IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
	// IConfigurationElement[] configurationElements = extensionRegistry
	// .getConfigurationElementsFor("");
	// for (IConfigurationElement configurationElement : configurationElements)
	// {
	// System.out.println(configurationElement.getName());
	// }
	// }

	private BundleContext _bundleContext;
	private ServiceRegistration<IChannelService> _serviceRegistration;

	public ChannelService(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	public void start() {
		_serviceRegistration = _bundleContext.registerService(
				IChannelService.class, this, null);
	}

	public void stop() {
		_serviceRegistration.unregister();
	}
}
