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

package net.locosoft.fold.channel.vitals.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.locosoft.fold.channel.vitals.AbstractVitals;
import net.locosoft.fold.util.FoldUtil;

public class SystemMemoryVitals extends AbstractVitals {

	private Pattern _memFreePattern = Pattern
			.compile("MemFree:\\s+(\\d+)\\s+kB");

	public void readVitals() {
		String procMemInfo = FoldUtil.readFileToString("/proc/meminfo");
		Matcher matcher = _memFreePattern.matcher(procMemInfo);
		if (matcher.find()) {
			String memFreeText = matcher.group(1);
			long memFree = Long.parseLong(memFreeText);
			recordVital("memFree", memFree);
		}
	}
}
