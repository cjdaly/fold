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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Path;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.times.INeo4jTimes;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.ISketchService;
import net.locosoft.fold.sketch.SketchUtil;
import net.locosoft.fold.sketch.pad.neo4j.INeo4jHierarchyNode;
import net.locosoft.fold.util.MarkdownComposer;

public class TimesChannel extends AbstractChannel implements ITimesChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return ITimesChannel.class;
	}

	private long _epochNodeId = -1;

	public long getEpochNodeId() {
		if (_epochNodeId == -1) {
			ISketchService sketchService = SketchUtil.getSketchService();
			INeo4jHierarchyNode neo4jHierarchyNode = sketchService
					.constructSketch(INeo4jHierarchyNode.class);
			neo4jHierarchyNode.init(getChannelNodeId());
			_epochNodeId = neo4jHierarchyNode.getSubId("ce", true);
		}
		return _epochNodeId;
	}

	public long getMinuteNodeId(long timeMillis, boolean createIfAbsent) {
		ISketchService sketchService = SketchUtil.getSketchService();
		INeo4jTimes neo4jTimes = sketchService
				.constructSketch(INeo4jTimes.class);
		neo4jTimes.init(getEpochNodeId());
		return neo4jTimes.getMinuteNodeId(timeMillis, createIfAbsent);
	}

	public void init() {
		long currentTimeMillis = System.currentTimeMillis();
		System.out.println("times channel starting at: " + currentTimeMillis);

		long minuteNodeId = getMinuteNodeId(currentTimeMillis, true);
		System.out.println("minute node ID: " + minuteNodeId);
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");

		MarkdownComposer md = new MarkdownComposer();
		md.line("# times", true);

		Path path = new Path(request.getPathInfo());
		long currNodeId = getChannelNodeId();

		ISketchService sketchService = SketchUtil.getSketchService();
		INeo4jHierarchyNode neo4jHierarchyNode = sketchService
				.constructSketch(INeo4jHierarchyNode.class);
		neo4jHierarchyNode.init(currNodeId);

		for (int index = 1; index < path.segmentCount(); index++) {
			String segment = path.segment(index);
			md.line("    segment(" + index + ")= " + segment);

			currNodeId = neo4jHierarchyNode.getSubId(segment, false);
			if (currNodeId != -1) {
				neo4jHierarchyNode.init(currNodeId);
				long[] subIds = neo4jHierarchyNode.getSubIds();
				String subIdList = "";
				for (long subId : subIds) {
					subIdList += subId + ",";
				}
				md.line("      subIds: " + subIdList);

				String[] subSegments = neo4jHierarchyNode.getSubSegments();
				String subSegmentList = "";
				for (String subSegment : subSegments) {
					subSegmentList += subSegment + ",";
				}
				md.line("      subSegments: " + subSegmentList);
			} else {
				break;
			}
		}

		response.getWriter().println(md.getHtml());
	}

}
