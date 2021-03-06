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

public interface IVitals {

	public static final String NODE_PROPERTY_ID = "Vitals_id";
	public static final String NODE_PROPERTY_CHECK_TIME = "Vitals_checkTime";

	String getId();

	Vital getVital(String id);

	Vital[] getVitals();

	Vital[] getVitals(boolean includeInternal);

}
