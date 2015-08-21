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

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import com.eclipsesource.json.JsonValue;

public abstract class AbstractVitals implements IVitals {

	protected Vital createCheckTimeVital() {
		return new Vital(IVitals.NODE_PROPERTY_CHECK_TIME, "long", "millis",
				"check time", "check time in milliseconds");
	}

	protected Vital createIdVital() {
		return new Vital(IVitals.NODE_PROPERTY_ID, "string", "id", "Id",
				"Vitals Id");
	}

	protected void addVital(Vital vital) {
		_idToVital.put(vital.Id, vital);
	}

	protected void addAndRecordVital(String id, String datatype,
			JsonValue jsonValue) {
		Vital vital = new Vital(id, datatype, "?", id, "?", false);
		addVital(vital);
		switch (datatype) {
		case "int":
			recordVital(id, jsonValue.asInt());
			break;
		case "long":
			recordVital(id, jsonValue.asLong());
			break;
		case "float":
			recordVital(id, jsonValue.asFloat());
			break;
		case "double":
			recordVital(id, jsonValue.asDouble());
			break;
		case "boolean":
			recordVital(id, jsonValue.asBoolean());
			break;
		case "string":
			recordVital(id, jsonValue.asString());
			break;
		}

	}

	protected Collection<Vital> getVitalsCollection() {
		return _idToVital.values();
	}

	private TreeMap<String, Vital> _idToVital = new TreeMap<String, Vital>();

	public Vital getVital(String id) {
		return _idToVital.get(id);
	}

	public Vital[] getVitals() {
		ArrayList<Vital> vitals = new ArrayList<Vital>();
		for (Vital vital : _idToVital.values()) {
			if (!vital.Internal) {
				vitals.add(vital);
			}
		}
		return vitals.toArray(new Vital[0]);
	}

	public Vital[] getVitals(boolean includeInternal) {
		if (includeInternal) {
			return _idToVital.values().toArray(new Vital[0]);
		} else {
			return getVitals();
		}
	}

	protected void recordVital(String id, int value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, long value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, float value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, double value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, boolean value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, String value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

}
