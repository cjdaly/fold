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

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.channel.IChannelService;
import net.locosoft.fold.util.HtmlComposer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ChannelService implements IChannelService {

	private BundleContext _bundleContext;
	private ServiceRegistration<IChannelService> _serviceRegistration;

	private TreeMap<String, IChannel> _idToChannel;
	private HashMap<Class<? extends IChannel>, IChannel> _ifaceToChannel;

	private ChannelController _channelController;

	public ChannelService(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_idToChannel = new TreeMap<String, IChannel>();
		_ifaceToChannel = new HashMap<Class<? extends IChannel>, IChannel>();
		_channelController = new ChannelController(this);
	}

	private void initChannelMaps() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.fold.channel");
		for (IConfigurationElement configurationElement : configurationElements) {

			try {
				String channelId = configurationElement.getAttribute("id");
				String channelDescription = configurationElement
						.getAttribute("description");
				Object extension = configurationElement
						.createExecutableExtension("implementation");
				IChannel channel = (IChannel) extension;

				IChannelInternal channelInternal = (IChannelInternal) channel;
				channelInternal.init(channelId, this, channelDescription);

				// TODO: check for duplicates
				_idToChannel.put(channelId, channel);
				_ifaceToChannel.put(channel.getChannelInterface(), channel);

			} catch (ClassCastException ex) {
				ex.printStackTrace();
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void start() {
		initChannelMaps();
		_channelController.start();

		_serviceRegistration = _bundleContext.registerService(
				IChannelService.class, this, null);
	}

	public void stop() {
		_channelController.stop();
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

	public String getChannelData(String channelId, String key, String... params) {
		IChannelInternal channel = (IChannelInternal) getChannel(channelId);
		if (channel == null)
			return null;
		else
			return channel.getChannelData(key, params);
	}

	private IChannelInternal lookupChannel(String pathInfo) {
		IChannelInternal channelInternal = null;

		if ((pathInfo != null) && !("".equals(pathInfo))
				&& !("/".equals(pathInfo))) {
			Path path = new Path(pathInfo);
			String channelSegment = path.segment(0);
			channelInternal = (IChannelInternal) _idToChannel
					.get(channelSegment);
		}

		if (channelInternal == null) {
			channelInternal = (IChannelInternal) _idToChannel.get("fold");
		}

		return channelInternal;
	}

	public boolean channelSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!_channelController.isChannelReady()) {
			return true;
		} else {
			IChannelInternal channel = lookupChannel(request.getPathInfo());
			if (channel != null)
				return channel.channelSecurity(request, response);
			else
				return false;
		}
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (!_channelController.isChannelReady()) {
			response.setContentType("text/html");
			HtmlComposer html = new HtmlComposer(response.getWriter());
			html.html_head("fold");
			html.p("fold channel down! :-(");
			html.html_body(false);
		} else {
			IChannelInternal channel = lookupChannel(request.getPathInfo());
			if (channel != null)
				channel.channelHttp(request, response);
		}
	}

}
