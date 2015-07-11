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

import net.locosoft.fold.channel.vitals.IVitals;
import net.locosoft.fold.sketch.pad.html.ChannelItemHtml;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;
import net.locosoft.fold.util.MarkdownComposer;

import com.eclipsesource.json.JsonObject;

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
			_itemOrdinal = _nodeSketch.getOrdinal();
		} else {
			_itemOrdinal = itemOrdinal;
		}
		_itemJson = _nodeSketch.getOrdinalNode(_itemOrdinal);
	}

	public void writeBlock(PrintWriter writer, int sizeHint) throws IOException {
		if (isSizeEmpty(sizeHint))
			return;

		MarkdownComposer md = new MarkdownComposer();
		if (_itemJson == null) {
			md.line("_(no data)_", true);
		} else {
			String vitalsId = _itemJson.getString(IVitals.NODE_PROPERTY_ID,
					null);
			long vitalsTime = _itemJson.getLong(
					IVitals.NODE_PROPERTY_CHECK_TIME, 0);
			Date date = new Date(vitalsTime);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			String vitalsTimeText = dateFormat.format(date);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(vitalsTime);

			md.line("### Vitals " + _itemOrdinal + " at " + vitalsTimeText);

			IVitals vitals = _vitalsChannel.getVitals(vitalsId);
			if (vitals != null) {
				md.table();
				for (Vital vital : vitals.getAllVitals()) {
					if (!vital.Internal) {
						md.tr(vital.Id, _itemJson.get(vital.Id).toString(),
								vital.Units);
					}
				}
				md.table(false);
			}
		}
		writer.append(md.getHtml());
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
