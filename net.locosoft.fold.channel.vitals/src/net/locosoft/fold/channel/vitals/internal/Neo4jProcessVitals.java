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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.locosoft.fold.channel.vitals.StaticVitals;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jUtil;
import net.locosoft.fold.util.FoldUtil;

public class Neo4jProcessVitals extends StaticVitals {

	private INeo4jService _neo4jService;

	public Neo4jProcessVitals() {
		_neo4jService = Neo4jUtil.getNeo4jService();
	}

	private Pattern _vmPeakPattern = Pattern.compile("VmPeak:\\s+(\\d+)\\s+kB");

	public void readVitals() {
		int neo4jPID = _neo4jService.getNeo4jPID();
		if (neo4jPID == -1) {
			return; // TODO: ???
		}

		String procStatus = FoldUtil.readFileToString("/proc/" + neo4jPID
				+ "/status");

		Matcher matcher = _vmPeakPattern.matcher(procStatus);
		if (matcher.find()) {
			String vmPeakText = matcher.group(1);
			long vmPeak = Long.parseLong(vmPeakText);
			recordVital("vmPeak", vmPeak);
		}
	}

}
