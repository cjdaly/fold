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

package net.locosoft.fold.neo4j.internal;

import java.net.URI;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.eclipsesource.json.JsonObject;

@SuppressWarnings("restriction")
public class Neo4jBridge {

	public Neo4jBridge() {
	}

	public JsonObject do_GET(URI uri) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient
					.execute(new HttpGet(uri));

			String bodyText = EntityUtils.toString(response.getEntity());
			System.out.println(">>> " + bodyText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public JsonObject do_POST(URI uri, JsonObject content) {
		return null;
	}

}
