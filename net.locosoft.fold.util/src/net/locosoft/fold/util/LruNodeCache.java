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

package net.locosoft.fold.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
// thanks to http://stackoverflow.com/a/1953516
public class LruNodeCache<T> extends LinkedHashMap<T, Long> {

	public static <U> Map<U, Long> createLruNodeCache(int maxEntries) {
		return Collections.synchronizedMap(new LruNodeCache<U>(maxEntries));
		// return new LruNodeCache<U>(maxEntries);
	}

	private int _maxEntries;

	private LruNodeCache(int maxEntries) {
		super(maxEntries + 1, 1.0f, true);
		_maxEntries = maxEntries;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry<T, Long> eldest) {
		return size() > _maxEntries;
	}
}
