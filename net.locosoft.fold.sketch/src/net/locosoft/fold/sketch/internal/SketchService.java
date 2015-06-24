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
import net.locosoft.fold.sketch.ISketchInternal;
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
	private HashMap<Class<? extends ISketch>, ISketch> _ifaceToSketch;

	public SketchService(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_idToSketch = new HashMap<String, ISketch>();
		_ifaceToSketch = new HashMap<Class<? extends ISketch>, ISketch>();
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

				if (sketch instanceof ISketchInternal) {
					ISketchInternal sketchInternal = (ISketchInternal) sketch;
					sketchInternal.setSketchId(sketchId);
				} else {
					// TODO: complain?
				}

				// TODO: check for duplicate id/iface use
				_idToSketch.put(sketchId, sketch);
				_ifaceToSketch.put(sketch.getSketchInterface(), sketch);
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

	public ISketch[] getAllSketches() {
		return _idToSketch.values().toArray(new ISketch[0]);
	}

	public ISketch getSketch(String sketchId) {
		return _idToSketch.get(sketchId);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ISketch> T getSketch(Class<T> sketchInterface) {
		return (T) _ifaceToSketch.get(sketchInterface);
	}

}
