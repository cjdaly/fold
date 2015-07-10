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

import net.locosoft.fold.channel.ChannelUtil;
import net.locosoft.fold.channel.IChannelService;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.channel.vitals.internal.Vital;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;

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

		Vital checkTime = new Vital("Vitals_checkTime", "long", "millis",
				"check time", "check time in milliseconds");
		_idToVital.put(checkTime.Id, checkTime);

		Vital vitalsId = new Vital("Vitals_id", "string", "id", "Id",
				"Vitals Id");
		_idToVital.put(vitalsId.Id, vitalsId);
	}

	public String getId() {
		return _id;
	}

	public boolean isCheckTime(long currentTimeMillis) {
		long nextCheckTime = _lastCheckTime + (_checkInterval * 1000);
		return (currentTimeMillis >= nextCheckTime);
	}

	public void checkVitals(long vitalsItemNodeId) {
		for (Vital vital : _idToVital.values()) {
			vital.clear();
		}
		readVitals();
		_lastCheckTime = System.currentTimeMillis();
		recordVital("Vitals_checkTime", _lastCheckTime);
		recordVital("Vitals_id", getId());

		if (vitalsItemNodeId != -1) {
			MultiPropertyAccessNode sketch = new MultiPropertyAccessNode(
					vitalsItemNodeId);
			for (Vital vital : _idToVital.values()) {
				vital.addTo(sketch);
			}
			sketch.setProperties();

			IChannelService channelService = ChannelUtil.getChannelService();
			ITimesChannel timesChannel = channelService
					.getChannel(ITimesChannel.class);
			timesChannel.createTimesRef(_lastCheckTime, vitalsItemNodeId);
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
