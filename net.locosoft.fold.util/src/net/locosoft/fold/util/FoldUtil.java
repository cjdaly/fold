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

package net.locosoft.fold.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class FoldUtil {

	public static String getFoldHomeDir() {
		try {
			String eclipseLocation = System
					.getProperty("eclipse.home.location");
			IPath eclipsePath = new Path(new URI(eclipseLocation).getPath());
			IPath foldHomePath = eclipsePath.removeLastSegments(1)
					.removeTrailingSeparator();

			return foldHomePath.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getFoldDataDir() {
		String foldHomeDir = getFoldHomeDir();
		if (foldHomeDir != null)
			return foldHomeDir + "/data";
		else
			return null;
	}

	public static <T> T getService(Class<T> serviceType) {
		Bundle bundle = FrameworkUtil.getBundle(serviceType);
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<T> serviceReference = bundleContext
				.getServiceReference(serviceType);
		T service = bundleContext.getService(serviceReference);
		return service;
	}

	public static String readFileToString(String path) {
		StringBuilder outputText = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			int readRaw = reader.read();
			while (readRaw != -1) {
				char c = (char) readRaw;
				outputText.append(c);
				readRaw = reader.read();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return outputText.toString();
	}

	public static int execCommand(String command, StringBuilder processOut) {
		int status = -1;
		if (processOut == null)
			processOut = new StringBuilder();
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
		return status;
	}

	public static class ProcessStreamReader extends Thread {
		private InputStream _inputStream;
		private StringBuilder _outputBuffer;

		ProcessStreamReader(InputStream inputStream, StringBuilder outputBuffer) {
			_inputStream = inputStream;
			_outputBuffer = outputBuffer;
		}

		public void run() {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(_inputStream))) {
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
