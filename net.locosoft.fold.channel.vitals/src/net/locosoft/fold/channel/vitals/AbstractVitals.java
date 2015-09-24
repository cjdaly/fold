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

import net.locosoft.fold.util.TypeConvertUtil;

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

	protected void recordVital(String id, Integer value) {
		if (value == null)
			return;
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, Long value) {
		if (value == null)
			return;
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, Float value) {
		if (value == null)
			return;
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, Double value) {
		if (value == null)
			return;
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, Boolean value) {
		if (value == null)
			return;
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVital(String id, String value) {
		if (value == null)
			return;
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			vital.setValue(value);
		}
	}

	protected void recordVitalConvertType(String id, Object value) {
		Vital vital = _idToVital.get(id);
		if (vital != null) {
			switch (vital.Datatype) {
			case "int":
				recordVital(id, TypeConvertUtil.toInteger(value));
				break;
			case "long":
				recordVital(id, TypeConvertUtil.toLong(value));
				break;
			case "float":
				recordVital(id, TypeConvertUtil.toFloat(value));
				break;
			case "double":
				recordVital(id, TypeConvertUtil.toDouble(value));
				break;
			case "boolean":
				recordVital(id, TypeConvertUtil.toBoolean(value));
				break;
			case "string":
				recordVital(id, TypeConvertUtil.toString(value));
				break;
			}
		}
	}

}
