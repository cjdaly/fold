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

package net.locosoft.fold.packaging;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class FoldActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		FoldActivator.context = bundleContext;
		System.out.println("starting FoldActivator");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		FoldActivator.context = null;
		System.out.println("stopping FoldActivator");
	}

}
