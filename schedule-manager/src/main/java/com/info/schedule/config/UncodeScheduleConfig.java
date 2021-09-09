package com.asiainfo.schedule.config;

import com.asiainfo.schedule.zk.ZKManager.KEYS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "uncode.schedule",ignoreInvalidFields = true)
public class UncodeScheduleConfig{
	
	private String zkConnect;
	private String rootPath = "/uncode/schedule";
	private int zkSessionTimeout = 60000;
	private String zkUsername;
	private String zkPassword;
	private List<String> ipBlackList;
	
	
	private List<String> targetBean;
	private List<String> targetMethod;
	private List<String> taskDefineName;
	private List<String> cronExpression;
	private List<String> startTime;
	private List<String> period;
	private List<String> delay;
	private List<String> params;
	private List<String> type;
	private List<String> extKeySuffix;
	private List<String> beforeMethod;
	private List<String> afterMethod;
	private List<String> threadNum;
	
	
	public Map<String, String> getConfig(){
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(KEYS.zkConnectString.key, zkConnect);
		if(StringUtils.isNotBlank(rootPath)){
			properties.put(KEYS.rootPath.key, rootPath);
		}
		if(zkSessionTimeout > 0){
			properties.put(KEYS.zkSessionTimeout.key, zkSessionTimeout+"");
		}
		if(StringUtils.isNotBlank(zkUsername)){
			properties.put(KEYS.userName.key, zkUsername);
		}
		if(StringUtils.isNotBlank(zkPassword)){
			properties.put(KEYS.password.key, zkPassword);
		}
		StringBuilder sb = new StringBuilder();
		if(ipBlackList != null && ipBlackList.size() > 0){
			for(String ip:ipBlackList){
				sb.append(ip).append(",");
			}
			ipBlackList.remove(sb.lastIndexOf(","));
		}
		properties.put(KEYS.ipBlacklist.key, sb.toString());
		return properties;
	}
	
	
	public String getZkConnect() {
		return zkConnect;
	}
	public void setZkConnect(String zkConnect) {
		this.zkConnect = zkConnect;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public int getZkSessionTimeout() {
		return zkSessionTimeout;
	}
	public void setZkSessionTimeout(int zkSessionTimeout) {
		this.zkSessionTimeout = zkSessionTimeout;
	}
	public String getZkUsername() {
		return zkUsername;
	}
	public void setZkUsername(String zkUsername) {
		this.zkUsername = zkUsername;
	}
	public String getZkPassword() {
		return zkPassword;
	}
	public void setZkPassword(String zkPassword) {
		this.zkPassword = zkPassword;
	}
	public List<String> getIpBlackList() {
		return ipBlackList;
	}
	public void setIpBlackList(String ipBlackList) {
		this.ipBlackList = StringUtils.isBlank(ipBlackList)? null : Arrays.asList(ipBlackList.split(","));
	}


	public List<String> getTargetBean() {
		return targetBean;
	}


	public void setTargetBean(String targetBean) {
		this.targetBean = StringUtils.isBlank(targetBean)? null : Arrays.asList(targetBean.split(","));
	}


	public List<String> getTargetMethod() {
		return targetMethod;
	}


	public void setTargetMethod(String targetMethod) {
		this.targetMethod = StringUtils.isBlank(targetMethod)? null : Arrays.asList(targetMethod.split(","));
	}


	public List<String> getTaskDefineName() {
		return taskDefineName;
	}


	public void setTaskDefineName(String taskDefineName) {
		this.taskDefineName = StringUtils.isBlank(taskDefineName)? null : Arrays.asList(taskDefineName.split(","));
	}


	public List<String> getCronExpression() {
		return cronExpression;
	}


	public void setCronExpression(String cronExpression) {
		this.cronExpression = StringUtils.isBlank(cronExpression)? null : Arrays.asList(cronExpression.split(","));
	}


	public List<String> getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = StringUtils.isBlank(startTime)? null : Arrays.asList(startTime.split(","));
	}


	public List<String> getPeriod() {
		return period;
	}


	public void setPeriod(String period) {
		this.period = StringUtils.isBlank(period)? null : Arrays.asList(period.split(","));
	}


	public List<String> getDelay() {
		return delay;
	}


	public void setDelay(String delay) {
		this.delay = StringUtils.isBlank(delay)? null : Arrays.asList(delay.split(","));
	}


	public List<String> getParams() {
		return params;
	}


	public void setParams(String params) {
		this.params = StringUtils.isBlank(params)? null : Arrays.asList(params.split(","));
	}


	public List<String> getType() {
		return type;
	}


	public void setType(String type) {
		this.type = StringUtils.isBlank(type)? null : Arrays.asList(type.split(","));
	}


	public List<String> getExtKeySuffix() {
		return extKeySuffix;
	}


	public void setExtKeySuffix(String extKeySuffix) {
		this.extKeySuffix = StringUtils.isBlank(extKeySuffix)? null : Arrays.asList(extKeySuffix.split(","));
	}


	public List<String> getBeforeMethod() {
		return beforeMethod;
	}


	public void setBeforeMethod(String beforeMethod) {
		this.beforeMethod = StringUtils.isBlank(beforeMethod)? null : Arrays.asList(beforeMethod.split(","));
	}


	public List<String> getAfterMethod() {
		return afterMethod;
	}


	public void setAfterMethod(String afterMethod) {
		this.afterMethod = StringUtils.isBlank(afterMethod)? null : Arrays.asList(afterMethod.split(","));
	}

	public List<String> getThreadNum() {
		return threadNum;
	}


	public void setThreadNum(String threadNum) {
		this.threadNum = StringUtils.isBlank(threadNum)? null : Arrays.asList(threadNum.split(","));
	}

	
	
	
	

}
