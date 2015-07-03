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

public class Neo4jController implements Runnable {

	private Thread _thread;
	private boolean _stopping = false;
	private boolean _stopped = true;

	public Neo4jController() {
		_thread = new Thread(this);
	}

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

	public boolean isNeo4jReady() {
		return !(_stopped || _stopping);
	}

	public void start() {
		_thread.start();
	}

	public void stop() {
		_stopping = true;
	}

	public void run() {
		while (!_stopping) {
			try {
				int pid = getNeo4jPID();
				if (pid == -1) {
					execNeo4jCommand("start");
					Thread.sleep(10 * 1000);
				} else {
					_stopped = false;
					Thread.sleep(60 * 1000);
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		_stopped = true;
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

	private int execNeo4jCommand(String command) {
		return execNeo4jCommand(command, null);
	}

	private int execNeo4jCommand(String command, StringBuilder processOut) {
		String fullCommand = getNeo4jHomeDir() + "/bin/neo4j " + command;
		return FoldUtil.execCommand(fullCommand, processOut);
	}
}
