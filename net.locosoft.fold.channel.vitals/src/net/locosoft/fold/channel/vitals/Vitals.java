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

package net.locosoft.fold.channel.vitals;

public abstract class Vitals {

	public abstract void readVitals();

	public int getIntVital(String id) {
		return 0;
	}

	public long getLongVital(String id) {
		return 0;
	}

	public float getFloatVital(String id) {
		return 0;
	}

	public double getDoubleVital(String id) {
		return 0;
	}

	public boolean getBooleanVital(String id) {
		return false;
	}

	public String getStringVital(String id) {
		return "";
	}

}
