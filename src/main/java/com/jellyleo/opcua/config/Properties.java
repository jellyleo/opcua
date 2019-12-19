/**
 * Created by Jellyleo on 2019年12月19日
 * Copyright © 2019 jellyleo.com 
 * All rights reserved. 
 */
package com.jellyleo.opcua.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * @ClassName: Properties
 * @Description: OpcUa参数
 * @author Jellyleo
 * @date 2019年12月19日
 */
@Getter
@Component
public class Properties {
	@Value("${opcua.server.endpoint.url}")
	private String endpointUrl;
	@Value("${opcua.server.idp.username}")
	private String idpUsername;
	@Value("${opcua.server.idp.password}")
	private String idpPassword;
	@Value("${opcua.client.app.name}")
	private String appName;
	@Value("${opcua.client.app.uri}")
	private String appUri;
	@Value("${opcua.client.cert.path}")
	private String certPath;
	@Value("${opcua.client.cert.file}")
	private String certFile;
	@Value("${opcua.client.cert.alias}")
	private String certAlias;
	@Value("${opcua.client.cert.common.name}")
	private String commonName;
	@Value("${opcua.client.cert.organization}")
	private String organization;
	@Value("${opcua.client.cert.organization.unit}")
	private String orgUnit;
	@Value("${opcua.client.cert.locality.name}")
	private String localityName;
	@Value("${opcua.client.cert.state.name}")
	private String stateName;
	@Value("${opcua.client.cert.country.code}")
	private String countryCode;
	@Value("${opcua.client.cert.dns.name}")
	private String dnsName;
	@Value("${opcua.client.cert.ip.address}")
	private String ipAddress;
	@Value("${opcua.client.cert.keystore.password}")
	private String keyPassword;
}
