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

import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;

import org.eclipse.core.runtime.IConfigurationElement;

public class Vital {

	public final String Id;
	public final String Datatype;
	public final String Units;
	public final String Name;
	public final String Description;
	public final boolean Internal;

	public boolean isNumeric() {
		switch (Datatype) {
		case "int":
			return true;
		case "long":
			return true;
		case "float":
			return true;
		case "double":
			return true;
		default:
			return false;
		}
	}

	//
	//

	Vital(String id, String datatype, String units, String name,
			String description) {
		Id = id;
		Datatype = datatype;
		Units = units;
		Name = name;
		Description = description;
		Internal = true;
	}

	Vital(String id, String datatype, String units, String name,
			String description, boolean internal) {
		Id = id;
		Datatype = datatype;
		Units = units;
		Name = name;
		Description = description;
		Internal = internal;
	}

	Vital(IConfigurationElement vitalElement) {
		Id = vitalElement.getAttribute("id");
		Datatype = vitalElement.getAttribute("datatype");
		Units = vitalElement.getAttribute("units");
		Name = vitalElement.getAttribute("name");
		Description = vitalElement.getAttribute("description");
		Internal = false;
	}

	//

	void clear() {
		_intValue = 0;
		_longValue = 0;
		_floatValue = 0;
		_doubleValue = 0;
		_booleanValue = false;
		_stringValue = null;
	}

	//

	void addTo(MultiPropertyAccessNode sketch) {
		switch (Datatype) {
		case "int":
			sketch.addProperty(Id, getIntValue());
			break;
		case "long":
			sketch.addProperty(Id, getLongValue());
			break;
		case "float":
			sketch.addProperty(Id, getFloatValue());
			break;
		case "double":
			sketch.addProperty(Id, getDoubleValue());
			break;
		case "boolean":
			sketch.addProperty(Id, getBooleanValue());
			break;
		case "string":
			sketch.addProperty(Id, getStringValue());
			break;
		}
	}

	//

	private int _intValue;

	void setValue(int value) {
		checkDatatype("int");
		_intValue = value;
	}

	int getIntValue() {
		return _intValue;
	}

	//

	private long _longValue;

	void setValue(long value) {
		checkDatatype("long");
		_longValue = value;
	}

	long getLongValue() {
		return _longValue;
	}

	//

	private float _floatValue;

	void setValue(float value) {
		checkDatatype("float");
		_floatValue = value;
	}

	float getFloatValue() {
		return _floatValue;
	}

	//

	private double _doubleValue;

	void setValue(double value) {
		checkDatatype("double");
		_doubleValue = value;
	}

	double getDoubleValue() {
		return _doubleValue;
	}

	//

	private boolean _booleanValue;

	void setValue(boolean value) {
		checkDatatype("boolean");
		_booleanValue = value;
	}

	boolean getBooleanValue() {
		return _booleanValue;
	}

	//

	private String _stringValue;

	void setValue(String value) {
		checkDatatype("string");
		_stringValue = value;
	}

	String getStringValue() {
		return _stringValue;
	}

	//

	private boolean checkDatatype(String opDatatype) {
		boolean datatypeMatch = opDatatype.equals(Datatype);
		if (!datatypeMatch) {
			System.out.println("Vital: " + Id + ", datatype mismatch! "
					+ opDatatype + "/" + Datatype);
		}
		return datatypeMatch;
	}
}
