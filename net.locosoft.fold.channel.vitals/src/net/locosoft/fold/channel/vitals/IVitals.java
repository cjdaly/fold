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

	// predefined vital attributes
	String ATTR_ID = "id";
	String ATTR_VALUE = "value";
	String ATTR_DATATYPE = "datatype";
	String ATTR_UNITS = "units";
	String ATTR_NAME = "name";
	String ATTR_DESCRIPTION = "description";

	// attribute groups
	String[] ATTRS_ALL = new String[0];
	String[] ATTRS_DEFAULT = new String[] { ATTR_ID, ATTR_VALUE };

	String readVitalsAsJson(String[] attributes);

}
