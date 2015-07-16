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

import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;

import org.eclipse.core.runtime.IConfigurationElement;

public class Vital {

	public final String Id;
	public final String Datatype;
	public final String Units;
	public final String Name;
	public final String Description;
	public final boolean Internal;

	public Vital(String id, String datatype, String units, String name,
			String description) {
		Id = id;
		Datatype = datatype;
		Units = units;
		Name = name;
		Description = description;
		Internal = true;
	}

	public Vital(String id, String datatype, String units, String name,
			String description, boolean internal) {
		Id = id;
		Datatype = datatype;
		Units = units;
		Name = name;
		Description = description;
		Internal = internal;
	}

	public Vital(IConfigurationElement vitalElement) {
		Id = vitalElement.getAttribute("id");
		Datatype = vitalElement.getAttribute("datatype");
		Units = vitalElement.getAttribute("units");
		Name = vitalElement.getAttribute("name");
		Description = vitalElement.getAttribute("description");
		Internal = false;
	}

	//

	public void clear() {
		_intValue = 0;
		_longValue = 0;
		_floatValue = 0;
		_doubleValue = 0;
		_booleanValue = false;
		_stringValue = null;
	}

	//

	public void addTo(MultiPropertyAccessNode sketch) {
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

	public void setValue(int value) {
		checkDatatype("int");
		_intValue = value;
	}

	public int getIntValue() {
		return _intValue;
	}

	//

	private long _longValue;

	public void setValue(long value) {
		checkDatatype("long");
		_longValue = value;
	}

	public long getLongValue() {
		return _longValue;
	}

	//

	private float _floatValue;

	public void setValue(float value) {
		checkDatatype("float");
		_floatValue = value;
	}

	public float getFloatValue() {
		return _floatValue;
	}

	//

	private double _doubleValue;

	public void setValue(double value) {
		checkDatatype("double");
		_doubleValue = value;
	}

	public double getDoubleValue() {
		return _doubleValue;
	}

	//

	private boolean _booleanValue;

	public void setValue(boolean value) {
		checkDatatype("boolean");
		_booleanValue = value;
	}

	public boolean getBooleanValue() {
		return _booleanValue;
	}

	//

	private String _stringValue;

	public void setValue(String value) {
		checkDatatype("string");
		_stringValue = value;
	}

	public String getStringValue() {
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
