/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.google.gson.JsonObject;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.CallConfDetail;
import com.ozonetel.occ.model.CallHoldDetail;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.service.AgentManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.jwebsocket.token.Token;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.HoldDetailManager;
import com.ozonetel.occ.util.AppContext;
//import com.ozonetel.occ.util.PhoneNumberUtil;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.params.CoreConnectionPNames;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

/**
 *
 * @author rajeshchary
 */
public class HoldManager implements Command {

    private WebSocketServerTokenEvent aEvent;
    private Token aToken;
    private String monitorUcid;
    private String phoneNumber;
    private String holdAction;
    private String agentId;
    private String username;
    private String callerId;
    private String did;
    private int confType;
    private GenericManager<CallConfDetail, Long> callConfDetailManager;
    Log log = LogFactory.getLog(getClass());
    private FwpNumber fwp;
    private Agent conferencedAgent;
    AgentManager agentManager;
    private CallConfDetail confDetail;
    private String ucid;
    private String holdNumber;
    private Long agentUniqueId;

    public HoldManager(WebSocketServerTokenEvent aEvent, Token aToken) {
        this.aEvent = aEvent;
        this.aToken = aToken;
        this.holdAction = aToken.getString("holdAction");
        this.ucid = aToken.getString("ucid");//now this is monitorUcid
        this.monitorUcid = aToken.getString("monitorUcid");// this is the actual Ucid these need to be Changed in future
//        this.ucid = aToken.getString("ucid");
        log.debug("PhoneNumber=" + aToken.getString("phoneNumber"));
        this.phoneNumber = aToken.getString("phoneNumber");
        this.agentId = aToken.getString("agentId");
        this.username = aToken.getString("customer");
        this.did = aToken.getString("did");
        this.callerId = aToken.getString("callerId");
        try {
            this.confType = Integer.parseInt(aToken.getString("confType"));
        } catch (Exception ignore) {
        }

    }

    public HoldManager(Map mp) {
        this.holdAction = mp.get("holdAction").toString();
        this.monitorUcid = mp.get("monitorUcid").toString();
        this.ucid = mp.get("ucid").toString();
        this.monitorUcid = mp.get("monitorUcid").toString();
        this.phoneNumber = mp.get("phoneNumber").toString();
        this.agentId = mp.get("agentId").toString();
        this.username = mp.get("username").toString();
        this.did = mp.get("did").toString();
        this.agentUniqueId = new Long(mp.get("agentUniqueId").toString());

        try {
            this.holdNumber = mp.get("holdNumber").toString();
        } catch (Exception ignore) {
        }
        try {
            this.confType = Integer.parseInt(mp.get("confType").toString());
        } catch (Exception ignore) {
        }
    }

    public String execute() {
        if (holdAction.equalsIgnoreCase("CALL_HOLD")) {
            return hold();
        } else if (holdAction.equalsIgnoreCase("CALL_UNHOLD")) {
            return unhold();
        } else if (holdAction.equalsIgnoreCase("CONFERENCE") || holdAction.equalsIgnoreCase("CONFERENCE_HOLD")) {
            return conference();
        } else if (holdAction.equalsIgnoreCase("KICK_CALL")) {
            return kickCall();
        } else if (holdAction.equalsIgnoreCase("CALL_MUTE")) {
            return mute();
        } else if (holdAction.equalsIgnoreCase("CALL_UNMUTE")) {
            return unMute();
        } else if (holdAction.equalsIgnoreCase("DROP_HOLD")) {
            return dropHold();
        } else {
            System.out.println("Invalid Params..");
            return "Invalid Params..";
        }
    }

    public String hold() {
        String status = "fail";
        status = SendRequest("CALL_HOLD");
        if (status.equalsIgnoreCase("success")) {
            HoldDetailManager holdDetailManager = (HoldDetailManager) getBean("holdDetailManager");
            log.debug("Hold Details Manager=" + holdDetailManager);
            CallHoldDetail callHoldDetail = new CallHoldDetail();
            callHoldDetail.setStartTime(new Date());
//            callHoldDetail.setUcid(new BigInteger(ucid));
            callHoldDetail.setMonitorUcid(new BigInteger(monitorUcid));
            callHoldDetail.setCallerNumber(phoneNumber);
            if (!did.isEmpty() && did != null) {
                callHoldDetail.setDid(new Long(did));
            }
            agentManager = (AgentManager) getBean("agentManager");
            Agent a = null;
            if (agentUniqueId != null) {
                a = agentManager.get(agentUniqueId);
            } else {
                a = agentManager.getAgentByAgentIdV2(username, agentId);
            }
            callHoldDetail.setAgent(a);
            holdDetailManager.save(callHoldDetail);
//            agentManager.updateHoldStartTime(a.getId(), true);
        }
        return status;
    }

    public String unhold() {
        String status = "fail";
        status = SendRequest("CALL_UNHOLD");
        if (status.equalsIgnoreCase("success")) {
            HoldDetailManager holdDetailManager = (HoldDetailManager) getBean("holdDetailManager");
            log.debug("Hold Details Manager=" + holdDetailManager);

            CallHoldDetail callHoldDetail = holdDetailManager.getCallHoldDetail(new BigInteger(monitorUcid), phoneNumber);

            if (callHoldDetail != null) {
                callHoldDetail.setEndTime(new Date());
                holdDetailManager.save(callHoldDetail);
            }
//            agentManager.updateHoldStartTime(agentUniqueId, false);
        }
        return status;
    }

    public String dropHold() {
        String status = "fail";
        status = SendRequest("DROP_HOLD");
        return status;
    }

    public String mute() {
        String status = "fail";
        System.out.println("Calling Mute for " + phoneNumber);
        status = SendRequest("CALL_MUTE");
        return status;
    }

    public String unMute() {
        String status = "fail";
        status = SendRequest("CALL_UNMUTE");
        return status;
    }

    public String kickCall() {
        return SendRequest("KICK_CALL");
    }

    public String conference() {
        JsonObject jsonObject = new JsonObject();
        confDetail = new CallConfDetail();
        callConfDetailManager = (GenericManager<CallConfDetail, Long>) getBean("callConfDetailManager");
        //FIXME don't need to save here, please check this.(Unnecessary db operation)
        confDetail = callConfDetailManager.save(confDetail);

        String phone = phoneNumber;
        agentManager = (AgentManager) getBean("agentManager");

//        if (StringUtils.endsWithIgnoreCase(action, "CONFERENCE")) {
        if (confType == 1) {
            log.trace("Agent conference for :" + phoneNumber);

            conferencedAgent = agentManager.getAgentByAgentIdV2(username, phoneNumber);

            if (conferencedAgent.getState() == Agent.State.IDLE) {
                phone = conferencedAgent.getFwpNumber().getPhoneNumber();
//                conferencedAgent.setNextFlag(1L);
//                agentManager.save(conferencedAgent);
            } else {
                log.info("Can't get agent [" + conferencedAgent + "] to conference, state : " + conferencedAgent.getState());
                return "fail";
            }

        } else if (confType == 3) {
            log.trace("FWP conference for :" + phoneNumber);
            String[] params = StringUtils.splitPreserveAllTokens(phoneNumber, "~");
            if (params != null && params.length > 1) {
                phone = params[1];
                log.trace("FWP number & name" + phone + " | " + params[0] + " & user =" + username);
                fwp = ((FwpNumberManager) getBean("fwpNumberManager")).getFwpNumberByName(params[0], username);
                log.trace("Get fwp phone as:" + fwp);

            } else {

                phone = phoneNumber;
            }
        } else {
            phone = phoneNumber;
            log.trace("Direct number ...:" + phoneNumber);
        }
        log.info("Final conference number:" + phone);
        phoneNumber = phone;

        String status = SendRequest("CONFERENCE");

        switch (confType) {
            case 1:
                confDetail.setAgentParticipant(conferencedAgent);
                break;
            case 2:
                confDetail.setPhoneParticipant(fwp);
                break;
        }
        confDetail.setOtherParticipant(phone);

        confDetail.setStartTime(new Date());
        jsonObject.addProperty("status", status);
        if (status.equalsIgnoreCase("success")) {
            // ----- > used to format <code>phone</code> using goole phone lib
            jsonObject.addProperty("phone", phone);
            new Thread(
                    new Runnable() {

                public void run() {

                    confDetail.setUcid(new BigInteger(ucid));
                    confDetail.setDid(did);
                    Agent a = null;
                    if (agentUniqueId != null) {
                        a = agentManager.get(agentUniqueId);
                    } else {
                        a = agentManager.getAgentByAgentIdV2(username, agentId);
                    }
                    confDetail.setConfCreator(a);
                    confDetail.setDialStatus("success");
                    confDetail.setPickUpTime(new Date());
                    CallConfDetail conf = callConfDetailManager.save(confDetail);
                    log.trace("Saved confDetail:" + conf);
                }
            }).start();
        } else if (conferencedAgent != null) {
            conferencedAgent.setNextFlag(0L);
            agentManager.save(conferencedAgent);
        }

        log.info("JSON response:" + jsonObject.toString());
        return jsonObject.toString();
    }

    public String SendRequest(final String action) {
//        http://172.16.15.34/cgi-bin/kookoo_tele_api.cgi?action={action type}&ucid={ucid of call }&phoneno={number which to hold} 
        HttpClientParams hmp = new HttpClientParams();
        HttpClient msuClient = new HttpClient();

        AppProperty appProperty = (AppProperty) getBean("appProperty");
        StringBuffer kUrl = new StringBuffer(appProperty.getKookooTeleApiUrl().toString());

        kUrl.append("?action=").append(holdAction).append("&ucid=").append(monitorUcid);

        if (StringUtils.endsWithIgnoreCase(action, "CONFERENCE")) {
            StringBuilder cburl = new StringBuilder(((AppProperty) getBean("appProperty")).getKooKooCallBackUrl());
            // ----- > used to format <code>phoneNumber</code> using goole phone lib
            cburl.append("confCallback.html").append("?ucid=").append(ucid).append("&agentId=").append(agentId).append("&id=").append(confDetail.getId()).append("&phoneNumber=").append(phoneNumber).append("&user=").append(username);
            if (conferencedAgent != null) {
                cburl.append("&confAgentId=").append(conferencedAgent.getAgentId());
            }
            try {
//                kUrl.append("&phoneno=").append(URLEncoder.encode(PhoneNumberUtil.getNationalNumberForAgent(phoneNumber), "UTF-8"));
                kUrl.append("&phoneno=").append(URLEncoder.encode(phoneNumber, "UTF-8"));
                kUrl.append("&confType=").append(confType);
                kUrl.append("&cburl=").append(URLEncoder.encode(cburl.toString(), "UTF-8"));
                kUrl.append("&holdNumber=").append(holdNumber);
            } catch (Exception e) {
            }
        } else {
            try {
                kUrl.append("&phoneno=").append(URLEncoder.encode(phoneNumber, "UTF-8"));
            } catch (Exception e) {
                kUrl.append("&phoneno=").append(phoneNumber);
            }
        }

        HttpMethod method = new GetMethod(kUrl.toString());
        hmp.setParameter("action", action);
        hmp.setParameter("ucid", monitorUcid);
        hmp.setParameter("phoneno", phoneNumber);
        log.trace("final url" + kUrl);
        msuClient.setParams(hmp);
        String message = "";
        final String status;
        try {
            // this one causes a timeout if a connection is established but there is 
// no response within 5 mins
            msuClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5 * 60 * 1000);

// this one causes a timeout if no connection is established within 5 mins
            msuClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 60 * 1000);
            int code = msuClient.executeMethod(method);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = (Document) docBuilder.parse(method.getResponseBodyAsStream());
            status = doc.getElementsByTagName("status").item(0).getTextContent();
            message = doc.getElementsByTagName("message").item(0).getTextContent();
            log.trace("Code" + code + "   status=" + status);
            log.trace("message=" + message);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        if (status.equalsIgnoreCase("success")) {
            return "success";
        } else {
            if (StringUtils.endsWithIgnoreCase(action, "CONFERENCE")) {
                callConfDetailManager.remove(confDetail.getId());
            }
            return message;
        }
    }

    public Object getBean(String name) {
        ApplicationContext ctx = AppContext.getApplicationContext();
        return ctx.getBean(name);
    }
}
