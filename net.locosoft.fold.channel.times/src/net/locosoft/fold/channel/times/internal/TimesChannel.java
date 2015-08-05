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
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.pad.html.ChannelHeaderFooterHtml;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;
import net.locosoft.fold.sketch.pad.neo4j.OrdinalNode;
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

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Path path = new Path(request.getPathInfo());
		if (path.segmentCount() == 1) {
			new ChannelHttpGetTimes().composeHtmlResponse(request, response);
		} else if ((path.segmentCount() > 1) && (path.segmentCount() < 8)) {
			new ChannelHttpGetTimesSegment(path).composeHtmlResponse(request,
					response);
		} else {
			super.channelHttpGet(request, response);
		}
	}

	private class ChannelHttpGetTimes extends ChannelHeaderFooterHtml {
		public ChannelHttpGetTimes() {
			super(TimesChannel.this);
		}

		protected void composeHtmlResponseBody(HttpServletRequest request,
				HttpServletResponse response, HtmlComposer html)
				throws ServletException, IOException {
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
				}

				HierarchyNode hierNode = new HierarchyNode(timeNodeId);
				String[] segments = hierNode.getSubSegments();
				html.ul();
				for (String segment : segments) {
					html.li(html.A("/fold"
							+ _path.removeTrailingSeparator().toString() + "/"
							+ segment, segment));
				}
				html.ul(false);

				TreeMap<String, TreeMap<Long, String>> refNodeMap = new TreeMap<String, TreeMap<Long, String>>();

				RefTimesNode refNodes = new RefTimesNode(timeNodeId);
				JsonObject[] jsonNodes = refNodes.getTimesRefNodes();
				populateRefNodeMap(refNodeMap, jsonNodes);

				for (Entry<String, TreeMap<Long, String>> channelEntry : refNodeMap
						.entrySet()) {
					html.h(4, channelEntry.getKey());
					html.ul();
					for (Entry<Long, String> channelItemEntry : channelEntry
							.getValue().entrySet()) {
						html.li(html.A(channelItemEntry.getValue(),
								channelItemEntry.getKey().toString()));
					}
					html.ul(false);
				}
			}
		}

		private void populateRefNodeMap(
				TreeMap<String, TreeMap<Long, String>> refNodeMap,
				JsonObject[] jsonNodes) {
			for (JsonObject jsonNode : jsonNodes) {
				for (String name : jsonNode.names()) {
					if (name.startsWith(OrdinalNode.PREFIX_ORDINAL_INDEX)) {
						addRefNodeMapEntry(refNodeMap, jsonNode, name);
					}
				}
			}
		}

		private Pattern _channelIdPattern = Pattern
				.compile(OrdinalNode.PREFIX_ORDINAL_INDEX + "([^_]+)_");

		private void addRefNodeMapEntry(
				TreeMap<String, TreeMap<Long, String>> refNodeMap,
				JsonObject jsonNode, String ordinalIndexKeyName) {
			Matcher matcher = _channelIdPattern.matcher(ordinalIndexKeyName);
			if (matcher.find()) {
				String channelId = matcher.group(1);
				IChannel channel = getChannelService().getChannel(channelId);
				if (channel != null) {
					long channelItemOrdinalIndex = jsonNode.getLong(
							ordinalIndexKeyName, -1);
					if (channelItemOrdinalIndex != -1) {
						String channelItemUrlPath = channel.getChannelData(
								"channelItem.urlPath", jsonNode.toString());
						if (channelItemUrlPath != null) {
							TreeMap<Long, String> channelMap = refNodeMap
									.get(channelId);
							if (channelMap == null) {
								channelMap = new TreeMap<Long, String>();
								refNodeMap.put(channelId, channelMap);
							}
							channelMap.put(channelItemOrdinalIndex,
									channelItemUrlPath);
						}
					}
				}
			}
		}
	}
}
