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
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class FoldUtil {

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

	public static <T> T getService(Class<T> serviceType) {
		Bundle bundle = FrameworkUtil.getBundle(serviceType);
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<T> serviceReference = bundleContext
				.getServiceReference(serviceType);
		T service = bundleContext.getService(serviceReference);
		return service;
	}

}
