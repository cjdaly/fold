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

package net.locosoft.fold.channel.times.internal;

import java.util.Calendar;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class TimesUtil {

	public static String getUrlPath(long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		IPath urlPath = new Path("/fold/times/ce");
		urlPath = urlPath.append(String.valueOf(calendar.get(Calendar.YEAR)));
		urlPath = urlPath
				.append(String.valueOf(calendar.get(Calendar.MONTH) + 1)); // Jan=1
		urlPath = urlPath.append(String.valueOf(calendar
				.get(Calendar.DAY_OF_MONTH)));
		urlPath = urlPath.append(String.valueOf(calendar
				.get(Calendar.HOUR_OF_DAY)));
		urlPath = urlPath.append(String.valueOf(calendar.get(Calendar.MINUTE)));
		return urlPath.toString();
	}

	public static long getTimeMillis(IPath urlPath) {
		// TODO!
		return Long.MIN_VALUE;
	}

}
