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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eclipsesource.json.JsonValue;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.plot.IPlotChannel;

public class PlotChannel extends AbstractChannel implements IPlotChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IPlotChannel.class;
	}

	public void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/png");

		Process process = Runtime.getRuntime().exec("/usr/bin/gnuplot");

		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				new BufferedOutputStream(process.getOutputStream()));
		outputStreamWriter
				.write("set terminal png size 800,600 enhanced font \"Helvetica,20\"\n");
		outputStreamWriter.write("plot sin(x)\n");
		outputStreamWriter.flush();
		outputStreamWriter.close();

		InputStream inputStream = process.getInputStream();
		ServletOutputStream outputStream = response.getOutputStream();

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
	}

	public void plotPngImage(String title, String xLabel, String yLabel,
			String keyLabel, TreeMap<Long, JsonValue> timeSeriesDataMap,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");

		Process process = Runtime.getRuntime().exec("/usr/bin/gnuplot");

		OutputStreamWriter writer = new OutputStreamWriter(
				new BufferedOutputStream(process.getOutputStream()));
		writer.write("set terminal png size 800,400\n");
		writer.write("set title '" + title + "'\n");
		writer.write("set xlabel '" + xLabel + "'\n");
		writer.write("set ylabel '" + yLabel + "'\n");
		writer.write("set key left top\n");
		writer.write("set grid\n");
		writer.write("set xdata time\n");
		writer.write("set timefmt '%s'\n");
		writer.write("set format x '%m/%d'\n");
		writer.write("plot '-' using 1:2 with lines title '" + keyLabel + "'\n");
		writer.write("\n");

		Set<Entry<Long, JsonValue>> entrySet = timeSeriesDataMap.entrySet();
		for (Entry<Long, JsonValue> entry : entrySet) {
			writer.write("" + entry.getKey() + " " + entry.getValue() + "\n");
		}
		writer.write("\n");
		writer.flush();
		writer.close();

		InputStream inputStream = process.getInputStream();
		ServletOutputStream outputStream = response.getOutputStream();

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

	}
}
