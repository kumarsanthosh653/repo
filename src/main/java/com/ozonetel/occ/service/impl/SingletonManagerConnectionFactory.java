package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.util.AppContext;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**	
 *	SingletonManagerConnection.java
 *	@author NBabu
 *	Date  : Oct 8, 2009
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */

public class SingletonManagerConnectionFactory extends UniversalManagerImpl{

	private static ManagerConnectionFactory factory;;
	

	public static ManagerConnectionFactory getManagerConnectionFactory(){
		if(null == factory){
//			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			ApplicationContext webApplicationContext = AppContext.getApplicationContext();
			OCCManagerConnectionFactory occFactory = (OCCManagerConnectionFactory)webApplicationContext.getBean("occManagerConnectionFactory");
			factory = occFactory.getManagerConnectionFactory();
			//factory = new ManagerConnectionFactory("172.16.15.95","phoneglue",  "phoneglue");
		}
		return factory;
	}
}
