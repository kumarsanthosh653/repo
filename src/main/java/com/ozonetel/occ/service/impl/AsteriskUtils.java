package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.asteriskjava.live.AsteriskServer;

/**
 *	AsteriskUtils.java
 *	NarayanaBabu.Nalluri
 *	Date : Aug 8, 2010
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */
public class AsteriskUtils {
	
	//[2004!1!0!00:01:17!0!0, 2005!1!0!01:32:47!0!0]
	
	/**
	 * @return
	 */
	protected synchronized List<String> getAvailableAgents(AsteriskServer asteriskServer) {
		List<String> agentsList = new ArrayList<String>();
		List<String> list = asteriskServer.executeCliCommand("meetme list concise");
		System.out.println(list);
		if (null != list) {
			int size = list.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					String string1 = list.get(i);
					if (null != string1) {
						String string2 = string1.split("!")[0];
						String string3 = string1.split("!")[1];
						if (null != string2 && null != string3) {
							String element = string2.trim();
							int busyCount = -1;
							try{
								busyCount = Integer.parseInt(string3);
							}catch (Exception e) {}
							if(1 == busyCount)
								agentsList.add(element);
						}
					}
				}
			}
		}
		return agentsList;
	}
}