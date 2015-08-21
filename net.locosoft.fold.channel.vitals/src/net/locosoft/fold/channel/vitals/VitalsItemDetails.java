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

import com.eclipsesource.json.JsonValue;

import net.locosoft.fold.channel.vitals.internal.VitalsChannel;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemDetails;

public class VitalsItemDetails extends ChannelItemDetails {

	private VitalsChannel _channel;

	public VitalsItemDetails(VitalsChannel channel) {
		super(channel, "Vitals");
		_channel = channel;
	}

	public Vital[] getVitals() {
		IVitals vitals = getIVitals();
		if (vitals == null)
			return new Vital[0];
		else
			return vitals.getVitals();
	}

	public JsonValue getValue(String vitalId) {
		return getJson().get(vitalId);
	}

	public String getUrlPath() {
		return "/fold/vitals/" + getOrdinal();
	}

	public long getTime() {
		return getJson().getLong(IVitals.NODE_PROPERTY_CHECK_TIME,
				Long.MIN_VALUE);
	}

	private IVitals getIVitals() {
		String vitalsId = getJson().getString(IVitals.NODE_PROPERTY_ID, null);
		if (vitalsId == null)
			return null;
		else
			return _channel.getVitals(vitalsId);
	}
}
