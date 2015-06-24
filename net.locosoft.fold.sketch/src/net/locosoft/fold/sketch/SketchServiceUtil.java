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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class SketchServiceUtil {

	public static ISketchService getSketchService() {
		Bundle bundle = FrameworkUtil.getBundle(SketchServiceUtil.class);
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceReference<ISketchService> serviceReference = bundleContext
				.getServiceReference(ISketchService.class);
		ISketchService sketchService = bundleContext
				.getService(serviceReference);
		return sketchService;
	}

}
