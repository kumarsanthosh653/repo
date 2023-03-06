/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.UserIntegration;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.util.HttpUtils;
import com.zoho.crm.ctiapisdk.util.CtiApiUtil;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

/**
 *
 * @author rajesh
 */
public class PluginManager implements Command {

    private HttpServletRequest request;
    private String monitorUcid;
    private String ucid;
    private String callType;
    private String agentNumber;
    private String did;
    private String custNumber;
    private String audioFile;
    private String action;
    private String callStatus;
    private String apiKey;
    private String causername;
    private Boolean isCompleted;
    private Boolean callCompleted;
    private Date sTime;
    private Date eTime;
    private String callBackParam;
    private String pluginUrl;
    private UserIntegration userIntegration;
    private static Logger log = Logger.getLogger(PluginManager.class);
    private int recordingduration;
    private String customerStatus;
    private String agentStatus;
    private RedisAgentManager redisAgentManager;
    private String caAgentId;
    private boolean isCampaignOffline;
    private String fallbackRule;

//    public PluginManager(UserIntegration userIntegration, String pluginUrl, String pluginName, HttpServletRequest request) {
//        this.pluginName = pluginName;
//        this.pluginUrl = pluginUrl;
//        this.request = request;
//        this.userIntegration = userIntegration;
//    }
    public PluginManager(UserIntegration userIntegration, RedisAgentManager redisAgentManager, String pluginUrl, String monitorUcid, String ucid, String callType, String agentNumber, String did, String custNumber, String audioFile, String action, String callStatus, String apiKey, String causername, String caAgentId, Boolean isCompleted, Boolean callCompleted, Date sTime, Date eTime, String uui, String customerStatus, String agentStatus, boolean isCampaignOffline, String fallbackRule) {
        this.userIntegration = userIntegration;
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.callType = callType;
        this.agentNumber = agentNumber;
        this.did = did;
        this.custNumber = custNumber;
        this.audioFile = audioFile;
        this.action = action;
        this.callStatus = callStatus;
        this.apiKey = apiKey;
        this.causername = causername;
        this.isCompleted = isCompleted;
        this.callCompleted = callCompleted;
        this.sTime = sTime;
        this.eTime = eTime;
        this.callBackParam = uui;
        this.pluginUrl = pluginUrl;
        this.customerStatus = customerStatus;
        this.agentStatus = agentStatus;
        this.caAgentId = caAgentId;
        this.redisAgentManager = redisAgentManager;
        this.isCampaignOffline = isCampaignOffline;
        this.fallbackRule = fallbackRule;
    }

    @Override
    public String execute() {

        if (sTime != null && eTime != null) {
            this.recordingduration = (int) TimeUnit.MILLISECONDS.toSeconds(eTime.getTime() - sTime.getTime());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                String token_ctikey = "";
                String method = "";
                if (!action.isEmpty()) {
                    if (action.equalsIgnoreCase("updateCallStatus") && !isCompleted && callType.equalsIgnoreCase("inbound")) {
                        method = "callReceived";
                    } else if (action.equalsIgnoreCase("updateCallStatus") && !isCompleted && !callType.equalsIgnoreCase("inbound")) {
                        method = "callDialed";
                    } else if (action.equalsIgnoreCase("busyAgent")) {
                        method = "callAnswered";

                    } else if (action.equalsIgnoreCase("updateCallStatus") && isCompleted && !callStatus.equalsIgnoreCase("fail")) {
                        method = "callHungup";
                    } else if (action.equalsIgnoreCase("updateCallStatus") && callCompleted && callStatus.equalsIgnoreCase("fail")) {
                        method = "callMissed";
//              
                    }

                    try {
                        URIBuilder urib = new URIBuilder(pluginUrl);
                        urib.setParameter("agentMonitorUcid", monitorUcid)
                                .setParameter("method", method)
                                .setParameter("plugin", userIntegration.getIntegration().getName())//screenpop url contains plugin name
                                .setParameter("ucid", ucid)
                                .setParameter("callType", callType)
                                .setParameter("agentPhoneNumber", agentNumber)
                                .setParameter("did", did)
                                .setParameter("callerId", custNumber)
                                .setParameter("audioFile", audioFile)
                                .setParameter("action", "updateCallStatus")
                                .setParameter("isCompleted", "" + isCompleted)
                                .setParameter("callCompleted", "" + callCompleted)
                                .setParameter("callStatus", callStatus) //.setParameter(dataId, type)
                                .setParameter("authType", userIntegration.getIntegration().getAuthType())
                                .setParameter("clientId", userIntegration.getIntegration().getClientId())
                                .setParameter("clientSecret", userIntegration.getIntegration().getClientSecret())
                                .setParameter("redirect_uri", userIntegration.getIntegration().getRedirectUrl())
                                .setParameter("refresh_token", userIntegration.getRefreshToken())
                                .setParameter("token_url", userIntegration.getIntegration().getAuthUrl())
                                .setParameter("stime", DateUtil.convertDateToString(sTime, "yyyy-MM-dd HH:mm:ss"))
                                .setParameter("etime", DateUtil.convertDateToString(eTime, "yyyy-MM-dd HH:mm:ss"))
                                .setParameter("customerStatus", customerStatus)
                                .setParameter("agentStatus", agentStatus)
                                .setParameter("callBackParam", callBackParam)
                                .setParameter("apiKey", apiKey)
                                .setParameter("username", causername)
                                .setParameter("authtoken", userIntegration.getAuthToken())//Zoho CRM
                                .setParameter("ctikey", userIntegration.getAuthCode())// Zoho CRM
                                .setParameter("callrefid", CtiApiUtil.getCallRefId())
                                .setParameter("caAgentId", "" + caAgentId)
                                .setParameter("recordingduration", "" + recordingduration)
                                .setParameter("location", userIntegration.getLocation())
//                                .setParameter("redisHost", redisAgentManager.getCurrentRedisHost())
//                                .setParameter("redisDb", "" + redisAgentManager.getCurrentRedisDb())
                                ;
                        if (isCampaignOffline) {
                            urib.setParameter("zohoUserId", redisAgentManager.hget("ca:integration:" + userIntegration.getIntegration().getName() + ":fwpnumber:" + causername, caAgentId));
                        } else if (StringUtils.equalsIgnoreCase(fallbackRule, "queue") && callCompleted && StringUtils.equalsIgnoreCase(callStatus, "fail")) {
                            log.debug("getting zoho user id from redis...." + redisAgentManager.hget("ca:integration:" + userIntegration.getIntegration().getName() + ":did:" + userIntegration.getUserId(), did));
                            urib.setParameter("zohoUserId", redisAgentManager.hget("ca:integration:" + userIntegration.getIntegration().getName() + ":did:" + userIntegration.getUserId(), did));
                        }  else if (StringUtils.equalsIgnoreCase(fallbackRule, "VoiceMail") && callCompleted && StringUtils.equalsIgnoreCase(callStatus, "fail")) {
                            log.debug("getting zoho user id from redis...." + redisAgentManager.hget("ca:integration:" + userIntegration.getIntegration().getName() + ":did:" + userIntegration.getUserId(), did));
                            urib.setParameter("zohoUserId", redisAgentManager.hget("ca:integration:" + userIntegration.getIntegration().getName() + ":did:" + userIntegration.getUserId(), did));
                        } else if (StringUtils.equalsIgnoreCase(userIntegration.getIntegration().getName(), "zohodesk")) {
                            urib.setParameter("zohoUserId", redisAgentManager.hget(Constants.REDIS_AGENT_ZOHO_USER_MAPPING, causername + ":" + caAgentId));
                        } else if (StringUtils.equalsIgnoreCase(userIntegration.getIntegration().getName(), "zohocrmv2")) {
                            log.debug("getting zoho user id from redis...." + redisAgentManager.hget("ca:agent:zoho-crmv2-user:mapping", causername + ":" + caAgentId));
                            urib.setParameter("zohoUserId", redisAgentManager.hget("ca:agent:zoho-crmv2-user:mapping", causername + ":" + caAgentId));
                        } else if (StringUtils.equalsIgnoreCase(userIntegration.getIntegration().getName(), "zoho")) {
                            log.debug("getting zoho user id from redis...." + redisAgentManager.hget("ca:agent:zoho-crmv3-user:mapping", causername + ":" + caAgentId));
                            urib.setParameter("zohoUserId", redisAgentManager.hget("ca:agent:zoho-crmv3-user:mapping", causername + ":" + caAgentId));
                        } else if (StringUtils.equalsIgnoreCase(userIntegration.getIntegration().getName(), "ZohoDesk_Europe")) {
                            urib.setParameter("zohoUserId", redisAgentManager.hget(Constants.REDIS_AGENT_ZOHO_EUROPE_USER_MAPPING, causername + ":" + caAgentId));
                        } else if (StringUtils.equalsIgnoreCase(userIntegration.getIntegration().getName(), "ZohoCrmV2_Europe")) {
                            urib.setParameter("zohoUserId", redisAgentManager.hget(Constants.REDIS_AGENT_ZOHO_EUROPE_CRMV2_USER_MAPPING, causername + ":" + caAgentId));
                        }
                        log.debug("" + fallbackRule + " | " + callCompleted + " | " + callStatus);
                        log.debug("😑 🔨  --->     Going to hit:" + urib.build().toString());
                        HttpResponseDetails httpResponseDetails = HttpUtils.doGet(urib.build().toString());
                        log.debug(urib.build().toString() + " -> Response:" + httpResponseDetails);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

            }
        }).start();
        return "Success";
    }

}
