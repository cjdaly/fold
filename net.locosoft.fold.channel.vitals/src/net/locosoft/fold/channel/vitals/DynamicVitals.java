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

import java.util.List;

import net.locosoft.fold.channel.ChannelUtil;
import net.locosoft.fold.channel.IChannelService;
import net.locosoft.fold.channel.times.ITimesChannel;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;
import net.locosoft.fold.sketch.pad.neo4j.PropertyAccessNode;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DynamicVitals extends AbstractVitals {

	private String _id;

	public DynamicVitals(String id) {
		_id = id;
		addVital(createCheckTimeVital());
		addVital(createIdVital());
	}

	public String getId() {
		return _id;
	}

	private boolean isJsonNumberFloaty(String jsonNumber) {
		return jsonNumber.contains(".") || jsonNumber.contains("e")
				|| jsonNumber.contains("E");
	}

	public void saveVitals(long vitalsItemNodeId, JsonObject vitalsJson) {
		long checkTime = vitalsJson.getLong(IVitals.NODE_PROPERTY_CHECK_TIME,
				System.currentTimeMillis());
		recordVital(IVitals.NODE_PROPERTY_CHECK_TIME, checkTime);
		recordVital(IVitals.NODE_PROPERTY_ID, getId());

		List<String> names = vitalsJson.names();
		for (String name : names) {
			if (IVitals.NODE_PROPERTY_CHECK_TIME.equals(name))
				continue;
			if (IVitals.NODE_PROPERTY_ID.equals(name))
				continue;

			JsonValue jsonValue = vitalsJson.get(name);
			if (jsonValue.isNumber()) {
				String value = jsonValue.toString();
				if (isJsonNumberFloaty(value)) {
					addAndRecordVital(name, "double", jsonValue);
				} else {
					addAndRecordVital(name, "long", jsonValue);
				}
			} else if (jsonValue.isBoolean()) {
				addAndRecordVital(name, "boolean", jsonValue);
			} else if (jsonValue.isString()) {
				addAndRecordVital(name, "string", jsonValue);
			}
		}

		MultiPropertyAccessNode sketch = new MultiPropertyAccessNode(
				vitalsItemNodeId);
		for (Vital vital : getVitalsCollection()) {
			vital.addTo(sketch);
		}
		sketch.setProperties();

		IChannelService channelService = ChannelUtil.getChannelService();
		ITimesChannel timesChannel = channelService
				.getChannel(ITimesChannel.class);
		timesChannel.createTimesRef(checkTime, vitalsItemNodeId);
	}

	public void loadVitals(String vitalsId, long vitalsNodeId) {
		HierarchyNode vitalsNode = new HierarchyNode(vitalsNodeId);

		long[] subIds = vitalsNode.getSubIds();
		for (long subId : subIds) {
			PropertyAccessNode props = new PropertyAccessNode(subId);
			String id = props.getStringValue("fold_Hierarchy_segment");
			String datatype = props.getStringValue("datatype");
			String units = props.getStringValue("units");
			String name = props.getStringValue("name");
			String description = props.getStringValue("description");
			Vital vital = new Vital(id, datatype, units, name, description,
					false);
			addVital(vital);
		}
	}

}
