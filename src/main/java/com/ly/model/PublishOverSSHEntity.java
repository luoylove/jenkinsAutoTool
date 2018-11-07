package com.ly.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 构建job中的ssh连接属性，ssh的密码或者key在系统设置中填写
 * 基于jenkins的 publish over ssh插件
 * 
 * @author luoy
 *
 */
@Builder
@Getter
@Setter
public class PublishOverSSHEntity {
	
	/**
	 * 服务器登录名
	 */
	private String user;
	
	/**
	 * 服务器ip,与系统设置同
	 */
	private String ip;
	
	/**
	 * 要上载到服务器的文件。
	 * 字符串是Ant文件集eg的逗号分隔的include列表。* * / *。jar’(参见Ant手册中的模式)。
	 * 这个文件集的基本目录是工作区。如/var/lib/jenkins/workspace，一般maven编译后该项填写为pojectName/xx.jar
	 */
	private String sourceFiles;
	
	/**
	 * 忽略文件夹部分
	 * 比如sourceFiles填写的是pojectName/xx.jar,上传时候需要忽略前面文件夹名
	 * 只需要上传xx.jar,此时该项填写pojectName/
	 * 
	 */
	private String removePrefix;
	
	/**
	 * 上传远程服务器文件夹名字，基于jenkins全局变量中配置的文件夹之下
	 * 如果远程服务器无该文件夹，将创建该文件夹
	 */
	private String remoteDirectory;
	
	/**
	 * 远程服务器执行命令
	 */
	private String execCommand;

}
