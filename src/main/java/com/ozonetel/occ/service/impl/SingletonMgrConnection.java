package com.ozonetel.occ.service.impl;

import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;

/**	
 *	SingletonManagerConnection.java
 *	@author NBabu
 *	Date  : Oct 8, 2009
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */

public class SingletonMgrConnection extends UniversalManagerImpl{

	private static ManagerConnection managerConnection;
	

	public static ManagerConnection getManagerConnection(){
		if(null == managerConnection){
			ManagerConnectionFactory factory = new ManagerConnectionFactory("172.16.15.203", "phoneglue", "phoneglue");
	        managerConnection = factory.createManagerConnection();
		}
		return managerConnection;
	}
}
