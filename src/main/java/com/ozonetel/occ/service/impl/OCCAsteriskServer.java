package com.ozonetel.occ.service.impl;

import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;

/**	
 *	OCCAsteriskServer.java
 *	@author NBabu
 *	Date  : Nov 13, 2009
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */

public class OCCAsteriskServer {
	
	
	private String hostname;
	
	private String username;
	
	private String password;
	
	private Integer maxChannelCount;
	
	private Integer maxThreadCount;
	
	private String port;
	
	private String screenPopUrl;
	
	public String getScreenPopUrl() {
		return screenPopUrl;
	}

	public void setScreenPopUrl(String screenPopUrl) {
		this.screenPopUrl = screenPopUrl;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AsteriskServer getAsteriskServer() {
		return new DefaultAsteriskServer(hostname, username, password);
	}

	public Integer getMaxChannelCount() {
		return maxChannelCount;
	}

	public void setMaxChannelCount(Integer maxChannelCount) {
		this.maxChannelCount = maxChannelCount;
	}

	public Integer getMaxThreadCount() {
		return maxThreadCount;
	}

	public void setMaxThreadCount(Integer maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}

}