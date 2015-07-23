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

package net.locosoft.fold.neo4j.internal;

import net.locosoft.fold.util.FoldUtil;
import net.locosoft.fold.util.MonitorThread;

public class Neo4jController {

	public String getNeo4jHomeDir() {
		try {
			String foldHomeDir = FoldUtil.getFoldHomeDir();
			String neo4jVersion = System
					.getProperty("net.locosoft.fold.neo4j.version");
			String neo4jHomeDir = foldHomeDir + "/neo4j/neo4j-community-"
					+ neo4jVersion;
			return neo4jHomeDir;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public int getNeo4jPID() {
		StringBuilder neo4jStatus = new StringBuilder();
		execNeo4jCommand("status", neo4jStatus);

		String runCheck = "is running at pid";
		int index = neo4jStatus.indexOf(runCheck);
		if (index == -1)
			return -1;

		String pidText = neo4jStatus.toString()
				.substring(index + runCheck.length()).trim();
		int pid = -1;
		try {
			pid = Integer.parseInt(pidText);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return pid;
	}

	public boolean isNeo4jReady() {
		return _neo4jMonitor.isRunning();
	}

	public void start() {
		_neo4jMonitor.start();
	}

	public void stop() {
		_neo4jMonitor.stop();
	}

	private MonitorThread _neo4jMonitor = new MonitorThread() {
		public boolean cycle() {

			int pid = getNeo4jPID();
			if (pid == -1) {
				System.out.println("starting Neo4j...");
				StringBuilder processOut = new StringBuilder();
				execNeo4jCommand("start", processOut);
				System.out.println(processOut);
			}
			return true;
		}
	};

	private int execNeo4jCommand(String command, StringBuilder processOut) {
		String fullCommand = getNeo4jHomeDir() + "/bin/neo4j " + command;
		return FoldUtil.execCommand(fullCommand, processOut);
	}
}
