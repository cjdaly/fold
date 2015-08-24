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

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.IChannelInternal;
import net.locosoft.fold.channel.vitals.DynamicVitals;
import net.locosoft.fold.channel.vitals.IVitals;
import net.locosoft.fold.sketch.pad.html.AbstractChannelHtmlSketch;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.MultiPropertyAccessNode;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class VitalsPostHtml extends AbstractChannelHtmlSketch {

	public VitalsPostHtml(IChannelInternal channel) {
		super(channel);
	}

	public void postVitals(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JsonObject jsonObject = JsonObject.readFrom(request.getReader());
		String vitalsId = jsonObject.getString(IVitals.NODE_PROPERTY_ID, null);
		if (vitalsId == null) {
			response.getWriter().println(
					"POST missing " + IVitals.NODE_PROPERTY_ID);
			return;
		}

		ChannelItemNode vitalsNode = new ChannelItemNode(getChannel(), "Vitals");
		long vitalsItemNodeId = vitalsNode.nextOrdinalNodeId();

		DynamicVitals vitals = new DynamicVitals(vitalsId);
		vitals.saveVitals(vitalsItemNodeId, jsonObject);
		response.getWriter().println(
				"Posted vitals: "
						+ vitalsNode.getOrdinalIndex(vitalsItemNodeId));
	}

	public void postVitalsDef(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JsonObject jsonObject = JsonObject.readFrom(request.getReader());
		String vitalsId = jsonObject.getString(IVitals.NODE_PROPERTY_ID, null);
		if (vitalsId == null) {
			response.getWriter().println(
					"POST missing " + IVitals.NODE_PROPERTY_ID);
			return;
		}

		HierarchyNode channelNode = new HierarchyNode(getChannel()
				.getChannelNodeId());
		long defsNodeId = channelNode.getSubId("defs", true);
		HierarchyNode defsNode = new HierarchyNode(defsNodeId);
		long vitalsNodeId = defsNode.getSubId(vitalsId, true);
		HierarchyNode vitalsNode = new HierarchyNode(vitalsNodeId);

		JsonValue vitalsJsonValue = jsonObject.get("Vitals");
		if ((vitalsJsonValue == null) || !vitalsJsonValue.isObject()) {
			response.getWriter().println("POST missing Vitals defs");
			return;
		}

		JsonObject vitalsJson = vitalsJsonValue.asObject();
		List<String> names = vitalsJson.names();
		for (String name : names) {
			JsonValue jsonValue = vitalsJson.get(name);
			if (!jsonValue.isObject())
				continue;
			JsonObject vitalDef = jsonValue.asObject();

			long subId = vitalsNode.getSubId(name, true);
			MultiPropertyAccessNode props = new MultiPropertyAccessNode(subId);
			props.addProperty("id", name);
			props.addProperty("datatype",
					vitalDef.getString("datatype", "string"));
			props.addProperty("units", vitalDef.getString("units", "?"));
			props.addProperty("name", vitalDef.getString("name", name));
			props.addProperty("description",
					vitalDef.getString("description", "?"));
			props.setProperties();
		}

		response.getWriter().println("Posted vitals def: " + vitalsId);
	}
}
