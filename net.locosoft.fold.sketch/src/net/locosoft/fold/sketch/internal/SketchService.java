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

	private HashMap<String, IConfigurationElement> _idToConfigElement;
	private HashMap<Class<? extends ISketch>, IConfigurationElement> _ifaceToConfigElement;

	public SketchService(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_idToConfigElement = new HashMap<String, IConfigurationElement>();
		_ifaceToConfigElement = new HashMap<Class<? extends ISketch>, IConfigurationElement>();
	}

	private void initSketchMaps() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = extensionRegistry
				.getConfigurationElementsFor("net.locosoft.fold.sketch");

		for (IConfigurationElement configurationElement : configurationElements) {
			String sketchId = configurationElement.getAttribute("id");
			ISketch sketch = constructSketch(configurationElement);

			// TODO: check for duplicates
			_idToConfigElement.put(sketchId, configurationElement);
			_ifaceToConfigElement.put(sketch.getSketchInterface(),
					configurationElement);
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

	public ISketch constructSketch(String sketchId) {
		IConfigurationElement configurationElement = _idToConfigElement
				.get(sketchId);
		ISketch sketch = constructSketch(configurationElement);
		return sketch;
	}

	@SuppressWarnings("unchecked")
	public <T extends ISketch> T constructSketch(Class<T> sketchInterface) {
		IConfigurationElement configurationElement = _ifaceToConfigElement
				.get(sketchInterface);
		ISketch sketch = constructSketch(configurationElement);
		return (T) sketch;
	}

	private ISketch constructSketch(IConfigurationElement configurationElement) {
		if (configurationElement != null) {
			try {
				String sketchId = configurationElement.getAttribute("id");
				Object extension = configurationElement
						.createExecutableExtension("implementation");
				ISketch sketch = (ISketch) extension;

				ISketchInternal sketchInternal = (ISketchInternal) sketch;
				sketchInternal.setSketchId(sketchId);

				return sketch;
			} catch (CoreException ex) {
				ex.printStackTrace();
			} catch (ClassCastException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

}
