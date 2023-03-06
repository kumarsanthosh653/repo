/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rajesh
 */
public class Zoho {
    
   private static Logger logger = Logger.getLogger(Zoho.class.getName());
	/**
	 * parameters used for Zoho API for CTI integration
	 * @param	ctiname			-	your service name provided by Zoho for Zoho API usage
	 * @param	authtoken		-	authtoken provided by Zoho and configured in TPI by the customer
	 * @param	currenttime		-	current epoch time in seconds (GMT)
	 * @param	callrefid		-	unique call reference id. This should be unique in all Zoho API calls (for all call state) to refer a single call
	 * @param	ctinumber		-	TPI number
	 * @param	custnumber		-	Customer Number
	 * @param	ctiagentref		-	TPI clientname/extn/callforwardno in which the user attended the call and hungup the call
	 * @param	dtmfcode		-	DTMF Code pressed by the customer on hearing the welcome IVR message like press 1 for sales, press 2 for support, etc.,
	 * @param	direction		-	type of call direction, either inbound or outbound
	 * @param	password		-	password shared by Zoho for you ie., TPI (Third Party Integrator)	
	 */	
   public Zoho(){
      
   }

}
