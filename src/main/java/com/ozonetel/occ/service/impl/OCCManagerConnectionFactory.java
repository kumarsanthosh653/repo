package com.ozonetel.occ.service.impl;

import org.asteriskjava.manager.ManagerConnectionFactory;

/**	
 *	OCCManagerConnectionFactory.java
 *	@author NBabu
 *	Date  : Nov 13, 2009
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */

public class OCCManagerConnectionFactory  {
	
	
	private String hostname;
	
	private String username;
	
	private String password;

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

	public ManagerConnectionFactory getManagerConnectionFactory() {
		return new ManagerConnectionFactory(hostname, username, password);
	}
}