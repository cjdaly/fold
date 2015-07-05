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

import org.eclipse.core.runtime.IConfigurationElement;

public class Vital {

	public final String Id;
	public final String Datatype;
	public final String Units;
	public final String Name;
	public final String Description;

	public Vital(IConfigurationElement vitalElement) {
		Id = vitalElement.getAttribute("id");
		Datatype = vitalElement.getAttribute("datatype");
		Units = vitalElement.getAttribute("units");
		Name = vitalElement.getAttribute("name");
		Description = vitalElement.getAttribute("description");
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

	private int _intValue;

	public void setValue(int value) {
		_intValue = value;
	}

	public int getIntValue() {
		return _intValue;
	}

	//

	private long _longValue;

	public void setValue(long value) {
		_longValue = value;
	}

	public long getLongValue() {
		return _longValue;
	}

	//

	private float _floatValue;

	public void setValue(float value) {
		_floatValue = value;
	}

	public float getFloatValue() {
		return _floatValue;
	}

	//

	private double _doubleValue;

	public void setValue(double value) {
		_doubleValue = value;
	}

	public double getDoubleValue() {
		return _doubleValue;
	}

	//

	private boolean _booleanValue;

	public void setValue(boolean value) {
		_booleanValue = value;
	}

	public boolean getBooleanValue() {
		return _booleanValue;
	}

	//

	private String _stringValue;

	public void setValue(String value) {
		_stringValue = value;
	}

	public String getStringValue() {
		return _stringValue;
	}
}
