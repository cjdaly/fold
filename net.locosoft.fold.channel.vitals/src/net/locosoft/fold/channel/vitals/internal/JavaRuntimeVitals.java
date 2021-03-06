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

import net.locosoft.fold.channel.vitals.StaticVitals;

public class JavaRuntimeVitals extends StaticVitals {

	public void readVitals() {
		Runtime runtime = Runtime.getRuntime();
		recordVital("freeMemory", runtime.freeMemory());
		recordVital("maxMemory", runtime.maxMemory());
		recordVital("totalMemory", runtime.totalMemory());
	}

}
