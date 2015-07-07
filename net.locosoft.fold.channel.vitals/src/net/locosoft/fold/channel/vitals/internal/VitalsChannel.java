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

package net.locosoft.fold.channel.vitals.internal;

import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.vitals.IVitals;
import net.locosoft.fold.channel.vitals.IVitalsChannel;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.util.MarkdownComposer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.eclipsesource.json.JsonObject;

public class VitalsChannel extends AbstractChannel implements IVitalsChannel {

	private VitalsMonitor _vitalsMonitor = new VitalsMonitor(this);
	private TreeMap<String, IVitals> _idToVitals = new TreeMap<String, IVitals>();

	public Class<? extends IChannel> getChannelInterface() {
		return IVitalsChannel.class;
	}

	String[] getVitalsIds() {
		Set<String> idSet = _idToVitals.keySet();
		String[] ids = idSet.toArray(new String[] {});
		return ids;
	}

	IVitals getVitals(String id) {
		return _idToVitals.get(id);
	}

	public void init() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.fold.channel.vitals");
		for (IConfigurationElement configurationElement : configurationElements) {
			try {
				String vitalsId = configurationElement.getAttribute("id");
				Object extension = configurationElement
						.createExecutableExtension("implementation");
				IVitals vitals = (IVitals) extension;
				vitals.init(configurationElement);
				_idToVitals.put(vitalsId, vitals);
			} catch (ClassCastException ex) {
				ex.printStackTrace();
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}

		_vitalsMonitor.start();
	}

	public void fini() {
		_vitalsMonitor.stop();
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		MarkdownComposer md = new MarkdownComposer();
		md.line("# vitals", true);

		ChannelItemNode sketch = new ChannelItemNode(this, "Vitals");

		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			md.line("latest: " + sketch.getOrdinal(), true);
		} else if (path.segmentCount() == 2) {
			int vitalsOrdinal = Integer.parseInt(path.segment(1));
			JsonObject jsonObject = sketch.getOrdinalNode(vitalsOrdinal);
			md.json(jsonObject);
		}

		response.getWriter().println(md.getHtml());
	}

}
