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

package net.locosoft.fold.channel.times.internal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.graph.IGraphChannel;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.IChannelItemDetails;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;
import net.locosoft.fold.util.HtmlComposer;
import net.locosoft.fold.util.LruNodeCache;

import org.eclipse.core.runtime.Path;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;

public class TimesChannel extends AbstractChannel implements ITimesChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return ITimesChannel.class;
	}

	private long _epochNodeId = -1;

	public long getEpochNodeId() {
		if (_epochNodeId == -1) {
			HierarchyNode sketch = new HierarchyNode(getChannelNodeId());
			_epochNodeId = sketch.getSubId("ce", true);
		}
		return _epochNodeId;
	}

	private Map<Long, Long> _minuteToNodeId = LruNodeCache
			.createLruNodeCache(2048);

	public long getMinuteNodeId(long timeMillis, boolean createIfAbsent) {
		synchronized (_minuteToNodeId) {
			long timeMinutes = timeMillis / 60000;
			Long minuteNodeIdLong = _minuteToNodeId.get(timeMinutes);
			if (minuteNodeIdLong == null) {
				GetTimesNode sketch = new GetTimesNode(getEpochNodeId());
				long minuteNodeId = sketch.getMinuteNodeId(timeMillis,
						createIfAbsent);
				if (minuteNodeId != -1) {
					_minuteToNodeId.put(timeMinutes, minuteNodeId);
				}
				return minuteNodeId;
			} else {
				return minuteNodeIdLong.longValue();
			}
		}
	}

	public void createTimesRef(long timeMillis, long refNodeId) {
		long minuteNodeId = getMinuteNodeId(timeMillis, true);
		RefTimesNode sketch = new RefTimesNode(minuteNodeId);
		sketch.createTimesRef(refNodeId);
	}

	public long[] getTimesRefNodeIds(long timeMillis, String refNodeLabel) {
		return getTimesRefNodeIds(timeMillis, refNodeLabel, null, null);
	}

	public long[] getTimesRefNodeIds(long timeMillis, String refNodeLabel,
			String refNodeKey, String refNodeValue) {
		long minuteNodeId = getMinuteNodeId(timeMillis, false);
		if (minuteNodeId == -1) {
			return new long[0];
		} else {
			RefTimesNode sketch = new RefTimesNode(minuteNodeId);
			return sketch.getTimesRefNodeIds(refNodeLabel, refNodeKey,
					refNodeValue);
		}
	}

	public JsonObject[] getTimesRefNodes(long timeMillis, String refNodeLabel,
			String refNodeKey, String refNodeValue) {
		long minuteNodeId = getMinuteNodeId(timeMillis, false);
		if (minuteNodeId == -1) {
			return new JsonObject[0];
		} else {
			RefTimesNode sketch = new RefTimesNode(minuteNodeId);
			return sketch.getTimesRefNodes(refNodeLabel, refNodeKey,
					refNodeValue);
		}
	}

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			new ChannelHttpGetTimes().composeHtmlResponse(request, response);
		} else if ((path.segmentCount() > 1) && (path.segmentCount() < 8)) {
			new ChannelHttpGetTimesSegment(path).composeHtmlResponse(request,
					response);
		} else if ((path.segmentCount() == 8)
				&& "time-tree-slice.svg".equals(path.segment(7))) {
			createGraphSvg(path, request, response);
		} else if ((path.segmentCount() == 8)
				&& "time-tree-slice.png".equals(path.segment(7))) {
			createGraphPng(path, request, response);
		} else {
			super.channelHttpGet(request, response);
		}
	}

	private String attrsHelper(String label, String value, String url) {
		return "[shape=box, style=filled, fillcolor=burlywood, label=\""
				+ label + "\\n" + value + "\", URL=\"" + url
				+ "\", target=_top]; ";
	}

	private void createGraphPng(Path path, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String dot = createGraphDot(path, request, response);
		IGraphChannel graphChannel = getChannelService().getChannel(
				IGraphChannel.class);
		graphChannel.graphPngImage(dot.toString(), request, response);
	}

	private void createGraphSvg(Path path, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String dot = createGraphDot(path, request, response);
		IGraphChannel graphChannel = getChannelService().getChannel(
				IGraphChannel.class);
		graphChannel.graphSvgImage(dot.toString(), request, response);
	}

	private String createGraphDot(Path path, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		StringBuilder dot = new StringBuilder();
		dot.append("digraph Times { rankdir=LR; subgraph clusterTimes { ");
		for (int i = 2; i < 7; i++) {
			String segment = path.segment(i);
			switch (i) {
			case 2:
				dot.append("year ");
				dot.append(attrsHelper("Year", segment,
						"/fold" + path.uptoSegment(i + 1)));
				dot.append("year -> month; ");
				break;
			case 3:
				dot.append("month ");
				dot.append(attrsHelper("Month", segment,
						"/fold" + path.uptoSegment(i + 1)));
				dot.append("month -> day; ");
				break;
			case 4:
				dot.append("day ");
				dot.append(attrsHelper("Day", segment,
						"/fold" + path.uptoSegment(i + 1)));
				dot.append("day -> hour; ");
				break;
			case 5:
				dot.append("hour ");
				dot.append(attrsHelper("Hour", segment,
						"/fold" + path.uptoSegment(i + 1)));
				dot.append("hour -> minute; ");
				break;
			case 6:
				dot.append("minute ");
				dot.append(attrsHelper("Minute", segment,
						"/fold" + path.uptoSegment(i + 1)));
				break;
			}
		}

		dot.append(" } ");

		GetTimesNode minuteNode = new GetTimesNode(getEpochNodeId());
		long timeNodeId = minuteNode.getTimeNodeId(path.removeLastSegments(1));
		if (timeNodeId != -1) {
			Calendar calendar = Calendar.getInstance();
			RefTimesNode refNodes = new RefTimesNode(timeNodeId);
			TreeMap<String, TreeMap<Long, IChannelItemDetails>> minuteMap = refNodes
					.populateMinuteMap(getChannelService());
			for (Entry<String, TreeMap<Long, IChannelItemDetails>> channelEntry : minuteMap
					.entrySet()) {
				String channelId = channelEntry.getKey();
				String prevNodeId = null;
				for (Entry<Long, IChannelItemDetails> channelItemEntry : channelEntry
						.getValue().entrySet()) {
					String nodeId = channelId + "_" + channelItemEntry.getKey();
					String nodeLabel = channelId + " <b>"
							+ channelItemEntry.getKey() + "</b>";
					IChannelItemDetails channelItemDetails = channelItemEntry
							.getValue();

					String kindParam = request.getParameter("kind");
					if ((kindParam == null)
							|| (kindParam.equals(channelItemDetails
									.getItemKind()))) {
						calendar.setTimeInMillis(channelItemDetails
								.getTimestamp());
						int second = calendar.get(Calendar.SECOND);

						if (prevNodeId != null) {
							dot.append(prevNodeId + " -> " + nodeId
									+ " [arrowhead=none]; ");
						}
						dot.append(nodeId);
						dot.append(" [shape=none, margin=0, URL=\""
								+ channelItemDetails.getUrlPath()
								+ "\", target=_top, ");
						dot.append("label=<<table border='0' cellborder='1' cellspacing='0'><tr><td bgcolor='burlywood'>");
						dot.append("Second: " + second);
						dot.append("</td></tr>");
						dot.append("<tr><td bgcolor='thistle'>");
						dot.append(nodeLabel);
						dot.append("</td></tr>");
						dot.append("<tr><td bgcolor='lightsteelblue'>");
						dot.append("kind: " + channelItemDetails.getItemKind());
						dot.append("</td></tr>");
						String[] dataLines = channelItemDetails.getDataLines();
						for (String dataLine : dataLines) {
							dot.append("<tr><td bgcolor='lightsteelblue'>");
							dot.append(dataLine);
							dot.append("</td></tr>");
						}
						dot.append("</table>>]; ");

						dot.append(nodeId + " -> minute; ");
						prevNodeId = nodeId;
					}
				}
			}
		}

		dot.append(" } ");
		return dot.toString();
	}

	private class ChannelHttpGetTimes extends ChannelHeaderFooterHtml {
		public ChannelHttpGetTimes() {
			super(TimesChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			Date now = new Date();
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
			String time = timeFormat.format(now);
			GetTimesNode sketchy = new GetTimesNode(getEpochNodeId());
			sketchy.getMinuteNodeId(now.getTime(), true);
			html.p("current minute: "
					+ html.A(TimesUtil.getUrlPath(now.getTime()), time));

			html.p("epochs:");
			HierarchyNode sketch = new HierarchyNode(getChannelNodeId());
			String[] segments = sketch.getSubSegments();
			html.ul();
			for (String segment : segments) {
				html.li(html.A("/fold/times/" + segment, segment));
			}
			html.ul(false);
		}
	}

	private class ChannelHttpGetTimesSegment extends ChannelHeaderFooterHtml {
		private Path _path;

		public ChannelHttpGetTimesSegment(Path path) {
			super(TimesChannel.this);
			_path = path;
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
			GetTimesNode sketch = new GetTimesNode(getEpochNodeId());
			long timeNodeId = sketch.getTimeNodeId(_path);
			if (timeNodeId == -1) {
				html.p("node not found.");
			} else {

				html.p("node: ");
				MultiPropertyAccessNode propsNode = new MultiPropertyAccessNode(
						timeNodeId);
				JsonObject jsonObject = propsNode.getProperties();
				html.pre(jsonObject.toString(WriterConfig.PRETTY_PRINT));

				switch (_path.segmentCount()) {
				case 2: // epoch
					html.p("years: ");
					break;
				case 3: // year
					html.p("months: ");
					break;
				case 4: // month
					html.p("days: ");
					break;
				case 5: // day
					html.p("hours: ");
					break;
				case 6: // hour
					html.p("minutes: ");
					break;
				case 7: // minute
					String imageUrl = "/fold"
							+ _path.removeTrailingSeparator().toString()
							+ "/time-tree-slice.svg";
					html.object(imageUrl, "image/svg+xml");
				}

				HierarchyNode hierNode = new HierarchyNode(timeNodeId);
				String[] segments = hierNode.getSubSegments();
				if (segments.length > 0) {
					html.ul();
					for (String segment : segments) {
						html.li(html.A("/fold"
								+ _path.removeTrailingSeparator().toString()
								+ "/" + segment, segment));
					}
					html.ul(false);
				}

				RefTimesNode refNodes = new RefTimesNode(timeNodeId);
				TreeMap<String, TreeMap<Long, IChannelItemDetails>> minuteMap = refNodes
						.populateMinuteMap(getChannelService());
				for (Entry<String, TreeMap<Long, IChannelItemDetails>> channelEntry : minuteMap
						.entrySet()) {
					html.h(4, channelEntry.getKey());
					html.ul();
					for (Entry<Long, IChannelItemDetails> channelItemEntry : channelEntry
							.getValue().entrySet()) {
						html.li(html.A(
								channelItemEntry.getValue().getUrlPath(),
								channelItemEntry.getKey().toString()));
					}
					html.ul(false);
				}
			}
		}
	}

}
