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
import net.locosoft.fold.channel.plot.IPlotChannel;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.channel.vitals.DynamicVitals;
import net.locosoft.fold.channel.vitals.IVitals;
import net.locosoft.fold.channel.vitals.IVitalsChannel;
import net.locosoft.fold.channel.vitals.StaticVitals;
import net.locosoft.fold.channel.vitals.Vital;
import net.locosoft.fold.channel.vitals.VitalsItemDetails;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;
import net.locosoft.fold.util.HtmlComposer;
import net.locosoft.fold.util.JsonUtil;
import net.locosoft.fold.util.MonitorThread;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

public class VitalsChannel extends AbstractChannel implements IVitalsChannel {

	private TreeMap<String, IVitals> _idToVitals = new TreeMap<String, IVitals>();

	public Class<? extends IChannel> getChannelInterface() {
		return IVitalsChannel.class;
	}

	public long getLatestVitalsOrdinal() {
		ChannelItemNode vitalsItemNode = new ChannelItemNode(this, "Vitals");
		return vitalsItemNode.getLatestOrdinal();
	}

	public VitalsItemDetails getVitalsItemDetails(long ordinal) {
		VitalsItemDetails vitalsItemDetails = new VitalsItemDetails(this);
		if (vitalsItemDetails.load(ordinal))
			return vitalsItemDetails;
		else
			return null;
	}

	String[] getVitalsIds() {
		Set<String> idSet = _idToVitals.keySet();
		String[] ids = idSet.toArray(new String[] {});
		return ids;
	}

	public IVitals getVitals(String id) {
		IVitals vitals = _idToVitals.get(id);
		if (vitals == null) {
			vitals = getDynamicVitals(id);
			if (vitals != null) {
				_idToVitals.put(id, vitals);
			}
		}
		return vitals;
	}

	private IVitals getDynamicVitals(String vitalsId) {
		HierarchyNode channelNode = new HierarchyNode(getChannelNodeId());
		long defsNodeId = channelNode.getSubId("defs", true);
		HierarchyNode defsNode = new HierarchyNode(defsNodeId);
		long vitalsNodeId = defsNode.getSubId(vitalsId, true);

		DynamicVitals vitals = new DynamicVitals(vitalsId);
		vitals.loadVitals(vitalsNodeId);
		return vitals;
	}

	public void init() {
		// static vitals
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

		// dynamic vitals
		HierarchyNode channelNode = new HierarchyNode(getChannelNodeId());
		long defsNodeId = channelNode.getSubId("defs", true);
		HierarchyNode defsNode = new HierarchyNode(defsNodeId);
		String[] dynamicVitalsIds = defsNode.getSubSegments();
		for (String dynamicVitalsId : dynamicVitalsIds) {
			IVitals dynamicVitals = getDynamicVitals(dynamicVitalsId);
			if (dynamicVitals != null) {
				_idToVitals.put(dynamicVitalsId, dynamicVitals);
			}
		}

		_vitalsMonitor.start();
	}

	public void fini() {
		_vitalsMonitor.stop();
	}

	public String getChannelData(String key, String... params) {
		switch (key) {
		case "channelItem.urlPath":
			if (params.length != 1)
				return null;
			JsonObject jsonNode = JsonUtil.readJsonObject(params[0]);
			if (jsonNode == null)
				return null;
			ChannelItemNode vitalsItemNode = new ChannelItemNode(this, "Vitals");
			long ordinalIndex = vitalsItemNode.getOrdinalIndex(jsonNode);
			if (ordinalIndex == -1)
				return null;
			else
				return "/fold/vitals/" + ordinalIndex;
		default:
			return super.getChannelData(key, params);
		}
	}

	private MonitorThread _vitalsMonitor = new MonitorThread() {

		protected long getSleepTimePreCycle() {
			return 2 * 1000;
		}

		protected long getSleepTimePostCycle() {
			return 2 * 1000;
		}

		public boolean cycle() {
			String[] vitalsIds = getVitalsIds();
			long currentTimeMillis = System.currentTimeMillis();
			for (String vitalsId : vitalsIds) {
				try {
					IVitals vitals = getVitals(vitalsId);
					if (vitals instanceof StaticVitals) {
						StaticVitals staticVitals = (StaticVitals) vitals;
						if (staticVitals.isCheckTime(currentTimeMillis)) {
							ChannelItemNode vitalsNode = new ChannelItemNode(
									VitalsChannel.this, "Vitals");
							long vitalsNodeId = vitalsNode.nextOrdinalNodeId();
							staticVitals.checkVitals(vitalsNodeId);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			return true;
		}
	};

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			new ChannelHttpGetVitals().composeHtmlResponse(request, response);
		} else if (path.segmentCount() == 2) {
			if ("defs".equals(path.segment(1))) {
				new ChannelHttpGetVitalsDefs().composeHtmlResponse(request,
						response);
			} else {
				new ChannelHttpGetVitalsItem(path.segment(1))
						.composeHtmlResponse(request, response);
			}
		} else if ((path.segmentCount() == 3) && "defs".equals(path.segment(1))) {
			new ChannelHttpGetVitalsDef(path.segment(2)).composeHtmlResponse(
					request, response);
		} else if ((path.segmentCount() == 4) && "plot".equals(path.segment(1))) {
			new ChannelHttpGetVitalsPlot(path.segment(2), path.segment(3))
					.composeHtmlResponse(request, response);
		} else if ((path.segmentCount() == 5) && "plot".equals(path.segment(1))
				&& ("image.png".equals(path.segment(4)))) {
			plotVitalsImage(request, response);
		} else {
			super.channelHttpGet(request, response);
		}
	}

	private class ChannelHttpGetVitals extends ChannelHeaderFooterHtml {
		public ChannelHttpGetVitals() {
			super(VitalsChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {

			ChannelItemNode vitalsItemNode = new ChannelItemNode(
					VitalsChannel.this, "Vitals");
			long latestVitalsOrdinal = vitalsItemNode.getLatestOrdinal();

			html.p();
			html.text("latest vitals: ");
			html.a("/fold/vitals/" + latestVitalsOrdinal,
					Long.toString(latestVitalsOrdinal));
			html.p(false);

			html.p();
			html.text("vitals definitions: ");
			html.a("/fold/vitals/defs", "defs");
			html.p(false);

			html.table();
			for (String vitalsId : getVitalsIds()) {
				IVitals vitals = getVitals(vitalsId);
				for (Vital vital : vitals.getVitals()) {
					if (vital.isNumeric() && !vital.Internal) {
						String linkPath = "/fold/vitals/plot/" + vitals.getId()
								+ "/" + vital.Id + "/?skip=";
						html.tr(vitals.getId(), vital.Id,
								html.A(linkPath + "1", "1 minute"),
								html.A(linkPath + "5", "5 minutes"),
								html.A(linkPath + "10", "10 minutes"),
								html.A(linkPath + "15", "15 minutes"),
								html.A(linkPath + "30", "30 minutes"),
								html.A(linkPath + "60", "60 minutes"));
					}
				}
			}
			html.table(false);
		}
	}

	private class ChannelHttpGetVitalsPlot extends ChannelHeaderFooterHtml {
		public ChannelHttpGetVitalsPlot(String vitalsId, String vitalId) {
			super(VitalsChannel.this);
			_vitalsId = vitalsId;
			_vitalId = vitalId;
		}

		private String _vitalsId;
		private String _vitalId;

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			String imagePath = "/fold/vitals/plot/" + _vitalsId + "/"
					+ _vitalId + "/image.png";
			String altText = "plot of " + _vitalId + ":" + _vitalId;
			html.img(imagePath, altText);
		}
	}

	private void plotVitalsImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IPlotChannel plotChannel = getChannelService().getChannel(
				IPlotChannel.class);
		ITimesChannel timesChannel = getChannelService().getChannel(
				ITimesChannel.class);

		Path path = new Path(request.getPathInfo());
		String vitalsId = path.segment(2);
		String vitalId = path.segment(3);

		TreeMap<Long, JsonValue> timeSeriesDataMap = new TreeMap<Long, JsonValue>();

		long currentTimeMillis = System.currentTimeMillis();
		int sampleCount = 40;

		int skipParam;
		try {
			skipParam = Integer.parseInt(request.getParameter("skip"));
		} catch (NumberFormatException ex) {
			skipParam = 1;
		}

		int skipCount = 60 * 1000 * skipParam;
		long[] timeNodeIds = new long[sampleCount];
		for (int i = 0; i < timeNodeIds.length; i++) {
			long[] refNodeIds = timesChannel.getTimesRefNodeIds(
					currentTimeMillis, "vitals_Vitals",
					IVitals.NODE_PROPERTY_ID, vitalsId);
			for (long refNodeId : refNodeIds) {
				MultiPropertyAccessNode sketch = new MultiPropertyAccessNode(
						refNodeId);
				JsonObject jsonObject = sketch.getProperties();
				long checkTimeMillis = jsonObject.getLong(
						IVitals.NODE_PROPERTY_CHECK_TIME, -1);
				long checkTimeSeconds = checkTimeMillis / 1000;

				JsonValue jsonValue = jsonObject.get(vitalId);
				if (jsonValue.isNumber()) {
					timeSeriesDataMap.put(Long.valueOf(checkTimeSeconds),
							jsonValue);
				}
			}
			currentTimeMillis -= skipCount;
		}

		plotChannel.plotPngImage("Vitals: " + vitalsId + " / " + vitalId,
				"time", vitalId, vitalId, timeSeriesDataMap, request, response);
	}

	private class ChannelHttpGetVitalsItem extends ChannelHeaderFooterHtml {
		private String _vitalsItemSegment;

		public ChannelHttpGetVitalsItem(String vitalsItemSegment) {
			super(VitalsChannel.this);
			_vitalsItemSegment = vitalsItemSegment;
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.h(3, "vitals " + _vitalsItemSegment);

			JsonObject vitalsItemJson = null;
			try {
				long vitalsItemOrdinal = Long.parseLong(_vitalsItemSegment);
				ChannelItemNode vitalsItemNode = new ChannelItemNode(
						VitalsChannel.this, "Vitals");
				vitalsItemJson = vitalsItemNode
						.getOrdinalNode(vitalsItemOrdinal);
			} catch (NumberFormatException ex) {
				//
			}
			if (vitalsItemJson == null) {
				html.p("item not found.");
			} else {
				html.pre(vitalsItemJson.toString(WriterConfig.PRETTY_PRINT));
			}
		}
	}

	private class ChannelHttpGetVitalsDefs extends ChannelHeaderFooterHtml {
		public ChannelHttpGetVitalsDefs() {
			super(VitalsChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			html.ul();
			for (String vitalsId : getVitalsIds()) {
				html.li(html.A("/fold/vitals/defs/" + vitalsId, vitalsId));
			}
			html.ul(false);
		}
	}

	private class ChannelHttpGetVitalsDef extends ChannelHeaderFooterHtml {
		private String _vitalsDefSegment;

		public ChannelHttpGetVitalsDef(String vitalsDefSegment) {
			super(VitalsChannel.this);
			_vitalsDefSegment = vitalsDefSegment;
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			String vitalsId = _vitalsDefSegment;
			html.h(3, "vitals def " + vitalsId);

			IVitals vitals = getVitals(vitalsId);
			if (vitals == null) {
				html.p("No definition found.");
				return;
			}

			html.table();
			html.tr(true, "id", "datatype", "units", "name", "description");
			for (Vital vital : vitals.getVitals()) {
				html.tr(vital.Id, vital.Datatype, vital.Units, vital.Name,
						vital.Description);
			}
			html.table(false);
		}
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
