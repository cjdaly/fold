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

import com.eclipsesource.json.JsonObject;

public abstract class AbstractVitals implements IVitals {

	public abstract void readVitals();

	private JsonObject _vitals;

	public String readVitalsAsJson(String[] attributes) {
		_vitals = new JsonObject();
		readVitals();
		return _vitals.toString();
	}

	protected void recordVital(String id, int value) {
		_vitals.add(id, value);
	}

	protected void recordVital(String id, long value) {
		_vitals.add(id, value);
	}

	protected void recordVital(String id, float value) {
		_vitals.add(id, value);
	}

	protected void recordVital(String id, double value) {
		_vitals.add(id, value);
	}

	protected void recordVital(String id, boolean value) {
		_vitals.add(id, value);
	}

	protected void recordVital(String id, String value) {
		_vitals.add(id, value);
	}

}
