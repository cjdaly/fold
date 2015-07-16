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
import net.locosoft.fold.channel.vitals.StaticVitals;
import net.locosoft.fold.sketch.pad.neo4j.ChannelItemNode;

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
		_stopped = false;
		while (!_stopping) {
			try {
				String[] vitalsIds = _vitalsChannel.getVitalsIds();
				long currentTimeMillis = System.currentTimeMillis();
				for (String vitalsId : vitalsIds) {
					try {
						IVitals vitals = _vitalsChannel.getVitals(vitalsId);
						if (vitals instanceof StaticVitals) {
							StaticVitals staticVitals = (StaticVitals) vitals;
							if (staticVitals.isCheckTime(currentTimeMillis)) {
								ChannelItemNode vitalsNode = new ChannelItemNode(
										_vitalsChannel, "Vitals");
								long vitalsNodeId = vitalsNode
										.nextOrdinalNodeId();
								staticVitals.checkVitals(vitalsNodeId);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				Thread.sleep(5 * 1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		_stopped = true;
	}

}
