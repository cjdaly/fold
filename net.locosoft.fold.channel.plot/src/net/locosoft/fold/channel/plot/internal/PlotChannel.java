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

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.AbstractChannel;
import net.locosoft.fold.channel.IChannel;
import net.locosoft.fold.channel.plot.IPlotChannel;

public class PlotChannel extends AbstractChannel implements IPlotChannel {

	public Class<? extends IChannel> getChannelInterface() {
		return IPlotChannel.class;
	}

	public void channelHttp(HttpServletRequest request,
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
}
