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

public class FoldProcessVitals extends AbstractVitals {

	private int _foldPID = -1;

	private int getFoldPid() {
		if (_foldPID != -1)
			return _foldPID;
		String foldHomeDir = FoldUtil.getFoldHomeDir();
		String foldPID = FoldUtil.readFileToString(foldHomeDir + "/fold.PID");
		_foldPID = Integer.parseInt(foldPID.trim());
		return _foldPID;
	}

	public void readVitals() {
		String procStatus = FoldUtil.readFileToString("/proc/" + getFoldPid()
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
