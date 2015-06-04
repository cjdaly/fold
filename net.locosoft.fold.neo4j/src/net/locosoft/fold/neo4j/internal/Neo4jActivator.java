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

import net.locosoft.fold.neo4j.INeo4jService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Neo4jActivator implements BundleActivator {

	private ServiceRegistration<INeo4jService> _serviceRegistration;
	private Neo4jMonitor _neo4jMonitor;

	public void start(BundleContext bundleContext) throws Exception {

		Neo4jService neo4jService = new Neo4jService();
		_serviceRegistration = bundleContext.registerService(
				INeo4jService.class, neo4jService, null);

		_neo4jMonitor = new Neo4jMonitor();
		_neo4jMonitor.start();

		System.out.println("starting Neo4jActivator");
	}

	public void stop(BundleContext bundleContext) throws Exception {

		_neo4jMonitor._stopping = true;
		// TODO: wait for stop?

		_serviceRegistration.unregister();

		System.out.println("stopping Neo4jActivator");
	}

	private class Neo4jMonitor extends Thread {
		boolean _stopping = false;

		public void run() {
			try {
				while (!_stopping) {
					execNeo4jCommand("/bin/ls");

					Thread.sleep(10 * 1000);

					execNeo4jCommand("/bin/pwd");

					Thread.sleep(10 * 1000);
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		int execNeo4jCommand(String command) {
			System.out.println("COMMAND: " + command);

			int status = -1;
			StringBuilder processOut = new StringBuilder();
			try {
				Process process = Runtime.getRuntime().exec(command);
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

			System.out.println("OUTPUT:");
			System.out.println(processOut.toString());
			return status;
		}
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
