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

import net.locosoft.fold.channel.vitals.AbstractVitals;
import net.locosoft.fold.util.FoldUtil;

public class SystemLoadVitals extends AbstractVitals {

	public void readVitals() {
		String procLoadAvg = FoldUtil.readFileToString("/proc/loadavg");
		String[] loadAvgs = procLoadAvg.split("\\s+");
		if ((loadAvgs != null) && (loadAvgs.length > 0)) {
			String loadAvg1MinText = loadAvgs[0];
			float loadAvg1Min = Float.parseFloat(loadAvg1MinText);
			recordVital("loadAvg1Min", loadAvg1Min);
		}
	}

}
