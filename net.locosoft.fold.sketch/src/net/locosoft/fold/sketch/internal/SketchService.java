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

import java.util.HashMap;

import net.locosoft.fold.sketch.ISketch;
import net.locosoft.fold.sketch.ISketchService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class SketchService implements ISketchService {

	private BundleContext _bundleContext;
	private ServiceRegistration<ISketchService> _serviceRegistration;

	private HashMap<String, ISketch> _idToSketch;

	public SketchService(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_idToSketch = new HashMap<String, ISketch>();
	}

	private void initSketchMaps() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.fold.sketch");
		for (IConfigurationElement configurationElement : configurationElements) {
			try {
				String sketchId = configurationElement.getAttribute("id");
				Object extension = configurationElement
						.createExecutableExtension("implementation");
				ISketch sketch = (ISketch) extension;

				// TODO: check for duplicate id/iface use
				_idToSketch.put(sketchId, sketch);
			} catch (ClassCastException ex) {
				ex.printStackTrace();
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void start() {
		initSketchMaps();

		_serviceRegistration = _bundleContext.registerService(
				ISketchService.class, this, null);
	}

	public void stop() {
		_serviceRegistration.unregister();
	}

	//
	// ISketchService
	//

	public ISketch getSketch(String sketchId) {
		return _idToSketch.get(sketchId);
	}

}
