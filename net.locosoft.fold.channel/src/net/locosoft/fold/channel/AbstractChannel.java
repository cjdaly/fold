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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.eclipsesource.json.JsonValue;
import com.github.rjeschke.txtmark.Processor;

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

	public boolean channelSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return true;
	}

	public void channelHttp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String htmlText = Processor.process("**fold** channel: "
				+ getChannelId() + " / " + getChannelNodeId() + " ^ "
				+ getChannelProject().getFullPath());
		response.getWriter().println(htmlText);
	}

	public final void setChannelId(String id) {
		_id = id;
	}

	public final IProject getChannelProject() {
		try {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
					.getRoot();
			IProject project = workspaceRoot.getProject(getChannelId());
			boolean workspaceStateChange = false;

			if (!project.exists()) {
				workspaceStateChange = true;
				project.create(null);
			}

			if (!project.isOpen()) {
				workspaceStateChange = true;
				project.open(null);
			}

			if (workspaceStateChange) {
				workspaceRoot.getWorkspace().save(true, null);
			}

			return project;
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private long _channelNodeId = -1;

	public final long getChannelNodeId() {
		if (_channelNodeId == -1) {
			INeo4jService neo4jService = Neo4jUtil.getNeo4jService();

			// TODO: move this bit to a less frequent spot
			ICypher cypherConstraint = neo4jService
					.constructCypher("CREATE CONSTRAINT ON (channel:fold_Channel) ASSERT channel.fold_channelId is UNIQUE");
			neo4jService.invokeCypher(cypherConstraint);

			ICypher cypher = neo4jService
					.constructCypher("MERGE (channel:fold_Channel {fold_channelId : {channelId}}) RETURN ID(channel)");
			cypher.addParameter("channelId", getChannelId());
			neo4jService.invokeCypher(cypher);

			JsonValue jsonValue = cypher.getResultDataRow(0);
			_channelNodeId = jsonValue.asLong();
		}
		return _channelNodeId;
	}

}
