/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.google.gson.JsonObject;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.service.CallbacksExecutorService;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.util.HttpUtils;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class CallbacksExecutorServiceImpl implements CallbacksExecutorService {

    @Override
    public HttpResponseDetails sendCallbackDetails(String url, String ucid, boolean dispositionSet,String disp,String comment) {

        HttpResponseDetails httpResponse = null;
//        CustomerCallback customerCallback = new CustomerCallback();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("UCID", ucid);
//            log.info("In execute call back UCID :" + customerCallback.getUcid());
        String query = "{CALL Rep_CDR_MonitDetails(?)}";
        List report = reportManager.executeProcedure(query, queryParams);
        final JsonObject json = new JsonObject();
        Map<String, Object> record;
        String queryString = "";

        if (CollectionUtils.isNotEmpty(report)) {

            record = (Map) report.get(0);
            log.info("Got call details for ucid:" + ucid + "=> " + record);
            Set<String> keys = record.keySet();
            for (String key : keys) {
                json.addProperty(key, record.get(key) == null ? null : record.get(key).toString());
            }
            
            json.addProperty("dispositionSet", dispositionSet);
            if(dispositionSet){
                json.addProperty("Disposition", disp);
                json.addProperty("Comments", comment);
            }
            
            try {
                queryString = String.format("data=%s", URLEncoder.encode(json.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                log.error(keys, ex);
            }
            try {
//                httpResponse = HttpUtils.sendHttpPOSTRequest(url, queryString,"nandini@urbanladder.com","test123");
                httpResponse = HttpUtils.sendHttpPOSTRequest(url, queryString);
                log.info(">>><<URL:" + url + "| data:" + queryString + "| Response:" + httpResponse);
            } catch (ConnectException ex) {
                log.error("UCID:" + ucid + "|" + ex.getMessage(), ex);
            } catch (Exception ex) {
                log.error("UCID:" + ucid + "|" + ex.getMessage(), ex);
            }


        } else {
            log.error("UCID:" + ucid + "|" + "No report found");
        }

        return httpResponse;


    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setHttpUtils(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
    }
    private ReportManager reportManager;
    private HttpUtils httpUtils;
    private int interval;
    private static Logger log = Logger.getLogger(CallbacksExecutorServiceImpl.class);
}
