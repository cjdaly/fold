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

package net.locosoft.fold.channel;

import net.locosoft.fold.neo4j.INode;

import org.eclipse.core.resources.IProject;

public interface IChannelInternal {

	void setChannelId(String id);

	void init();

	void fini();

	IProject getChannelProject();

	INode getChannelNode();
}
