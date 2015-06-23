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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public abstract class AbstractChannel implements IChannel, IChannelInternal {

	private String _id;

	public String getChannelId() {
		return _id;
	}

	//
	// IChannelInternal
	//

	public void init() {
	}

	public void fini() {
	}

	public final void setChannelId(String id) {
		_id = id;
	}

	public final IProject getChannelProject() {
		try {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
					.getRoot();
			IProject project = workspaceRoot.getProject(getChannelId());
			if (!project.exists())
				project.create(null);
			if (!project.isOpen())
				project.open(null);
			return project;
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public final INode getChannelNode() {
		return null;
	}

}
