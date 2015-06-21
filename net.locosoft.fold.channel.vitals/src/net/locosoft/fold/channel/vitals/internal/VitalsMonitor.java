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

package net.locosoft.fold.channel.vitals.internal;

import net.locosoft.fold.channel.vitals.IVitals;

public class VitalsMonitor implements Runnable {

	private VitalsChannel _vitalsChannel;
	private Thread _thread;
	private boolean _stopping = false;
	private boolean _stopped = true;

	public VitalsMonitor(VitalsChannel vitalsChannel) {
		_vitalsChannel = vitalsChannel;
		_thread = new Thread(this);
	}

	public boolean isRunning() {
		return !(_stopped || _stopping);
	}

	public void start() {
		_thread.start();
	}

	public void stop() {
		_stopping = true;
	}

	public void run() {
		while (!_stopping) {
			try {
				String[] vitalsIds = _vitalsChannel.getVitalsIds();
				for (String vitalsId : vitalsIds) {
					IVitals vitals = _vitalsChannel.getVitals(vitalsId);
					String vitalsAsJson = vitals.readVitalsAsJson(null);
					System.out.println("vitals: " + vitalsId);
					System.out.println("  " + vitalsAsJson);
				}

				Thread.sleep(10 * 1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		_stopped = true;
	}

}
