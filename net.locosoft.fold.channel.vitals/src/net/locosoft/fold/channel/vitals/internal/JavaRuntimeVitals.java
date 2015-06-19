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

import net.locosoft.fold.channel.vitals.Vitals;

public class JavaRuntimeVitals extends Vitals {

	private long _freeMemory;
	private long _maxMemory;
	private long _totalMemory;

	public void readVitals() {
		Runtime runtime = Runtime.getRuntime();
		_freeMemory = runtime.freeMemory();
		_maxMemory = runtime.maxMemory();
		_totalMemory = runtime.totalMemory();
	}

	public long getLongVital(String id) {
		switch (id) {
		case "freeMemory":
			return _freeMemory;
		case "maxMemory":
			return _maxMemory;
		case "totalMemory":
			return _totalMemory;
		default:
			return 0;
		}
	}

}
