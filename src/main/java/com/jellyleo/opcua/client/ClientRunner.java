/**
 * Created by Jellyleo on 2019年12月19日
 * Copyright © 2019 jellyleo.com 
 * All rights reserved. 
 */
package com.jellyleo.opcua.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

import com.jellyleo.opcua.cert.KeyStoreLoader;
import com.jellyleo.opcua.config.Properties;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ClientRunner
 * @Description: 客户端启动类
 * @author Jellyleo
 * @date 2019年12月19日
 */
@Slf4j
@Component
public class ClientRunner {

	private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();

	@Autowired
	private Properties properties;

	@Autowired
	private KeyStoreLoader keyStoreLoader;

	/**
	 * @MethodName: run
	 * @Description: 启动
	 * @return
	 * @throws Exception
	 * @CreateTime 2019年12月18日 下午4:03:47
	 */
	public OpcUaClient run() throws Exception {

		OpcUaClient client = createClient();

		future.whenCompleteAsync((c, ex) -> {
			if (ex != null) {
				log.error("Error running example: {}", ex.getMessage(), ex);
			}

			try {
				c.disconnect().get();
				Stack.releaseSharedResources();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error disconnecting:", e.getMessage(), e);
			}
		});

		return client;
	}

	/**
	 * @MethodName: createClient
	 * @Description: 创建客户端
	 * @return
	 * @throws Exception
	 * @CreateTime 2019年12月18日 下午4:02:54
	 */
	private OpcUaClient createClient() throws Exception {

		Path securityTempDir = Paths.get(properties.getCertPath(), "security");

		Files.createDirectories(securityTempDir);
		if (!Files.exists(securityTempDir)) {
			log.error("unable to create security dir: " + securityTempDir);
			return null;
		}

		KeyStoreLoader loader = keyStoreLoader.load(securityTempDir);

		// 搜索OPC节点
		List<EndpointDescription> endpoints = null;
		try {
			endpoints = DiscoveryClient.getEndpoints(properties.getEndpointUrl()).get();
		} catch (Throwable e) {
			// try the explicit discovery endpoint as well
			String discoveryUrl = properties.getEndpointUrl();

			if (!discoveryUrl.endsWith("/")) {
				discoveryUrl += "/";
			}
			discoveryUrl += "discovery";

			log.info("Trying explicit discovery URL: {}", discoveryUrl);
			endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
		}

		EndpointDescription endpoint = endpoints.stream()
				.filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri())).filter(endpointFilter())
				.findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

		OpcUaClientConfig config = OpcUaClientConfig.builder()
				.setApplicationName(LocalizedText.english("jlOpcUaClient"))
				.setApplicationUri("urn:Jellyleo:UnifiedAutomation:UaExpert@Jellyleo")
				.setCertificate(loader.getClientCertificate()).setKeyPair(loader.getClientKeyPair())
				.setEndpoint(endpoint).setIdentityProvider(new UsernameProvider("jellyleo", "123456"))
//				.setIdentityProvider(new AnonymousProvider()) // 匿名验证
				.setRequestTimeout(Unsigned.uint(5000)).build();

		return OpcUaClient.create(config);
	}

	/**
	 * @MethodName: endpointFilter
	 * @Description: endpointFilter
	 * @return
	 * @CreateTime 2019年12月18日 下午4:06:22
	 */
	private Predicate<EndpointDescription> endpointFilter() {
		return e -> true;
	}

	/**
	 * @return the future
	 */
	public CompletableFuture<OpcUaClient> getFuture() {
		return future;
	}

}
