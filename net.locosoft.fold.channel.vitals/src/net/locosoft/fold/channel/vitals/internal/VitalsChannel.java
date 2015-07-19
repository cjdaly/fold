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
import net.locosoft.fold.channel.vitals.StaticVitals;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.PropertyAccessNode;
import net.locosoft.fold.util.MarkdownComposer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

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
				StaticVitals vitals = (StaticVitals) extension;
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

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		ChannelHeaderFooterHtml htmlHeaderFooterSketch = new ChannelHeaderFooterHtml(
				this);
		htmlHeaderFooterSketch.writeHeader(response.getWriter());

		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			VitalsHtml vitalsHtmlSketch = new VitalsHtml(this, "Vitals", -1); // latest
			vitalsHtmlSketch.writeBlock(response.getWriter());
		} else if (path.segmentCount() == 2) {
			if ("defs".equals(path.segment(1))) {
				// TODO
			} else {
				long vitalsOrdinal = Long.parseLong(path.segment(1));
				VitalsHtml vitalsHtmlSketch = new VitalsHtml(this, "Vitals",
						vitalsOrdinal);
				vitalsHtmlSketch.writeBlock(response.getWriter());
			}
		} else if ((path.segmentCount() == 3)
				&& ("defs".equals(path.segment(1)))) {
			String vitalsId = path.segment(2);
			HierarchyNode channelNode = new HierarchyNode(getChannelNodeId());
			long defsNodeId = channelNode.getSubId("defs", true);
			HierarchyNode defsNode = new HierarchyNode(defsNodeId);
			long vitalsNodeId = defsNode.getSubId(vitalsId, true);
			HierarchyNode vitalsNode = new HierarchyNode(vitalsNodeId);

			MarkdownComposer md = new MarkdownComposer();
			md.line("### VitalsDef " + vitalsId, true);
			md.table();
			md.tr("id", "datatype", "units", "name", "description");

			long[] subIds = vitalsNode.getSubIds();
			for (long subId : subIds) {
				PropertyAccessNode props = new PropertyAccessNode(subId);
				String id = props.getStringValue("fold_Hierarchy_segment");
				String datatype = props.getStringValue("datatype");
				String units = props.getStringValue("units");
				String name = props.getStringValue("name");
				String description = props.getStringValue("description");
				md.tr(id, datatype, units, name, description);
			}
			md.table(false);
			response.getWriter().write(md.getHtml());
		}

		htmlHeaderFooterSketch.writeFooter(response.getWriter());
	}

	protected void channelHttpPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");

		response.getWriter().println("Posting to vitals...");
		VitalsPostHtml vitalsPost = new VitalsPostHtml(this);

		switch (request.getPathInfo()) {
		case "/vitals":
			vitalsPost.postVitals(request, response);
			break;
		case "/vitals/defs":
			vitalsPost.postVitalsDef(request, response);
			break;
		default:
			response.getWriter().println(
					"POST to invalid path: " + request.getPathInfo());
			break;
		}
	}

}
