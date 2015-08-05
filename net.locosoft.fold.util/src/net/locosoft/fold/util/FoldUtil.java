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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class FoldUtil {

	public static String getFoldInternalVersion() {
		return System.getProperty("net.locosoft.fold.internalVersion");
	}

	private static final Pattern _ipPattern = Pattern
			.compile("\\d+:\\s+((eth|wlan)\\d+)\\s+inet\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)/");

	public static String[] getFoldUrls() {
		String foldPort = System.getProperty("org.osgi.service.http.port");
		String[] foldIpAddrs = getFoldIpAddrs();
		String[] foldUrls = new String[foldIpAddrs.length];
		for (int i = 0; i < foldUrls.length; i++) {
			foldUrls[i] = "http://" + foldIpAddrs[i] + ":" + foldPort + "/fold";
		}
		return foldUrls;
	}

	public static String[] getFoldIpAddrs() {
		ArrayList<String> foldIpAddrs = new ArrayList<String>();

		StringBuilder processOut = new StringBuilder();
		String ipCommand = "ip -o -4 addr";
		FoldUtil.execCommand(ipCommand, processOut);

		Matcher matcher = _ipPattern.matcher(processOut);
		while (matcher.find()) {
			String ipAddr = matcher.group(3);
			foldIpAddrs.add(ipAddr);
		}

		return foldIpAddrs.toArray(new String[0]);
	}

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

	public static String getFoldConfigDir() {
		String foldHomeDir = getFoldHomeDir();
		if (foldHomeDir != null)
			return foldHomeDir + "/config";
		else
			return null;
	}

	public static int getFoldPid() {
		String foldHomeDir = getFoldHomeDir();
		String foldPIDFile = readFileToString(foldHomeDir + "/fold.PID", false);
		try {
			return Integer.parseInt(foldPIDFile.trim());
		} catch (NumberFormatException ex) {
			return -1;
		}
	}

	public static <T> T getService(Class<T> serviceType) {
		Bundle bundle = FrameworkUtil.getBundle(serviceType);
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<T> serviceReference = bundleContext
				.getServiceReference(serviceType);
		T service = bundleContext.getService(serviceReference);
		return service;
	}

	public static Properties loadPropertiesFile(String path) {
		Properties properties = new Properties();

		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			properties.load(reader);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return properties;
	}

	public static String readFileToString(String path) {
		return readFileToString(path, true);
	}

	public static String readFileToString(String path, boolean logErrors) {
		StringBuilder outputText = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			int readRaw = reader.read();
			while (readRaw != -1) {
				char c = (char) readRaw;
				outputText.append(c);
				readRaw = reader.read();
			}
		} catch (FileNotFoundException ex) {
			if (logErrors)
				ex.printStackTrace();
		} catch (IOException ex) {
			if (logErrors)
				ex.printStackTrace();
		}
		return outputText.toString();
	}

	public static int execCommand(String command, String processIn,
			OutputStream processOut) throws IOException {
		int status = -1;

		Process process = Runtime.getRuntime().exec(command);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				new BufferedOutputStream(process.getOutputStream()));
		outputStreamWriter.write(processIn.toString());
		outputStreamWriter.flush();
		outputStreamWriter.close();

		InputStream inputStream = process.getInputStream();

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			processOut.write(buffer, 0, bytesRead);
		}
		try {
			status = process.waitFor();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		return status;
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

				char[] buffer = new char[1024];

				int bytesRead;
				while ((bytesRead = reader.read(buffer)) != -1) {
					_outputBuffer.append(buffer, 0, bytesRead);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
