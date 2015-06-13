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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import com.eclipsesource.json.JsonObject;

public class Neo4jController implements Runnable {

	private Neo4jService _neo4jService;
	private Thread _thread;

	public Neo4jController(Neo4jService neo4jService) {
		_neo4jService = neo4jService;
		_thread = new Thread(this);
	}

	boolean _stopping = false;
	boolean _stopped = false;

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
					System.out.println("Neo PID: " + pid);

					Neo4jBridge neo4jBridge = new Neo4jBridge();
					URI uri = new URI("http://localhost:7474/db/data");
					JsonObject jsonObject = neo4jBridge.doGetJson(uri);

					System.out.println("JSON: " + jsonObject);

					Thread.sleep(60 * 1000);
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (URISyntaxException ex) {
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
		String fullCommand = _neo4jService.getNeo4jHomeDir() + "/bin/neo4j "
				+ command;

		int status = -1;
		if (processOut == null)
			processOut = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec(fullCommand);
			ProcessStreamReader reader = new ProcessStreamReader(
					process.getInputStream(), processOut);
			reader.start();
			status = process.waitFor();
			reader.join();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		System.out.println(command + "\n -> " + processOut.toString());
		return status;
	}

	private class ProcessStreamReader extends Thread {
		private InputStream _inputStream;
		private StringBuilder _outputBuffer;

		ProcessStreamReader(InputStream inputStream, StringBuilder outputBuffer) {
			_inputStream = inputStream;
			_outputBuffer = outputBuffer;
		}

		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(_inputStream));

				int readRaw = reader.read();
				while (readRaw != -1) {
					char c = (char) readRaw;
					_outputBuffer.append(c);
					readRaw = reader.read();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
