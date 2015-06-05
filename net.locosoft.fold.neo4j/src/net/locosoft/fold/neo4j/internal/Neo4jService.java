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

import net.locosoft.fold.neo4j.INeo4jService;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Neo4jService implements INeo4jService, Runnable {

	private BundleContext _bundleContext;
	private String _neo4jHomeDir;

	public Neo4jService(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_neo4jHomeDir = getNeo4jHomeDir();
		System.out.println("neoHome: " + _neo4jHomeDir);
	}

	private String getNeo4jHomeDir() {
		try {
			String eclipseLocation = System
					.getProperty("eclipse.home.location");
			IPath eclipsePath = new Path(new URI(eclipseLocation).getPath());
			IPath foldHomePath = eclipsePath.removeLastSegments(1)
					.removeTrailingSeparator();

			String neo4jVersion = System
					.getProperty("net.locosoft.fold.neo4j.version");
			IPath neo4jHomePath = foldHomePath.append("/neo4j/neo4j-community-"
					+ neo4jVersion);
			return neo4jHomePath.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private ServiceRegistration<INeo4jService> _serviceRegistration;
	private Thread _thread;

	public void start() {
		_serviceRegistration = _bundleContext.registerService(
				INeo4jService.class, this, null);
		_thread = new Thread(this);
		_thread.start();
	}

	public void stop() {
		_serviceRegistration.unregister();
		_stopping = true;
		// TODO: wait for _stopped?
	}

	boolean _stopping = false;
	boolean _stopped = false;

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
		_stopped = true;
	}

	private int execNeo4jCommand(String command) {
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
