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

import java.util.TreeMap;

import net.locosoft.fold.channel.vitals.internal.Vital;

import org.eclipse.core.runtime.IConfigurationElement;

public abstract class AbstractVitals implements IVitals {

	public abstract void readVitals();

	private String _id;
	private int _checkInterval;
	private long _lastCheckTime;

	private TreeMap<String, Vital> _idToVital = new TreeMap<String, Vital>();

	public void init(IConfigurationElement configurationElement) {
		_id = configurationElement.getAttribute("id");
		String checkIntervalText = configurationElement
				.getAttribute("checkInterval");
		_checkInterval = Integer.parseInt(checkIntervalText);

		IConfigurationElement[] vitalElements = configurationElement
				.getChildren("vital");
		for (IConfigurationElement vitalElement : vitalElements) {
			Vital vital = new Vital(vitalElement);
			_idToVital.put(vital.Id, vital);
		}

	}

	public String getId() {
		return _id;
	}

	public boolean isCheckTime(long currentTimeMillis) {
		long nextCheckTime = _lastCheckTime + (_checkInterval * 1000);
		return (currentTimeMillis >= nextCheckTime);
	}

	public void checkVitals() {
		for (Vital vital : _idToVital.values()) {
			vital.clear();
		}
		readVitals();
		_lastCheckTime = System.currentTimeMillis();
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
