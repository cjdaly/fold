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

package net.locosoft.fold.channel.plot;

import java.io.IOException;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.IChannel;

import com.eclipsesource.json.JsonValue;

public interface IPlotChannel extends IChannel {

	public void plotPngImage(String title, String xLabel, String yLabel,
			String keyLabel, TreeMap<Long, JsonValue> timeSeriesDataMap,
			HttpServletResponse response) throws ServletException, IOException;

}
