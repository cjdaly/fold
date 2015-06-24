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

package net.locosoft.fold.sketch.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SketchActivator implements BundleActivator {

	private SketchService _sketchService;

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("starting SketchActivator");
		_sketchService = new SketchService(bundleContext);
		_sketchService.start();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("stopping SketchActivator");
		_sketchService.stop();
	}
}
