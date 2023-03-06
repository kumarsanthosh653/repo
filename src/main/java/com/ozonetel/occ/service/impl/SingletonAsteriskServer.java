package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.util.AppContext;
import org.asteriskjava.live.AsteriskServer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**	
 *	SingletonManagerConnection.java
 *	@author NBabu
 *	Date  : Oct 8, 2009
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */

public class SingletonAsteriskServer extends UniversalManagerImpl{

	private static AsteriskServer asteriskServer;;
	

	public static AsteriskServer getAsteriskServer(){
		if(null == asteriskServer){
//			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			ApplicationContext webApplicationContext = AppContext.getApplicationContext();
			OCCAsteriskServer occAsteriskServer = (OCCAsteriskServer)webApplicationContext.getBean("occAsteriskServer");
			asteriskServer = occAsteriskServer.getAsteriskServer();
			//asteriskServer = new DefaultAsteriskServer("172.16.15.95","phoneglue","phoneglue");
		}
		return asteriskServer;
	}
}
