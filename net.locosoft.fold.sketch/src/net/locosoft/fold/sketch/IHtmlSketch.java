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

public interface IHtmlSketch extends ISketch {

	public static final int SIZE_EMPTY = 0;

	public static final int SIZE_BLOCK = 1;
	public static final int SIZE_BLOCK_EMPTY = 2;
	public static final int SIZE_BLOCK_TINY = 3;
	public static final int SIZE_BLOCK_SMALL = 4;
	public static final int SIZE_BLOCK_MEDIUM = 5;
	public static final int SIZE_BLOCK_LARGE = 6;
	public static final int SIZE_BLOCK_FULL = 7;

	public static final int SIZE_INLINE_SIGN = -1;
	//
	public static final int SIZE_INLINE = 1 * SIZE_INLINE_SIGN;
	public static final int SIZE_INLINE_EMPTY = 2 * SIZE_INLINE_SIGN;
	public static final int SIZE_INLINE_TINY = 3 * SIZE_INLINE_SIGN;
	public static final int SIZE_INLINE_SMALL = 4 * SIZE_INLINE_SIGN;
	public static final int SIZE_INLINE_MEDIUM = 5 * SIZE_INLINE_SIGN;
	public static final int SIZE_INLINE_LARGE = 6 * SIZE_INLINE_SIGN;
	public static final int SIZE_INLINE_FULL = 7 * SIZE_INLINE_SIGN;

}
