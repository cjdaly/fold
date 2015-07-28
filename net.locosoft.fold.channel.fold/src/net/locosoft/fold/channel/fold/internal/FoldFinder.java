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

package net.locosoft.fold.channel.fold.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.locosoft.fold.channel.chatter.IChatterChannel;
import net.locosoft.fold.sketch.pad.neo4j.HierarchyNode;
import net.locosoft.fold.sketch.pad.neo4j.PropertyAccessNode;
import net.locosoft.fold.util.FoldUtil;
import net.locosoft.fold.util.MonitorThread;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.eclipsesource.json.JsonObject;

@SuppressWarnings("restriction")
public class FoldFinder {

	private FoldChannel _foldChannel;

	public FoldFinder(FoldChannel foldChannel) {
		_foldChannel = foldChannel;
	}

	public void start() {
		_foldMonitor.start();
	}

	public void stop() {
		_foldMonitor.stop();
	}

	private MonitorThread _foldMonitor = new MonitorThread() {

		private String[] _foldIpAddrs;
		private int _currentFoldIpAddrIndex;
		private int _currentScanSegment;

		public boolean cycle() {

			if (_foldIpAddrs == null) {
				_foldIpAddrs = FoldUtil.getFoldIpAddrs();
				if (_foldIpAddrs.length == 0)
					return false;
				_currentFoldIpAddrIndex = 0;
				_currentScanSegment = 1;
			}

			String currentFoldIpAddr = _foldIpAddrs[_currentFoldIpAddrIndex];
			int lastDotIndex = currentFoldIpAddr.lastIndexOf('.');
			String subnetMask = currentFoldIpAddr
					.substring(0, lastDotIndex + 1);
			String currentScanIpAddr = subnetMask + _currentScanSegment;

			_currentScanSegment++;
			if (_currentScanSegment > 254) {
				_currentScanSegment = 1;
				_currentFoldIpAddrIndex++;
			}

			if (_currentFoldIpAddrIndex >= _foldIpAddrs.length) {
				_foldIpAddrs = null;
			} else {
				sendFoldPost(currentScanIpAddr);
			}

			return true;
		}
	};

	private String getIpAddrPrefix(String ipAddr) {
		int lastDotIndex = ipAddr.lastIndexOf('.');
		return ipAddr.substring(0, lastDotIndex);
	}

	private String getIpAddrSuffix(String ipAddr) {
		int lastDotIndex = ipAddr.lastIndexOf('.');
		return ipAddr.substring(lastDotIndex + 1, ipAddr.length());
	}

	void sendFoldPost(String ipAddr) {
		JsonObject jsonObject = new JsonObject();
		String thingName = _foldChannel.getChannelData("thing", "name");
		jsonObject.add("senderName", thingName);
		jsonObject.add("message", "hello");

		String foldPostUrl = "http://" + ipAddr + ":8421/fold/fold";

		boolean postError = false;
		String postResponse = null;

		try (CloseableHttpClient httpClient = HttpClientBuilder.create()
				.disableAutomaticRetries().build()) {
			HttpPost httpPost = new HttpPost(foldPostUrl);
			httpPost.addHeader("Content-Type", "application/json");
			StringEntity stringEntity = new StringEntity(jsonObject.toString(),
					"UTF-8");
			httpPost.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);

			postResponse = EntityUtils.toString(response.getEntity());
		} catch (Exception ex) {
			postResponse = ex.toString();
			postError = true;
		}

		IChatterChannel chatterChannel = _foldChannel.getChannelService()
				.getChannel(IChatterChannel.class);

		JsonObject chatterData = new JsonObject();
		chatterData.add("postResponse", postResponse);
		chatterData.add("postError", postError);
		chatterData.add("postIpAddr", ipAddr);
		long chatterItemOrdinal = chatterChannel.createChatterItem(
				"sent FoldFinder post to ${postIpAddr}", "foldFinder",
				chatterData);

		HierarchyNode foldChannelNode = new HierarchyNode(
				_foldChannel.getChannelNodeId());
		long subnetsNodeId = foldChannelNode.getSubId("subnets", true);
		HierarchyNode subnetsNode = new HierarchyNode(subnetsNodeId);
		long ipAddrPrefixNodeId = subnetsNode.getSubId(getIpAddrPrefix(ipAddr),
				true);
		HierarchyNode ipAddrPrefixNode = new HierarchyNode(ipAddrPrefixNodeId);
		long ipAddrSuffixNodeId = ipAddrPrefixNode.getSubId(
				getIpAddrSuffix(ipAddr), true);
		PropertyAccessNode props = new PropertyAccessNode(ipAddrSuffixNodeId);
		props.setValue("lastSend_postError", postError);
		props.setValue("lastSend_chatterItemOrdinal", chatterItemOrdinal);
	}

	void receiveFoldPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JsonObject jsonObject = JsonObject.readFrom(request.getReader());

		String senderName = jsonObject.getString("senderName", null);
		String message = jsonObject.getString("message", null);

		if ((senderName != null) && (message != null)) {
			String senderIpAddr = request.getRemoteAddr();
			IChatterChannel chatterChannel = _foldChannel.getChannelService()
					.getChannel(IChatterChannel.class);
			JsonObject chatterData = new JsonObject();
			chatterData.add("senderIpAddr", senderIpAddr);
			chatterData.add("senderName", senderName);
			chatterData.add("message", message);

			long chatterItemOrdinal = chatterChannel.createChatterItem(
					"received FoldFinder post from ${senderIpAddr}",
					"foldFinder", chatterData);

			HierarchyNode foldChannelNode = new HierarchyNode(
					_foldChannel.getChannelNodeId());
			long subnetsNodeId = foldChannelNode.getSubId("subnets", true);
			HierarchyNode subnetsNode = new HierarchyNode(subnetsNodeId);
			long ipAddrPrefixNodeId = subnetsNode.getSubId(
					getIpAddrPrefix(senderIpAddr), true);
			HierarchyNode ipAddrPrefixNode = new HierarchyNode(
					ipAddrPrefixNodeId);
			long ipAddrSuffixNodeId = ipAddrPrefixNode.getSubId(
					getIpAddrSuffix(senderIpAddr), true);
			PropertyAccessNode props = new PropertyAccessNode(
					ipAddrSuffixNodeId);
			props.setValue("lastReceive_senderName", senderName);
			props.setValue("lastReceive_chatterItemOrdinal", chatterItemOrdinal);
		}
	}
}
