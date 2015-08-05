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
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.neo4j.ICypher;
import net.locosoft.fold.neo4j.INeo4jService;
import net.locosoft.fold.neo4j.Neo4jUtil;
import net.locosoft.fold.util.FoldUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.eclipsesource.json.JsonValue;

public abstract class AbstractChannel implements IChannel, IChannelInternal {

	//
	// IChannel
	//

	private String _id;

	public String getChannelId() {
		return _id;
	}

	private String _description;

	public String getChannelData(String key, String... params) {
		switch (key) {
		case "channel.description":
			return _description;
		default:
			return null;
		}
	}

	//
	// IChannelInternal
	//

	private IChannelService _channelService;

	public IChannelService getChannelService() {
		return _channelService;
	}

	public void init(String channelId, IChannelService channelService,
			String channelDescription) {
		_id = channelId;
		_description = channelDescription;
		_channelService = channelService;
	}

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

		switch (request.getMethod()) {
		case "GET":
			channelHttpGet(request, response);
			break;
		case "POST":
			channelHttpPost(request, response);
			break;
		case "PUT":
			channelHttpPut(request, response);
			break;
		case "DELETE":
			channelHttpDelete(request, response);
			break;
		}
	}

	protected void channelHttpGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		channelHttpNotImplemented(request, response);
	}

	protected void channelHttpPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		channelHttpNotImplemented(request, response);
	}

	protected void channelHttpPut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		channelHttpNotImplemented(request, response);
	}

	protected void channelHttpDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		channelHttpNotImplemented(request, response);
	}

	private void channelHttpNotImplemented(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		PrintWriter writer = response.getWriter();

		String channelId = getChannelId();
		String thingName = getChannelData("thing", "name");
		writer.append("<html>\n<head>\n<title>");
		writer.append("fold: " + thingName + " / " + channelId);
		writer.append("</title>\n</head>\n<body>\n");

		writer.append("<p>");
		writer.append("Method " + request.getMethod() + " not implemented!");
		writer.append("</p>\n");

		writer.append("\n</body>\n</html>\n");
	}

	public final Properties getChannelConfigProperties(
			String propertiesFilePrefix) {
		String channelConfigFilePath = FoldUtil.getFoldConfigDir()
				+ "/channel/" + getChannelId() + "/" + propertiesFilePrefix
				+ ".properties";
		return FoldUtil.loadPropertiesFile(channelConfigFilePath);
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
