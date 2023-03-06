/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.HttpUtils;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author rajesh
 */
public class CheckManualDialStatus extends OCCManagerImpl implements Command {

    private String ucid;

    public CheckManualDialStatus() {
    }

    public CheckManualDialStatus(HttpServletRequest request) {
    }

    public CheckManualDialStatus(String ucid) {
        this.ucid = ucid;
    }

    @Override
    public String execute() {
        HttpUtils httpUtils = new HttpUtils();
        String response = "";
        try {
            AppProperty appProperty = (AppProperty) getBean("appProperty");
            String manualDialCheckUrl = appProperty.getManualDialCheckUrl();
//            response = httpUtils.doGet("http://172.16.15.120/kookooapi/index.php/RestAPI/GetCallStatus/ucid/" + ucid);
            response = httpUtils.doGet(manualDialCheckUrl + ucid).getResponseBody();
            log.debug("Response=" + response);
            JSONObject json = new JSONObject(response);
            String status = json.getString("status");
            JSONArray jSONArray = json.getJSONArray("data");
            JSONObject jsonData = jSONArray.getJSONObject(0);

            if (status.equalsIgnoreCase("0")) {// No Call Found Close the Call
                response = "Call Completed:" +jsonData.getString("CallStatus");
            } else {// 
                response = "Call in Progress:"+jsonData.getString("CallStatus");
            }
        } catch (Exception e) {
            response = "Error: " + e.getMessage();
        }

        log.debug(ucid + " Response=" + response);
        
        return response;
    }
    public Object getBean(String name) {

//        ApplicationContext ctx =
//                ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext ctx = AppContext.getApplicationContext();
        return ctx.getBean(name);
    }
}
