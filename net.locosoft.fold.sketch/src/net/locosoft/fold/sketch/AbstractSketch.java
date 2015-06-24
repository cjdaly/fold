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

package net.locosoft.fold.sketch;

public abstract class AbstractSketch implements ISketch, ISketchInternal {
	private String _id;

	public String getSketchId() {
		return _id;
	}

	//
	// ISketchInternal
	//

	public final void setSketchId(String id) {
		_id = id;
	}
}
