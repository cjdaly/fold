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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.eclipsesource.json.JsonObject;

@SuppressWarnings("restriction")
public class Neo4jRestUtil {

	public static final String DATA_URI = "http://localhost:7474/db/data";
	public static final String CYPHER_URI = DATA_URI + "/transaction/commit";

	public static JsonObject doGetJson(String uri) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpClient
					.execute(new HttpGet(uri));

			String bodyText = EntityUtils.toString(response.getEntity());
			JsonObject jsonObject = JsonObject.readFrom(bodyText);
			return jsonObject;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static JsonObject doPostJson(String uri, JsonObject content) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.addHeader("Content-Type", "application/json");
			StringEntity stringEntity = new StringEntity(content.toString(),
					"UTF-8");
			httpPost.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);

			String bodyText = EntityUtils.toString(response.getEntity());
			JsonObject jsonObject = JsonObject.readFrom(bodyText);
			return jsonObject;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
