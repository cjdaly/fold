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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import net.locosoft.fold.channel.vitals.AbstractVitals;
import net.locosoft.fold.neo4j.INeo4jService;

public class Neo4jProcessVitals extends AbstractVitals {

	private INeo4jService _neo4jService;

	public Neo4jProcessVitals() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		_neo4jService = bundleContext.getService(bundleContext
				.getServiceReference(INeo4jService.class));
	}

	public void readVitals() {
		int neo4jPID = _neo4jService.getNeo4jPID();
		if (neo4jPID == -1) {
			return; // TODO: ???
		}

		StringBuilder outputText = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader("/proc/"
				+ neo4jPID + "/status"))) {
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

		Pattern pattern = Pattern.compile("VmPeak:\\s+(\\d+)\\s+kB");
		Matcher matcher = pattern.matcher(outputText.toString());
		if (matcher.find()) {
			String vmPeakText = matcher.group(1);
			long vmPeak = Integer.parseInt(vmPeakText);
			recordVital("vmPeak", vmPeak);
		}
	}

}
