package com.ly.biz;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ly.model.GitEntity;
import com.ly.model.PublishOverSSHEntity;

import static com.ly.constants.PluginsVersion.*;

@SuppressWarnings("unused")
public class CreateXml {
	private String xmlString;
	
	/**
	 * 生成一个默认不带任何配置的jenkins config.xml
	 * @return
	 */
	public CreateXml createDefultXml() {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("maven2-moduleset")
				.addAttribute("plugin", MAVEN_VERSION);
		rootElement.addElement("actions");
		rootElement.addElement("description");
		rootElement.addElement("keepDependencies").setText("false");
		rootElement.addElement("properties");
		
		rootElement.addElement("canRoam").setText("true");
		rootElement.addElement("disabled").setText("false");
		rootElement.addElement("blockBuildWhenDownstreamBuilding").setText("false");
		rootElement.addElement("blockBuildWhenUpstreamBuilding").setText("false");
		rootElement.addElement("triggers");
		
		rootElement.addElement("concurrentBuild").setText("false");
		rootElement.addElement("aggregatorStyleBuild").setText("true");
		rootElement.addElement("incrementalBuild").setText("false");
		rootElement.addElement("ignoreUpstremChanges").setText("false");
		rootElement.addElement("ignoreUnsuccessfulUpstreams").setText("false");
		rootElement.addElement("archivingDisabled").setText("false");
		rootElement.addElement("siteArchivingDisabled").setText("false");
		rootElement.addElement("fingerprintingDisabled").setText("false");
		rootElement.addElement("resolveDependencies").setText("false");
		rootElement.addElement("processPlugins").setText("false");
		rootElement.addElement("mavenValidationLevel").setText("-1");
		rootElement.addElement("runHeadless").setText("false");
		rootElement.addElement("disableTriggerDownstreamProjects").setText("false");
		rootElement.addElement("blockTriggerWhenBuilding").setText("true");
		
		rootElement.addElement("settings").addAttribute("class", "jenkins.mvn.FilePathSettingsProvider")
								.addElement("path");
		
		rootElement.addElement("globalSettings").addAttribute("class", "jenkins.mvn.DefaultGlobalSettingsProvider");
		
		rootElement.addElement("reporters");
		rootElement.addElement("publishers");
		rootElement.addElement("buildWrappers");
		rootElement.addElement("prebuilders");
		rootElement.addElement("postbuilders");
		
		Element runPostStepsIfResultElement = rootElement.addElement("runPostStepsIfResult");
		runPostStepsIfResultElement.addElement("name").setText("FAILURE");
		runPostStepsIfResultElement.addElement("ordinal").setText("2");
		runPostStepsIfResultElement.addElement("color").setText("RED");
		runPostStepsIfResultElement.addElement("completeBuild").setText("true");
		
		xmlString = document.asXML();
		
		return this;
	}
	
	/**
	 * 增加一个git为源码管理器的配置，支持用户名密码模式，credentialsId为用户名密码加密
	 * @param gitUrl
	 * @param credentialsId
	 * @param branch
	 * @return
	 */
	public CreateXml addGitCodeManage(GitEntity entity) {
		Element scmElement = DocumentHelper.createElement("scm")
								.addAttribute("class", "hudson.plugins.git.GitSCM")
								.addAttribute("plugin", GIT_VERSION);
		
		scmElement.addElement("configVersion").setText("2");
		
		Element userRemoteConfigsElement = scmElement.addElement("userRemoteConfigs");
		
		Element userRemoteConfigsSonElement = userRemoteConfigsElement.addElement("hudson.plugins.git.UserRemoteConfig");
								
		userRemoteConfigsSonElement.addElement("url").setText(entity.getUrl());
		userRemoteConfigsSonElement.addElement("credentialsId").setText(entity.getCredentialsId());
		
		Element barnchesElement = scmElement.addElement("branches");
		barnchesElement.addElement("hudson.plugins.git.BranchSpec").addElement("name")
									.setText("*/" + entity.getBranch());
		
		scmElement.addElement("doGenerateSubmoduleConfigurations").setText("false");
		scmElement.addElement("submoduleCfg").addAttribute("class", "list");
		scmElement.addElement("extensions");
		
		try {
			Document defultXml = DocumentHelper.parseText(this.xmlString);
			Element root = defultXml.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> elements = root.elements();
			
			int i = elements.indexOf(root.element("canRoam"));
			elements.add(i, scmElement);
			
			xmlString = defultXml.asXML();
		} catch (DocumentException e) {
			e.printStackTrace();
		}	
		return this;
	}
	
	/**
	 * 生成一个build完成后对服务器的操作，对应jenkins中的Post Steps增加ssh，可增加多台ssh修改
	 * @return
	 */
	public CreateXml afterPostStepsAddSSH(List<PublishOverSSHEntity> entitys) {
		Document defultXml;
		try {
			defultXml = DocumentHelper.parseText(this.xmlString);
			Element root = defultXml.getRootElement();
			
			root.element("postbuilders").add(createSSH(entitys));
			
			xmlString = defultXml.asXML();
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}
	
	/**
	 * 生成n个ssh
	 * @param entity
	 * @return
	 */
	private Element createSSH(List<PublishOverSSHEntity> entitys) {
		Element publishOverSSHElement = DocumentHelper.createElement("jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin")
				.addAttribute("plugin", PUBLISH_OVER_SSH_VERSION);
		
		Element delegateElemnt1 = publishOverSSHElement.addElement("delegate");
		delegateElemnt1.addElement("consolePrefix").setText("SSH: ");
		
		Element delegateElemnt2 = delegateElemnt1.addElement("delegate").addAttribute("plugin", "publish-over@0.22");
		Element publishersSonElement = delegateElemnt2.addElement("publishers");
		
		entitys.forEach(entity -> {
			Element bapSshPublisherElement = publishersSonElement.addElement("jenkins.plugins.publish__over__ssh.BapSshPublisher")
																	.addAttribute("plugin", PUBLISH_OVER_SSH_VERSION);
			bapSshPublisherElement.addElement("configName").setText(entity.getUser() + "@" + entity.getIp());
			bapSshPublisherElement.addElement("verbose").setText("false");
			
			Element transfersElement = bapSshPublisherElement.addElement("transfers");
			Element bapSshTransferElement = transfersElement.addElement("jenkins.plugins.publish__over__ssh.BapSshTransfer");
			bapSshTransferElement.addElement("remoteDirectory").setText(entity.getRemoteDirectory());
			bapSshTransferElement.addElement("sourceFiles").setText(entity.getSourceFiles());
			bapSshTransferElement.addElement("excludes");
			bapSshTransferElement.addElement("removePrefix").setText(entity.getRemovePrefix());
			bapSshTransferElement.addElement("remoteDirectorySDF").setText("false");
			bapSshTransferElement.addElement("flatten").setText("false");
			bapSshTransferElement.addElement("cleanRemote").setText("false");
			bapSshTransferElement.addElement("noDefaultExcludes").setText("false");
			bapSshTransferElement.addElement("makeEmptyDirs").setText("false");
			bapSshTransferElement.addElement("patternSeparator").setText("[, ]+");
			bapSshTransferElement.addElement("execCommand").setText(entity.getExecCommand());
			bapSshTransferElement.addElement("execTimeout").setText("120000");
			bapSshTransferElement.addElement("usePty").setText("false");
			bapSshTransferElement.addElement("useAgentForwarding").setText("false");
			
			bapSshPublisherElement.addElement("useWorkspaceInPromotion").setText("false");
			bapSshPublisherElement.addElement("usePromotionTimestamp").setText("false");
		});	
		
		delegateElemnt2.addElement("continueOnError").setText("false");
		delegateElemnt2.addElement("failOnError").setText("false");
		delegateElemnt2.addElement("alwaysPublishFromMaster").setText("false");
		delegateElemnt2.addElement("hostConfigurationAccess").addAttribute("class", "jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin")
															.addAttribute("reference", "../..");
		
		return publishOverSSHElement;
	}
	
	
	public String toStrig() {
		return this.xmlString;
	}
	
	public static void main(String[] args) {
		CreateXml createXml = new CreateXml();
		createXml.createDefultXml();
		createXml.addGitCodeManage(new GitEntity("111", "222", "uat"));

		
		List<PublishOverSSHEntity> list = ImmutableList.of(PublishOverSSHEntity.builder().user("111").ip("11.22.33.44")
													.execCommand("sh 1").remoteDirectory("1").sourceFiles("1")
													.removePrefix("2").build(),
													PublishOverSSHEntity.builder().user("222").ip("2222")
													.execCommand("sh 2").remoteDirectory("2").sourceFiles("2")
													.removePrefix("2").build()
				
				);
		
//		createXml.afterTheBuildAddSSH(list);
		
		System.out.println(createXml.xmlString);
	}
}
