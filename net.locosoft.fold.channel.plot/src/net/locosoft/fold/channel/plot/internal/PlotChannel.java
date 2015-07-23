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

package net.locosoft.fold.channel.plot.internal;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.plot.IPlotChannel;
import net.locosoft.fold.util.FoldUtil;

import com.eclipsesource.json.JsonValue;

public class PlotChannel extends AbstractChannel implements IPlotChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IPlotChannel.class;
	}

	public void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/png");

		StringBuilder plotInput = new StringBuilder();
		plotInput.append("set terminal png size 800,600\n");
		plotInput.append("plot sin(x)\n");

		FoldUtil.execCommand("/usr/bin/gnuplot", plotInput.toString(),
				response.getOutputStream());
	}

	public void plotPngImage(String title, String xLabel, String yLabel,
			String keyLabel, TreeMap<Long, JsonValue> timeSeriesDataMap,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");

		StringBuilder plotInput = new StringBuilder();
		plotInput.append("set terminal png size 800,400\n");
		plotInput.append("set title '" + title + "'\n");
		plotInput.append("set xlabel '" + xLabel + "'\n");
		plotInput.append("set ylabel '" + yLabel + "'\n");
		plotInput.append("set key left top\n");
		plotInput.append("set grid\n");
		plotInput.append("set xdata time\n");
		plotInput.append("set timefmt '%s'\n");
		plotInput.append("set format x '%m/%d'\n");
		plotInput.append("plot '-' using 1:2 with lines title '" + keyLabel
				+ "'\n");
		plotInput.append("\n");

		Set<Entry<Long, JsonValue>> entrySet = timeSeriesDataMap.entrySet();
		for (Entry<Long, JsonValue> entry : entrySet) {
			plotInput.append("" + entry.getKey() + " " + entry.getValue()
					+ "\n");
		}
		plotInput.append("\n");

		FoldUtil.execCommand("/usr/bin/gnuplot", plotInput.toString(),
				response.getOutputStream());

	}
}
