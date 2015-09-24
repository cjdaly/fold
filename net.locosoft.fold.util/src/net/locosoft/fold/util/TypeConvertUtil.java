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

public class TypeConvertUtil {

	public static Integer toInteger(Object value) {
		if (value instanceof Integer)
			return (Integer) value;
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException ex) {
				//
			}
		}
		return null;
	}

	public static Long toLong(Object value) {
		if (value instanceof Long)
			return (Long) value;
		if (value instanceof Integer) {
			Integer v = (Integer) value;
			return v.longValue();
		}
		if (value instanceof String) {
			try {
				return Long.parseLong((String) value);
			} catch (NumberFormatException ex) {
				//
			}
		}
		return null;
	}

	public static Float toFloat(Object value) {
		if (value instanceof Float)
			return (Float) value;
		if (value instanceof String) {
			try {
				return Float.parseFloat((String) value);
			} catch (NumberFormatException ex) {
				//
			}
		}
		return null;
	}

	public static Double toDouble(Object value) {
		if (value instanceof Double)
			return (Double) value;
		if (value instanceof Float) {
			Float v = (Float) value;
			return v.doubleValue();
		}
		if (value instanceof String) {
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException ex) {
				//
			}
		}
		return null;
	}

	public static Boolean toBoolean(Object value) {
		if (value instanceof Boolean)
			return (Boolean) value;
		if (value instanceof String) {
			try {
				return Boolean.parseBoolean((String) value);
			} catch (NumberFormatException ex) {
				//
			}
		}
		return null;
	}

	public static String toString(Object value) {
		if (value == null)
			return null;
		else
			return value.toString();
	}

}
