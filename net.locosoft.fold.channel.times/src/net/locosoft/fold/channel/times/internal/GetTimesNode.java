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

import java.util.Calendar;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.sketch.AbstractNodeSketch;

import org.eclipse.core.runtime.Path;

import com.eclipsesource.json.WriterConfig;

public class GetTimesNode extends AbstractNodeSketch {

	public GetTimesNode(long nodeId) {
		_nodeId = nodeId;
	}

	private static final String CYPHER_CREATE_MINUTE = "MATCH epochNode" //
			+ " WHERE id(epochNode)={epochNodeId}" //
			+ " CREATE UNIQUE (epochNode)-[:fold_Hierarchy]->"
			+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
			+ "(monthNode { fold_Hierarchy_segment:{month} })-[:fold_Hierarchy]->"
			+ "(dayNode { fold_Hierarchy_segment:{day} })-[:fold_Hierarchy]->"
			+ "(hourNode { fold_Hierarchy_segment:{hour} })-[:fold_Hierarchy]->" //
			+ "(minuteNode { fold_Hierarchy_segment:{minute} })" //
			+ " RETURN ID(minuteNode)";

	private static final String CYPHER_FIND_MINUTE = "MATCH (epochNode)-[:fold_Hierarchy]->" //
			+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
			+ "(monthNode { fold_Hierarchy_segment:{month} })-[:fold_Hierarchy]->"
			+ "(dayNode { fold_Hierarchy_segment:{day} })-[:fold_Hierarchy]->"
			+ "(hourNode { fold_Hierarchy_segment:{hour} })-[:fold_Hierarchy]->" //
			+ "(minuteNode { fold_Hierarchy_segment:{minute} })" //
			+ " WHERE id(epochNode)={epochNodeId}" //
			+ " RETURN ID(minuteNode)";

	public long getMinuteNodeId(long timeMillis, boolean createIfAbsent) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);

		INeo4jService neo4jService = getNeo4jService();
		String cypherText;
		if (createIfAbsent) {
			cypherText = CYPHER_CREATE_MINUTE;
		} else {
			cypherText = CYPHER_FIND_MINUTE;
		}
		ICypher cypher = neo4jService.constructCypher(cypherText);

		cypher.addParameter("epochNodeId", getNodeId());
		cypher.addParameter("year", String.valueOf(calendar.get(Calendar.YEAR)));
		cypher.addParameter("month",
				String.valueOf(calendar.get(Calendar.MONTH) + 1)); // Jan=1
		cypher.addParameter("day",
				String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		cypher.addParameter("hour",
				String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
		cypher.addParameter("minute",
				String.valueOf(calendar.get(Calendar.MINUTE)));

		if (createIfAbsent) {
			return retryCreateMinuteCypher(neo4jService, cypher, 8);
		} else {
			neo4jService.invokeCypher(cypher);
			if (cypher.getResultDataRowCount() != 1)
				return -1;
			else
				return cypher.getResultDataRow(0).asLong();
		}
	}

	private long retryCreateMinuteCypher(INeo4jService neo4jService,
			ICypher cypher, int retryCount) {
		for (int i = 0; i < retryCount; i++) {
			neo4jService.invokeCypher(cypher, false);
			if (cypher.getErrorCount() == 0) {
				if (i > 2) {
					System.out.println("retryCreateMinuteCypher: " + i);
					System.out.println(cypher.getErrors().toString(
							WriterConfig.PRETTY_PRINT));
				}
				return cypher.getResultDataRow(0).asLong();
			}
			try {
				Thread.sleep(100 * i);
			} catch (InterruptedException ex) {
				//
			}
		}
		System.out.println("retryCreateMinuteCypher: " + retryCount);
		System.out.println(cypher.getErrors().toString(
				WriterConfig.PRETTY_PRINT));
		return -1;
	}

	private static final String CYPHER_FIND_YEAR = "MATCH (epochNode)-[:fold_Hierarchy]->" //
			+ "(yearNode { fold_Hierarchy_segment:{year} })" //
			+ " WHERE id(epochNode)={epochNodeId}" //
			+ " RETURN ID(yearNode)";

	private static final String CYPHER_FIND_MONTH = "MATCH (epochNode)-[:fold_Hierarchy]->" //
			+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
			+ "(monthNode { fold_Hierarchy_segment:{month} })"
			+ " WHERE id(epochNode)={epochNodeId}" //
			+ " RETURN ID(monthNode)";

	private static final String CYPHER_FIND_DAY = "MATCH (epochNode)-[:fold_Hierarchy]->" //
			+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
			+ "(monthNode { fold_Hierarchy_segment:{month} })-[:fold_Hierarchy]->"
			+ "(dayNode { fold_Hierarchy_segment:{day} })"
			+ " WHERE id(epochNode)={epochNodeId}" //
			+ " RETURN ID(dayNode)";

	private static final String CYPHER_FIND_HOUR = "MATCH (epochNode)-[:fold_Hierarchy]->" //
			+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
			+ "(monthNode { fold_Hierarchy_segment:{month} })-[:fold_Hierarchy]->"
			+ "(dayNode { fold_Hierarchy_segment:{day} })-[:fold_Hierarchy]->"
			+ "(hourNode { fold_Hierarchy_segment:{hour} })" //
			+ " WHERE id(epochNode)={epochNodeId}" //
			+ " RETURN ID(hourNode)";

	public long getTimeNodeId(Path path) {
		INeo4jService neo4jService = getNeo4jService();

		ICypher cypher = null;
		switch (path.segmentCount()) {
		case 2: // epoch
			return getNodeId();
		case 3: // year
			cypher = neo4jService.constructCypher(CYPHER_FIND_YEAR);
			cypher.addParameter("epochNodeId", getNodeId());
			cypher.addParameter("year", path.segment(2));
			break;
		case 4: // month
			cypher = neo4jService.constructCypher(CYPHER_FIND_MONTH);
			cypher.addParameter("epochNodeId", getNodeId());
			cypher.addParameter("year", path.segment(2));
			cypher.addParameter("month", path.segment(3));
			break;
		case 5: // day
			cypher = neo4jService.constructCypher(CYPHER_FIND_DAY);
			cypher.addParameter("epochNodeId", getNodeId());
			cypher.addParameter("year", path.segment(2));
			cypher.addParameter("month", path.segment(3));
			cypher.addParameter("day", path.segment(4));
			break;
		case 6: // hour
			cypher = neo4jService.constructCypher(CYPHER_FIND_HOUR);
			cypher.addParameter("epochNodeId", getNodeId());
			cypher.addParameter("year", path.segment(2));
			cypher.addParameter("month", path.segment(3));
			cypher.addParameter("day", path.segment(4));
			cypher.addParameter("hour", path.segment(5));
			break;
		case 7: // minute
			cypher = neo4jService.constructCypher(CYPHER_FIND_MINUTE);
			cypher.addParameter("epochNodeId", getNodeId());
			cypher.addParameter("year", path.segment(2));
			cypher.addParameter("month", path.segment(3));
			cypher.addParameter("day", path.segment(4));
			cypher.addParameter("hour", path.segment(5));
			cypher.addParameter("minute", path.segment(6));
			break;
		default:
			return -1;
		}

		neo4jService.invokeCypher(cypher);
		if (cypher.getResultDataRowCount() != 1)
			return -1;
		else
			return cypher.getResultDataRow(0).asLong();
	}
}
