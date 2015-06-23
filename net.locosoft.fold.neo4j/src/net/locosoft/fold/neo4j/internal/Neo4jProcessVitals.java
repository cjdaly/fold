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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.locosoft.fold.channel.vitals.AbstractVitals;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.util.FoldUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

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

		String procStatus = FoldUtil.readFileToString("/proc/" + neo4jPID
				+ "/status");
		Pattern pattern = Pattern.compile("VmPeak:\\s+(\\d+)\\s+kB");
		Matcher matcher = pattern.matcher(procStatus);
		if (matcher.find()) {
			String vmPeakText = matcher.group(1);
			long vmPeak = Integer.parseInt(vmPeakText);
			recordVital("vmPeak", vmPeak);
		}
	}

}
