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

package net.locosoft.fold.channel.times;

import java.util.Calendar;

import com.eclipsesource.json.JsonValue;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.sketch.ISketch;
import net.locosoft.fold.sketch.pad.neo4j.INeo4jNodeSketch;

public interface INeo4jTimes extends INeo4jNodeSketch {

	long getMinuteNodeId(long time, boolean createIfAbsent);

	class Impl extends INeo4jNodeSketch.Impl implements INeo4jTimes {

		public Class<? extends ISketch> getSketchInterface() {
			return INeo4jTimes.class;
		}

		public long getMinuteNodeId(long timeMillis, boolean createIfAbsent) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeMillis);

			INeo4jService neo4jService = getNeo4jService();
			String cypherText;
			if (createIfAbsent) {
				cypherText = "MATCH epochNode" //
						+ " WHERE id(epochNode)={epochNodeId}" //
						+ " CREATE UNIQUE (epochNode)-[:fold_Hierarchy]->"
						+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
						+ "(monthNode { fold_Hierarchy_segment:{month} })-[:fold_Hierarchy]->"
						+ "(dayNode { fold_Hierarchy_segment:{day} })-[:fold_Hierarchy]->"
						+ "(hourNode { fold_Hierarchy_segment:{hour} })-[:fold_Hierarchy]->" //
						+ "(minuteNode { fold_Hierarchy_segment:{minute} })" //
						+ " RETURN ID(minuteNode)";
			} else {
				cypherText = "MATCH (epochNode)-[:fold_Hierarchy]->" //
						+ "(yearNode { fold_Hierarchy_segment:{year} })-[:fold_Hierarchy]->"
						+ "(monthNode { fold_Hierarchy_segment:{month} })-[:fold_Hierarchy]->"
						+ "(dayNode { fold_Hierarchy_segment:{day} })-[:fold_Hierarchy]->"
						+ "(hourNode { fold_Hierarchy_segment:{hour} })-[:fold_Hierarchy]->" //
						+ "(minuteNode { fold_Hierarchy_segment:{minute} })" //
						+ " WHERE id(epochNode)={epochNodeId}" //
						+ " RETURN ID(minuteNode)";
			}
			ICypher cypher = neo4jService.constructCypher(cypherText);

			cypher.addParameter("epochNodeId", getNodeId());
			cypher.addParameter("year",
					String.valueOf(calendar.get(Calendar.YEAR)));
			cypher.addParameter("month",
					String.valueOf(calendar.get(Calendar.MONTH) + 1)); // Jan=1
			cypher.addParameter("day",
					String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
			cypher.addParameter("hour",
					String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
			cypher.addParameter("minute",
					String.valueOf(calendar.get(Calendar.MINUTE)));

			neo4jService.invokeCypher(cypher);
			JsonValue jsonValue = cypher.getResultDataRow(0);
			return jsonValue.asLong();
		}
	}
}
