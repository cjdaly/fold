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

import net.locosoft.fold.channel.vitals.StaticVitals;
import net.locosoft.fold.util.FoldUtil;

public class SystemStorageVitals extends StaticVitals {

	private Pattern _dfPattern = Pattern
			.compile("/dev/\\S+\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)%");

	public void readVitals() {
		StringBuilder processOut = new StringBuilder();
		String dfCommand = "/bin/df -k " //
				+ FoldUtil.getFoldDataDir();
		FoldUtil.execCommand(dfCommand, processOut);

		Matcher matcher = _dfPattern.matcher(processOut);
		if (matcher.find()) {
			String dfAvailableText = matcher.group(3);
			long dfAvailableKb = Long.parseLong(dfAvailableText);
			recordVital("diskAvailable", dfAvailableKb);
		}
	}

}
