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

import java.util.HashMap;

import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.channel.IChannelService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ChannelService implements IChannelService {

	private BundleContext _bundleContext;
	private ServiceRegistration<IChannelService> _serviceRegistration;

	private HashMap<String, IChannel> _idToChannel;
	private HashMap<Class<? extends IChannel>, IChannel> _ifaceToChannel;

	public ChannelService(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_idToChannel = new HashMap<String, IChannel>();
		_ifaceToChannel = new HashMap<Class<? extends IChannel>, IChannel>();
	}

	public void start() {
		initChannelMaps();

		_serviceRegistration = _bundleContext.registerService(
				IChannelService.class, this, null);
	}

	private void initChannelMaps() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.fold.channel");
		for (IConfigurationElement configurationElement : configurationElements) {

			try {
				String channelId = configurationElement.getAttribute("id");
				Object extension = configurationElement
						.createExecutableExtension("implementation");
				IChannel channel = (IChannel) extension;

				if (channel instanceof IChannelInternal) {
					IChannelInternal channelInternal = (IChannelInternal) channel;
					channelInternal.setIdFromExtensionRegistry(channelId);
					channelInternal.init();
				} else {
					// TODO: complain?
				}

				// TODO: check for duplicate id/iface use
				_idToChannel.put(channelId, channel);
				_ifaceToChannel.put(channel.getChannelInterface(), channel);

			} catch (ClassCastException ex) {
				ex.printStackTrace();
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void stop() {
		_serviceRegistration.unregister();
	}

	//
	// IChannelService
	//

	public IChannel[] getAllChannels() {
		return _idToChannel.values().toArray(new IChannel[0]);
	}

	public IChannel getChannel(String channelId) {
		return _idToChannel.get(channelId);
	}

	@SuppressWarnings("unchecked")
	public <T extends IChannel> T getChannel(Class<T> channelInterface) {
		return (T) _ifaceToChannel.get(channelInterface);
	}
}
