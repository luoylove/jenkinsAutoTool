package com.ly.biz;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ly.model.GitEntity;
import com.ly.model.PublishOverSSHEntity;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;

/**
 * java-client-api 项目
 * 
 * git：https://github.com/jenkinsci/java-client-api
 * @author luoyoujun
 *
 */
public class JenkinsClient {
	
	private JenkinsServer jenkinsServer;
	
	public JenkinsClient(String url) throws URISyntaxException {
		this.jenkinsServer = new JenkinsServer(new URI(url));
	}
	
	/**
	 * 获取jenkins实例
	 * @param url
	 * @param userName
	 * @param passWord
	 * @throws URISyntaxException
	 */
	public JenkinsClient(String url, String userName, String passWord) throws URISyntaxException {
		this.jenkinsServer = new JenkinsServer(new URI(url), userName, passWord);
	}
	
	/**
	 * 获取job
	 * @param jobName
	 * @return
	 */
	public Job getJob(String jobName) {
		Job job = null;
		try {
			job = this.jenkinsServer.getJob(jobName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return job;
	}
	
	/**
	 * 获取job xml
	 * @param jobName
	 * @return
	 * @throws IOException
	 */
	public String getJobXml(String jobName) throws IOException {
		return this.jenkinsServer.getJobXml(jobName);
	}
	
	/**
	 * 获取所有job
	 * @return
	 */
	public Map<String, Job> getJobs() {
		Map<String, Job> jobs = Maps.newHashMap();
		try {
			jobs = this.jenkinsServer.getJobs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobs;
	}
	
	/**
	 * 新建一个job
	 * @param jobName
	 * @param jobXml
	 */
	public void createJob(String jobName, String jobXml) {
		try {
			jenkinsServer.createJob(jobName, jobXml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		JenkinsClient jenkinsClient = new JenkinsClient("http://106.12.26.52:10001/", "test", "test");
		
		PublishOverSSHEntity entity = PublishOverSSHEntity.builder().user("root").ip("106.12.26.52")
														.sourceFiles("testAuto/target/*.jar")
														.removePrefix("testAuto/target/")
														.remoteDirectory("apps/jar_bak/")
														.execCommand("mkdir /root/apps/jar_bak/111").build();
		CreateXml xml = new CreateXml().createDefultXml().addGitCodeManage(new GitEntity("https://github.com/luoylove/autotest.git", 
									"111111111111", "master")).afterPostStepsAddSSH(ImmutableList.of(entity));
		
		jenkinsClient.createJob("test3", xml.toStrig());
		
		jenkinsClient.getJob("test3").build();
	} 
}
