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

import net.locosoft.fold.channel.vitals.AbstractVitals;
import net.locosoft.fold.util.FoldUtil;

public class FoldStorageVitals extends AbstractVitals {

	private Pattern _eclipsePattern = //
	Pattern.compile("(\\d+)\\s+/.*/data/eclipse");

	private Pattern _neo4jPattern = //
	Pattern.compile("(\\d+)\\s+/.*/data/neo4j");

	public void readVitals() {
		StringBuilder processOut = new StringBuilder();
		String duCommand = "/usr/bin/du -sk " //
				+ FoldUtil.getFoldDataDir() + "/eclipse " //
				+ FoldUtil.getFoldDataDir() + "/neo4j";
		FoldUtil.execCommand(duCommand, processOut);

		Matcher matcher;
		matcher = _eclipsePattern.matcher(processOut);
		if (matcher.find()) {
			String duEclipseText = matcher.group(1);
			long duEclipseKb = Long.parseLong(duEclipseText);
			recordVital("dataEclipse", duEclipseKb);
		}

		matcher = _neo4jPattern.matcher(processOut);
		if (matcher.find()) {
			String duNeo4jText = matcher.group(1);
			long duNeo4jKb = Long.parseLong(duNeo4jText);
			recordVital("dataNeo4j", duNeo4jKb);
		}
	}

}
