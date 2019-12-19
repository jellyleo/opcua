/**
 * Created by Jellyleo on 2019年12月16日
 * Copyright © 2019 jellyleo.com 
 * All rights reserved. 
 */
package com.jellyleo.opcua.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jellyleo.opcua.client.ClientHandler;
import com.jellyleo.opcua.entity.NodeEntity;

//import com.google.common.collect.Lists;

/**
 * @ClassName: OpcUaController
 * @Description: OpcUa控制器
 * @author Jellyleo
 * @date 2019年12月16日
 */
@Controller
public class CommonController {

	@Autowired
	private ClientHandler clientHandler;

	/**
	 * @MethodName: connect
	 * @Description: opcua连接并订阅变量
	 * @param request
	 * @param response
	 * @return
	 * @CreateTime 2019年12月16日 上午10:52:39
	 */
	@RequestMapping("/connect")
	@ResponseBody
	public String connect() {

		try {
			return clientHandler.connect();
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * @MethodName: disconnect
	 * @Description: disconnect
	 * @return
	 * @CreateTime 2019年12月18日 上午10:48:46
	 */
	@RequestMapping("/disconnect")
	@ResponseBody
	public String disconnect() {

		try {
			return clientHandler.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * @MethodName: subscribe
	 * @Description: subscribe
	 * @return
	 * @CreateTime 2019年12月18日 上午10:49:06
	 */
	@RequestMapping("/subscribe")
	@ResponseBody
	public String subscribe(HttpServletRequest request) {

		try {
			List<NodeEntity> nodes = Stream.of(request.getParameter("id").split(","))
					.map(id -> NodeEntity.builder().index(2).identifier(id).build()).collect(Collectors.toList());

			return clientHandler.subscribe(nodes);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * @MethodName: write
	 * @Description: 节点写入
	 * @param request
	 * @return
	 * @CreateTime 2019年12月18日 上午9:59:23
	 */
	@RequestMapping("/write")
	@ResponseBody
	public String write(HttpServletRequest request) {

		NodeEntity node = NodeEntity.builder().index(2).identifier(request.getParameter("id"))
				.value(request.getParameter("value")).type(request.getParameter("type")).build();

		try {
			return clientHandler.write(node);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * @MethodName: read
	 * @Description: read
	 * @param request
	 * @return
	 * @CreateTime 2019年12月19日 下午2:46:05
	 */
	@RequestMapping("/read")
	@ResponseBody
	public String read(HttpServletRequest request) {

		NodeEntity node = NodeEntity.builder().index(2).identifier(request.getParameter("id")).build();

		try {
			return clientHandler.read(node);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}
