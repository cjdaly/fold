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

public abstract class MonitorThread implements Runnable {

	private Thread _thread = new Thread(this);
	private boolean _stopping = false;
	private boolean _stopped = true;

	protected long getSleepTimePreCycle() {
		return 5 * 1000;
	}

	protected long getSleepTimePostCycle() {
		return 30 * 1000;
	}

	public void start() {
		_thread.start();
	}

	public void stop() {
		_stopping = true;
	}

	public boolean isRunning() {
		return !(_stopped || _stopping);
	}

	public void run() {
		while (!_stopping) {
			try {
				Thread.sleep(getSleepTimePreCycle());
				_stopped = !cycle();
				Thread.sleep(getSleepTimePostCycle());
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		_stopped = true;
	}

	public abstract boolean cycle() throws Exception;
}
