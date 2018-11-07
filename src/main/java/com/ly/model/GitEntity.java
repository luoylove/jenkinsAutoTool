package com.ly.model;

import lombok.Data;
import lombok.NonNull;

@Data
/**
 * 后续处理rsakey
 */
public class GitEntity {
	/**
	 * git项目地址
	 */
	@NonNull
	private String url;
	
	/**
	 * git用户名密码生成的登录标识
	 */
	@NonNull
	private String credentialsId;
	
	/**
	 * 代码分支
	 */
	@NonNull
	private String branch;
}
