/**
 * Created by Jellyleo on 2019年12月18日
 * Copyright © 2019 jellyleo.com 
 * All rights reserved. 
 */
package com.jellyleo.opcua.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NodeEntity
 * @Description: NodeEntity
 * @author Jellyleo
 * @date 2019年12月18日
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NodeEntity {

	private Integer index;
	private String identifier;
	private Object value;
	private String type;
	private Integer clientHandle;
}
