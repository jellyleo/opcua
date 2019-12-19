/**
 * Created by Jellyleo on 2019年12月12日
 * Copyright © 2019 jellyleo.com 
 * All rights reserved. 
 */
package com.jellyleo.opcua.client;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.jellyleo.opcua.entity.NodeEntity;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

/**
 * @ClassName: ClientHandler
 * @Description: 客户端处理
 * @author Jellyleo
 * @date 2019年12月12日
 */
@Slf4j
@Service
public class ClientHandler {

	// 客户端实例
	private OpcUaClient client = null;

	@Autowired
	private ClientRunner clientRunner;

	/**
	 * 
	 * @MethodName: connect
	 * @Description: connect
	 * @throws Exception
	 * @CreateTime 2019年12月18日 上午10:41:09
	 */
	public String connect() throws Exception {

		if (client != null) {
			return "客户端已创建";
		}

		client = clientRunner.run();

		if (client == null) {
			return "客户端配置实例化失败";
		}

		// 创建连接
		client.connect().get();
		return "创建连接成功";
	}

	/**
	 * @MethodName: disconnect
	 * @Description: 断开连接
	 * @return
	 * @throws Exception
	 * @CreateTime 2019年12月18日 上午10:45:21
	 */
	public String disconnect() throws Exception {

		if (client == null) {
			return "连接已断开";
		}

		// 断开连接
		client.disconnect().get();
		client = null;
		return "断开连接成功";
	}

	/**
	 * @MethodName: subscribe
	 * @Description: 订阅节点变量
	 * @throws Exception
	 * @CreateTime 2019年12月18日 上午10:38:11
	 */
	public String subscribe(List<NodeEntity> nodes) throws Exception {

		if (client == null) {
			return "找不到客户端，操作失败";
		}

//		List<Node> ns = client.getAddressSpace().browse(new NodeId(2, "模拟通道一.模拟设备一")).get();

		// 查询订阅对象，没有则创建
		UaSubscription subscription = null;
		ImmutableList<UaSubscription> subscriptionList = client.getSubscriptionManager().getSubscriptions();
		if (CollectionUtils.isEmpty(subscriptionList)) {
			subscription = client.getSubscriptionManager().createSubscription(1000.0).get();
		} else {
			subscription = subscriptionList.get(0);
		}

		// 监控项请求列表
		List<MonitoredItemCreateRequest> requests = new ArrayList<>();

		if (!CollectionUtils.isEmpty(nodes)) {
			for (NodeEntity node : nodes) {
				// 创建监控的参数
				MonitoringParameters parameters = new MonitoringParameters(subscription.nextClientHandle(), 1000.0, // sampling
						// interval
						null, // filter, null means use default
						Unsigned.uint(10), // queue size
						true // discard oldest
				);
				// 创建订阅的变量， 创建监控项请 求
				MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
						new ReadValueId(new NodeId(node.getIndex(), node.getIdentifier()), AttributeId.Value.uid(),
								null, null),
						MonitoringMode.Reporting, parameters);
				requests.add(request);
			}
		}

		// 创建监控项，并且注册变量值改变时候的回调函数
		subscription.createMonitoredItems(TimestampsToReturn.Both, requests, (item, id) -> {
			item.setValueConsumer((i, v) -> {
				log.info("item={}, value={}", i.getReadValueId().getNodeId(), v.getValue());
			});
		}).get();

		return "订阅成功";
	}

	/**
	 * @MethodName: write
	 * @Description: 变节点量写入
	 * @param node
	 * @throws Exception
	 * @CreateTime 2019年12月18日 上午9:51:40
	 */
	public String write(NodeEntity node) throws Exception {

		if (client == null) {
			return "找不到客户端，操作失败";
		}

		NodeId nodeId = new NodeId(node.getIndex(), node.getIdentifier());
		Variant value = null;
		switch (node.getType()) {
		case "int":
			value = new Variant(Integer.parseInt(node.getValue().toString()));
			break;
		case "boolean":
			value = new Variant(Boolean.parseBoolean(node.getValue().toString()));
			break;
		}
		DataValue dataValue = new DataValue(value, null, null);

		StatusCode statusCode = client.writeValue(nodeId, dataValue).get();

		return "节点【" + node.getIdentifier() + "】写入状态：" + statusCode.isGood();
	}

	/**
	 * @MethodName: read
	 * @Description: 读取
	 * @param node
	 * @return
	 * @throws Exception
	 * @CreateTime 2019年12月19日 下午2:40:34
	 */
	public String read(NodeEntity node) throws Exception {

		if (client == null) {
			return "找不到客户端，操作失败";
		}

		NodeId nodeId = new NodeId(node.getIndex(), node.getIdentifier());
		VariableNode vnode = client.getAddressSpace().createVariableNode(nodeId);
		DataValue value = vnode.readValue().get();
		log.info("Value={}", value);

		Variant variant = value.getValue();
		log.info("Variant={}", variant.getValue());

		log.info("BackingClass={}", BuiltinDataType.getBackingClass(variant.getDataType().get()));

		return "节点【" + node.getIdentifier() + "】：" + variant.getValue();
	}
}
