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
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.locosoft.fold.channel.vitals.DynamicVitals;
import net.locosoft.fold.channel.vitals.IVitals;
import net.locosoft.fold.channel.vitals.Vital;
import net.locosoft.fold.sketch.pad.html.ChannelItemHtml;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.util.HtmlComposer;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

public class VitalsHtml extends ChannelItemHtml {

	private VitalsChannel _vitalsChannel;
	private ChannelItemNode _nodeSketch;
	private long _itemOrdinal;
	private JsonObject _itemJson;

	public VitalsHtml(VitalsChannel channel, String itemLabel, long itemOrdinal) {
		super(channel, "Vitals");
		_vitalsChannel = channel;
		_nodeSketch = new ChannelItemNode(channel, itemLabel);

		if (itemOrdinal == -1) {
			_itemOrdinal = _nodeSketch.getLatestOrdinal();
		} else {
			_itemOrdinal = itemOrdinal;
		}
		_itemJson = _nodeSketch.getOrdinalNode(_itemOrdinal);
	}

	public void writeBlock(PrintWriter writer, int sizeHint) throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		HtmlComposer html = new HtmlComposer(writer);

		if (_itemJson == null) {
			html.p("_(no data)_");
		} else {
			String vitalsId = _itemJson.getString(IVitals.NODE_PROPERTY_ID,
					null);
			if (vitalsId == null) {
				System.out.println("Error: null vitalsId in VitalsHtml!");
				System.out.println(_itemJson
						.toString(WriterConfig.PRETTY_PRINT));
				// NPE in 3 2 1 ...
			}

			long vitalsTime = _itemJson.getLong(
					IVitals.NODE_PROPERTY_CHECK_TIME, 0);
			Date date = new Date(vitalsTime);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			String vitalsTimeText = dateFormat.format(date);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(vitalsTime);

			html.p("Vitals " + _itemOrdinal + " at " + vitalsTimeText);

			IVitals vitals = _vitalsChannel.getVitals(vitalsId);
			if (vitals == null) {
				vitals = getDynamicVitals(vitalsId);
			}
			if (vitals != null) {
				html.table();
				for (Vital vital : vitals.getVitals()) {
					JsonValue jsonValue = _itemJson.get(vital.Id);
					String value = jsonValue == null ? "?" : jsonValue
							.toString();
					html.tr(vital.Id, value, vital.Units);
				}
				html.table(false);
			}
		}
	}

	private IVitals getDynamicVitals(String vitalsId) {
		DynamicVitals vitals = new DynamicVitals(vitalsId);

		HierarchyNode channelNode = new HierarchyNode(
				_vitalsChannel.getChannelNodeId());
		long defsNodeId = channelNode.getSubId("defs", true);
		HierarchyNode defsNode = new HierarchyNode(defsNodeId);
		long vitalsNodeId = defsNode.getSubId(vitalsId, true);

		vitals.loadVitals(vitalsId, vitalsNodeId);

		return vitals;
	}

	public void writeInline(PrintWriter writer, int sizeHint)
			throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		if (_itemJson == null) {
			writer.append("<em>(no data)</em>");
		} else {
			writer.append(_itemJson.toString());
		}
	}

}
