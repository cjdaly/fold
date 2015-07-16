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

import net.locosoft.fold.channel.ChannelUtil;
import net.locosoft.fold.channel.IChannelService;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.channel.vitals.internal.Vital;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;

import org.eclipse.core.runtime.IConfigurationElement;

public abstract class StaticVitals extends AbstractVitals {

	public abstract void readVitals();

	private String _id;
	private int _checkInterval;
	private long _lastCheckTime;

	public void init(IConfigurationElement configurationElement) {
		_id = configurationElement.getAttribute("id");
		String checkIntervalText = configurationElement
				.getAttribute("checkInterval");
		_checkInterval = Integer.parseInt(checkIntervalText);

		IConfigurationElement[] vitalElements = configurationElement
				.getChildren("vital");
		for (IConfigurationElement vitalElement : vitalElements) {
			Vital vital = new Vital(vitalElement);
			addVital(vital);
		}

		addVital(createCheckTimeVital());
		addVital(createIdVital());
	}

	public String getId() {
		return _id;
	}

	public boolean isCheckTime(long currentTimeMillis) {
		long nextCheckTime = _lastCheckTime + (_checkInterval * 1000);
		return (currentTimeMillis >= nextCheckTime);
	}

	public void checkVitals(long vitalsItemNodeId) {
		for (Vital vital : getVitals()) {
			vital.clear();
		}
		readVitals();

		_lastCheckTime = System.currentTimeMillis();
		recordVital(IVitals.NODE_PROPERTY_CHECK_TIME, _lastCheckTime);
		recordVital(IVitals.NODE_PROPERTY_ID, getId());

		MultiPropertyAccessNode sketch = new MultiPropertyAccessNode(
				vitalsItemNodeId);
		for (Vital vital : getVitals()) {
			vital.addTo(sketch);
		}
		sketch.setProperties();

		IChannelService channelService = ChannelUtil.getChannelService();
		ITimesChannel timesChannel = channelService
				.getChannel(ITimesChannel.class);
		timesChannel.createTimesRef(_lastCheckTime, vitalsItemNodeId);
	}

}
