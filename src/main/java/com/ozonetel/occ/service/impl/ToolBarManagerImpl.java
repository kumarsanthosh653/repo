package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.*;
import com.ozonetel.occ.model.Agent.State;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.MailEngine;
import com.ozonetel.occ.service.OCCManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.SMSManager;
import com.ozonetel.occ.service.SMSTemplateManager;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.ToolBarManager;
import com.ozonetel.occ.service.chat.impl.ChatServiceImpl;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.userManager;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.util.TimeConverter;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.mail.MessagingException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author root
 */
public class ToolBarManagerImpl extends OCCManagerImpl implements ToolBarManager {
    
    
    private static Logger logger = Logger.getLogger(ToolBarManagerImpl.class);

    @Override
    public void initialize() {

//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        eventManager = (EventManager) webApplicationContext.getBean("eventManager");
        dispositionManager = (DispositionManager) webApplicationContext.getBean("dispositionManager");
        campaignManager = (CampaignManager) webApplicationContext.getBean("campaignManager");
        occManager = (OCCManager) webApplicationContext.getBean("occManager");
        reportManager = (ReportManager) webApplicationContext.getBean("reportManager");
        fwpNumberManager = (FwpNumberManager) webApplicationContext.getBean("fwpNumberManager");
        previewDataManager = (PreviewDataManager) webApplicationContext.getBean("previewDataManager");
        callBackManager = (CallBackManager) webApplicationContext.getBean("callBackManager");
        previewExtraDataManager = (GenericManager<PreviewExtraData, Long>) webApplicationContext.getBean("previewExtraDataManager");
        sMSManager = (SMSManager) webApplicationContext.getBean("sMSManager");
        sMSTemplateManager = (SMSTemplateManager) webApplicationContext.getBean("sMSTemplateManager");
        chatService = (ChatServiceImpl) webApplicationContext.getBean("chatService");
        redisReportManager = (RedisManager<Report>) webApplicationContext.getBean("redisReportManager");
        tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");;
        telephonyManager = (TelephonyManager) webApplicationContext.getBean("telephonyManager");
        redisAgentManager = (RedisAgentManager) webApplicationContext.getBean("redisAgentManager");
//        beanstalkService = (BeanstalkService) webApplicationContext.getBean("beanstalkService");
    }

    public String openTicket(Long ticketID, String username, String agent_id, String callerID, String desc, Long ucid, Long monitorUCID, String comment) {
        log.info("In opening ticket");
        String details = null;
        try {
            String rawData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("openTicket", "UTF-8");
            rawData += "&" + URLEncoder.encode("ticketID", "UTF-8") + "=" + URLEncoder.encode("" + ticketID, "UTF-8");
            rawData += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            rawData += "&" + URLEncoder.encode("agent_id", "UTF-8") + "=" + URLEncoder.encode(agent_id, "UTF-8");
            rawData += "&" + URLEncoder.encode("callerID", "UTF-8") + "=" + URLEncoder.encode(callerID, "UTF-8");
            rawData += "&" + URLEncoder.encode("ucid", "UTF-8") + "=" + URLEncoder.encode("" + ucid, "UTF-8");
            rawData += "&" + URLEncoder.encode("monitorUCID", "UTF-8") + "=" + URLEncoder.encode("" + monitorUCID, "UTF-8");
            rawData += "&" + URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode("" + comment, "UTF-8");
            rawData += "&" + URLEncoder.encode("desc", "UTF-8") + "=" + URLEncoder.encode(desc, "UTF-8");
            String response = getResponse(ticketSystemURL, rawData);

            log.info("Ticket sending :" + response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return details;
    }

    public String updateTicket(Long ticketID, String status, String username, Long ucid, Long monitorUCID, String comment, String agent_id, String callerID) {
        log.info("In closing ticket");
        String details = null;
        try {
            String rawData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("updateTicket", "UTF-8");
            rawData += "&" + URLEncoder.encode("ticketID", "UTF-8") + "=" + URLEncoder.encode("" + ticketID, "UTF-8");
            rawData += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            rawData += "&" + URLEncoder.encode("agent_id", "UTF-8") + "=" + URLEncoder.encode(agent_id, "UTF-8");
            rawData += "&" + URLEncoder.encode("callerID", "UTF-8") + "=" + URLEncoder.encode(callerID, "UTF-8");
            rawData += "&" + URLEncoder.encode("ucid", "UTF-8") + "=" + URLEncoder.encode("" + ucid, "UTF-8");
            rawData += "&" + URLEncoder.encode("monitorUCID", "UTF-8") + "=" + URLEncoder.encode("" + monitorUCID, "UTF-8");
            rawData += "&" + URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode("" + comment, "UTF-8");
            rawData += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");
            String response = getResponse(ticketSystemURL, rawData);

            log.info("Ticket updating :" + response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return details;
    }

    public Long getAgentID(String user, String agentID) {
        Agent agent = agentManager.getAgentByAgentIdV2(user, agentID);
        if (agent != null) {
            return agent.getId();
        } else {
            return null;
        }
    }

    @Override
    public JSONTicketDetails getTicketDetails(Long ticketId, String user) {
        JSONTicketDetails jSONTicketDetails = null;

        try {
            String rawData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("getTicket", "UTF-8");
            rawData += "&" + URLEncoder.encode("ticketID", "UTF-8") + "=" + URLEncoder.encode("" + ticketId, "UTF-8");
            rawData += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("" + user, "UTF-8");
            String output = getResponse(ticketSystemURL, rawData);

            Gson gson = new GsonBuilder().serializeNulls().create();
//            log.info("got ticket details as :" + output);
            if (StringUtils.isNotEmpty(output)) {
                MiniTicket callDetails = gson.fromJson(output, MiniTicket.class);
                jSONTicketDetails = new JSONTicketDetails();
                jSONTicketDetails.setStatus(callDetails.getStatus());
                jSONTicketDetails.setDesc(callDetails.getTktDesc());

                if (CollectionUtils.isNotEmpty(callDetails.getTicketDetails())) {

                    jSONTicketDetails.setDetails(new TreeSet(callDetails.getTicketDetails()));
                }
                // details = gson.toJson(jSONTicketDetails);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return jSONTicketDetails;
    }

    public String getTicketDetailByPhone(String user, String phoneNumber) {
        String details = "";
        try {
            String rawData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("getTicketsByPhone", "UTF-8");
            rawData += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("" + user, "UTF-8");
            rawData += "&" + URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode("" + phoneNumber, "UTF-8");
            String output = getResponse(ticketSystemURL, rawData);
            log.info("Get the tickets by phone (" + phoneNumber + " ):" + output);
            if (StringUtils.isNotEmpty(output)) {
                //Type collectionType = new TypeToken<List<MiniTicket>>() {
                //}.getType();
                details = output;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return details;
    }

    public boolean sendTicketDetails(Long refID, Long ucid, String status, Long userID, Date startDate,
            Date updateDate, Date closeDate, Long agentID, String callerID, String comment, String disposition, String skillName,
            Long campaignID) {
        boolean success = false;
        log.info("Sending ticket details.....");
        try {
            String rawData = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("save", "UTF-8");
            rawData += "&" + URLEncoder.encode("referenceID", "UTF-8") + "=" + URLEncoder.encode("" + refID, "UTF-8");
            rawData += "&" + URLEncoder.encode("ucid", "UTF-8") + "=" + URLEncoder.encode("" + ucid, "UTF-8");
            rawData += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode("" + status, "UTF-8");
            rawData += "&" + URLEncoder.encode("user.id", "UTF-8") + "=" + URLEncoder.encode("" + userID, "UTF-8");
//            rawData += "&" + URLEncoder.encode("startDate", "UTF-8") + "=" + URLEncoder.encode("" + startDate, "UTF-8");
//            rawData += "&" + URLEncoder.encode("updateDate", "UTF-8") + "=" + URLEncoder.encode("" + updateDate, "UTF-8");
//            rawData += "&" + URLEncoder.encode("closeDate", "UTF-8") + "=" + URLEncoder.encode("" + closeDate, "UTF-8");
            rawData += "&" + URLEncoder.encode("agentId", "UTF-8") + "=" + URLEncoder.encode("" + agentID, "UTF-8");
            rawData += "&" + URLEncoder.encode("callerID", "UTF-8") + "=" + URLEncoder.encode("" + callerID, "UTF-8");
            rawData += "&" + URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode("" + comment, "UTF-8");
            rawData += "&" + URLEncoder.encode("disposition", "UTF-8") + "=" + URLEncoder.encode("" + disposition, "UTF-8");
            rawData += "&" + URLEncoder.encode("skillName", "UTF-8") + "=" + URLEncoder.encode("" + skillName, "UTF-8");
            rawData += "&" + URLEncoder.encode("campaignId", "UTF-8") + "=" + URLEncoder.encode("" + campaignID, "UTF-8");
            String response = getResponse(ticketSystemURL, rawData);

            log.info("Ticket saving :" + response);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return success;
    }

    private String getResponse(URL url, String rawData) {
        String output = "";
        log.info("Request url:" + url);
        log.info("POST data:" + rawData);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String type = "application/x-www-form-urlencoded";
            String agent = "Mozilla/6.0";
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", agent);
            connection.setRequestProperty("Content-Type", type);
            connection.setRequestProperty("Content-Length", "" + rawData.length());
//            connection.connect();

            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(rawData);
            wr.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                output += line;
            }

            wr.close();
            in.close();

            connection.disconnect();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (StringUtils.isNotEmpty(output)) {
            output = output.replaceAll("&#034;", "\"");
        }
        return output;

    }

    //For WebJSocket
    @Override
    public JsonObject tbAgentLogin(String username, String agentId, Long agentUniqId, String phoneNumber, String sessionId, String usId, boolean reconnect, Event.AgentMode mode, String agentLoginIp) {
        JsonObject loginResponse = new JsonObject();
        agentManager.syncToLdb(agentUniqId);
        Agent a = agentManager.get(agentUniqId);

        if (null != a && a.isLoggedIn()) {

            if (!reconnect && a.getClientId() != null) {
                occManager.logoffAgentByAdmin(username, agentUniqId, "admin");
            }

            FwpNumber fwp = fwpNumberManager.getFwpNumberByPhone(phoneNumber, a.getUserId());
            //Checking FWP Number is logged in with other Agent or not to avoid concurrent logins with same FWP Number

            /*       log.debug("FWP Number logged in with Agent :  " + fwp.getAgent());
//            if (fwp.getAgent() != null && !fwp.getAgent().equals(a.getId())) {
            /* if (fwp.getAgent() != null) {
                log.debug("Error: AgentId [" + fwp.getAgent() + "] already logged in with FWPNumber[" + fwp.getPhoneName() + "]");
                loginResponse.addProperty("status", "Error");
                loginResponse.addProperty("message", "AgentId [" + agentId + "] already logged in with FWPNumber[" + fwp.getPhoneNumber() + "]");
                return loginResponse;
            }
            log.debug("FWP Number is free, Hence allowed for login");
             */
            //log.debug("Mean while disablig the Concurrent Login Check functionality");
            fwp.setAgent(a.getId());
            fwp.setLastSelected(System.currentTimeMillis());
            fwp = fwpNumberManager.save(fwp);
            loginResponse.addProperty("isSip", fwp.isSip());
            agentManager.saveAgentLogin(a.getId(), reconnect, phoneNumber, fwp, usId);

            log.info(">>>>>Going to save in redis...");

            try {
                log.info("Saved agent");
                redisAgentManager.addToSet(username + ":mode:" + a.getMode(), a.getAgentId());
                log.info("Added to mode set");
                redisAgentManager.addToSet(username + ":state:" + Agent.State.AUX.toString(), a.getAgentId());
                log.info("Added to state set");
                redisAgentManager.addToSet(username + ":loggedin", a.getAgentId());
                log.info("Added to logged in set");
                redisAgentManager.zadd(username + ":agent:scores", System.currentTimeMillis() * a.getPriority(), a.getAgentId());
                redisAgentManager.saveAgentWsIdToRedis(username, a.getAgentId(), usId);
                redisAgentManager.hset(RedisKeys.AGENT_SESSIONID_AGENTID_MAP, sessionId, username + ":" + a.getAgentId());
                redisAgentManager.hset(RedisKeys.AGENTID_AGENT_SESSIONID_MAP, username + ":" + a.getAgentId(), sessionId);
            } catch (StackOverflowError error) {
                log.error(error.getMessage(), error);
            }

            if (!reconnect) {

                redisAgentManager.del("cachat:" + username + ":" + agentId + ":chat-sessions");
                Map<String, Object> loginEvent = new HashMap<>();
                loginEvent.put("type", "toolbarLogin");
                loginEvent.put("mode", Agent.Mode.valueOf(mode.toString()));
                loginEvent.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(new Date()));
                redisAgentManager.hset(username + ":agent:events", agentId, new Gson().toJson(loginEvent));

            }

            eventManager.logEvent(reconnect ? "reconnect" : "login", a.getUserId(), username, agentUniqId, agentId, Agent.Mode.INBOUND, new Date(), null, agentLoginIp, agentId);

            log.debug("Success : [" + username + "][" + agentId + "] is " + (reconnect ? "reconnected" : "logged") + " in with #" + phoneNumber);
            loginResponse.addProperty("status", "Success");
            loginResponse.addProperty("campaignTy2pe", "Campaign");
            loginResponse.addProperty("campaignScript", "Hello sir/Madam");
            loginResponse.addProperty("phoneNumber", phoneNumber);

            Set<String> roleSet = new HashSet<>();
            for (Role role : userManager.getUserRoles(username)) {
//                log.debug(role);
                roleSet.add(role.getName());
            }

            loginResponse.addProperty("outboundRole", (roleSet.contains(Constants.OUTBOUND_ROLE) ? 1 : 0));
            loginResponse.addProperty("callbackRole", (roleSet.contains(Constants.CALLBACKS_ROLE) ? 1 : 0));
            loginResponse.addProperty("blendedRole", (roleSet.contains(Constants.BLENDED_ROLE) ? 1 : 0));
            loginResponse.addProperty("agentCallHist", (roleSet.contains(Constants.AGENT_CALL_HISTORY_ROLE) ? 1 : 0));

            loginResponse.addProperty("tktCustomer", (roleSet.contains(Constants.TICKET_ROLE) ? 1 : 0));
            loginResponse.addProperty("hr", (roleSet.contains(Constants.HOLD_ROLE) ? 1 : 0));
            loginResponse.addProperty("cr", (roleSet.contains(Constants.CONFERENCE_ROLE) ? 1 : 0));
            loginResponse.addProperty("mr", (roleSet.contains(Constants.MUTE_ROLE) ? 1 : 0));
            loginResponse.addProperty("chtr", (roleSet.contains(Constants.CONSULTATIVEHOLDTRFR_ROLE) ? 1 : 0));

            boolean sendSMS = false;
            boolean pauseAlert = false;
            boolean maskCustomerNumber = false;
            boolean handleReconnect = false;
            boolean groupDisps = false;
            boolean forceRelease = false;
            boolean queueAlert = false;

            Map<String, Object> params = new LinkedHashMap();
            params.put("user_id", a.getUserId());
            params.put("isAdmin", 1);

            List paramList = campaignManager.executeProcedure("call Get_UserParamtersV2(?,?)", params);

            if (!paramList.isEmpty()) {

                Map item;
                for (Object param : paramList) {
                    item = (Map) param;

                    switch (item.get("ParameterCode").toString()) {
                        case "SEND_SMS":
                            sendSMS = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "PAUSE_EMAIL_ALERT":
                            pauseAlert = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "MASK_CUSTOMER_NUMBER":
                            maskCustomerNumber = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "HANDLE_RECONNECT":
                            handleReconnect = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "DISPOSITION_GROUPING":
                            groupDisps = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "FORCE_RELEASE_MYSELF":
                            forceRelease = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "QUEUE_ALERT":
                            queueAlert = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                    }

                }

            }

            if (!roleSet.contains(Constants.SMS_ROLE)) {
                sendSMS = false;
            }

            loginResponse.addProperty("smsRole", (sendSMS ? 1 : 0));
            loginResponse.addProperty("handleReconnect", (handleReconnect ? 1 : 0));
            loginResponse.addProperty("maskCustomernumber", (maskCustomerNumber ? 1 : 0));
            loginResponse.addProperty("groupDisps", (groupDisps ? 1 : 0));
            loginResponse.addProperty("forceRelease", (forceRelease ? 1 : 0));
            loginResponse.addProperty("queueAlert", (queueAlert ? 1 : 0));

            if (sendSMS) {
                loginResponse.addProperty("stemps", new GsonBuilder()
                        .setExclusionStrategies(new MyExclusionStrategy(User.class))
                        .serializeNulls()
                        .create().toJson(sMSTemplateManager.getSMSTemplatesByUser(username)));
            } else {
                loginResponse.addProperty("stemps", "[]");//emtpy array
            }

            System.out.println("Pause alert enabled..?" + pauseAlert);
            loginResponse.addProperty("pauseAlert", (pauseAlert ? 1 : 0));
            Map<String, Object> model = new LinkedHashMap<>();
            model.put("event", "login");
            pushToStream(username, agentId, model, System.currentTimeMillis());
            return loginResponse;
        } else {
            log.debug("Error: [" + username + "][" + agentId + "] Agent is NULL");
            loginResponse.addProperty("status", "Error");
            loginResponse.addProperty("message", "Agent is not Loggedin from Phone");
            return loginResponse;
        }
    }

    @Override
    public Map<String, String> tbAgentReconnect(String sessionId, String usId, String username, String agentId, Long agentUniqId, String phoneNumber, Event.AgentMode mode, String event, Long ucid) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("eventData", redisAgentManager.hget(username + ":agent:events", agentId));
        responseMap.put("status", "Success");

        Agent a = agentManager.get(agentUniqId);

        if ("reconnect".equalsIgnoreCase(event) || "refresh".equalsIgnoreCase(event)) {
            if (a.getClientId() != null && !StringUtils.equals(usId, a.getClientId())) {
                Token tokenResponse = TokenFactory.createToken();
                tokenResponse.setType("closeTab");
                //WebSocketConnector
                tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
            }

//            a = agentManager.save(a);
            agentManager.saveAgentReconnect(agentUniqId, usId);
            responseMap.put("mode", a.getMode().toString());
            long diff = System.currentTimeMillis() - a.getIdleTime();
            responseMap.put("idleTime", "" + (diff < 0 ? 0 : diff) / 1000);
            redisAgentManager.saveAgentWsIdToRedis(username, a.getAgentId(), usId);
            redisAgentManager.hset(RedisKeys.AGENT_SESSIONID_AGENTID_MAP, sessionId, username + ":" + a.getAgentId());
            redisAgentManager.hset(RedisKeys.AGENTID_AGENT_SESSIONID_MAP, username + ":" + a.getAgentId(), sessionId);
        }
        if (a.getState() == Agent.State.AUX && StringUtils.isNotBlank(a.getStateReason())) {
            //a.getStateReason() : "changeMode"''
            eventManager.logEvent("pause", a.getUserId(), username, agentUniqId, agentId, a.getMode(), new Date(), null, a.getStateReason(), event.toLowerCase());
        } else {

            Map lastToken = new Gson().fromJson(redisAgentManager.hget(username + ":agent:events", agentId), Map.class);
            log.debug("updating reconnect event for ucid : " + (ucid != null ? ucid : ""));
            eventManager.logEvent(a.getState() == Agent.State.BUSY ? lastToken.get("inchat") != null ? "Chat" : "incall" : ((a.getState() == Agent.State.IDLE && a.getNextFlag() == 1) ? "calling" : a.getState().toString()),
                    a.getUserId(), username, agentUniqId, agentId, a.getMode(), new Date(), ucid != null ? ucid : a.getUcid(), event.toLowerCase(), event.toLowerCase());
        }
        return responseMap;
    }

    public void pushToStream(String userId, String agentId, Map event, long timestamp) {
        log.debug("Pushing to stream : " + userId + " : " + agentId + " : " + event);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("agent", agentId);
        jsonObject.addProperty("ts", timestamp);
        jsonObject.addProperty("eventMap", new Gson().toJson(event));
        redisAgentManager.rpush("activity:stream:" + userId, jsonObject.toString());

        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.set(Calendar.HOUR_OF_DAY, 23);
        expireCalendar.set(Calendar.MINUTE, 59);
        expireCalendar.set(Calendar.SECOND, 59);
        long expireTime = expireCalendar.getTimeInMillis() / 1000;
        redisAgentManager.expireAt("activity:stream:" + userId, expireTime);
    }

    @Override
    public String tbAgentLogout(String username, String agentId, Long agentUniqId, String phoneNumber, Event.AgentMode mode, String logoffMessage) {
        Agent a = agentManager.get(agentUniqId);

        String clientId = a.getClientId();
//        log.debug("tbAgentLogout" + agentId + " is " + a);
        if (null != a && a.isLoggedIn()) {

            if (a.getUcid() != null) {
                List<Report> reports = reportManager.getReportByMonitorUcid(a.getUcid());
                if (reports.size() >= 1) {
                    StatusMessage statusMessage = telephonyManager.kickCall(username, agentId, "" + a.getUcid(), "" + a.getUcid(), reports.get(0).getDid(), a.getPhoneNumber());
                    log.debug("Kick status:" + a.getUcid() + " | " + statusMessage);
                }
            }

            FwpNumber fwp = a.getFwpNumber();

            if (fwp != null) {
                log.debug("FwpNumber Details : " + fwp + " and Agent Details : " + fwp.getAgent());

                fwp.setAgent(null);
                fwp.setContact("");
                fwp.setState(State.IDLE);
                fwp.setNextFlag(new Long(0));
                fwpNumberManager.save(fwp);
            }

            //FwpNumber fwp2 = a.getFwpNumber();
            //log.debug("After Saving the FWP number : " + fwp2 + " And agent Details: " + fwp2.getAgent());
            agentManager.saveAgentLogout(agentUniqId);
            try {
                chatService.checkIfAgentIsInChat(username, a.getAgentId(), a.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            chatService.deleteChatSessionCount(username, a.getAgentId());
            // ---> Remove agentsessionid<-->agentid map(two ways) from redis
            String agentSessionId = redisAgentManager.hget(RedisKeys.AGENTID_AGENT_SESSIONID_MAP, username + ":" + a.getAgentId());
            redisAgentManager.hdel(RedisKeys.AGENTID_AGENT_SESSIONID_MAP, username + ":" + a.getAgentId());
            log.debug("AGENT SESSION ID=" + agentSessionId);
            redisAgentManager.hdel(RedisKeys.AGENT_SESSIONID_AGENTID_MAP, agentSessionId);

            Map<String, Object> logoutEvent = new HashMap<>();
            logoutEvent.put("type", "toolbarLogout");
            logoutEvent.put("mode", a.getMode());
            logoutEvent.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(new Date()));
            redisAgentManager.hset(username + ":agent:events", agentId, new Gson().toJson(logoutEvent));

            log.info("@@@@@Removing from sets in logout action:");

//            agentManager.redisAgentLoggedOut(a);
            redisAgentManager.delAgentWsIdFromRedis(username, a.getAgentId(), clientId);
            eventManager.logEvent("logout", a.getUserId(), username, agentUniqId, agentId, a.getMode(), new Date(), null, logoffMessage, null);
            log.debug("Success : [" + username + "][" + agentId + "] is logged out with #" + phoneNumber);

            Map<String, Object> params = new LinkedHashMap();
            boolean alertEnabled = false;
            params.put("user_id", a.getUserId());
            params.put("isAdmin", 0);
            params.put("param_code", "PAUSE_EMAIL_ALERT");
            List alertParam = campaignManager.executeProcedure("call Get_UserParamterV2(?,?,?)", params);
            if (alertParam != null && !alertParam.isEmpty()) {
                alertEnabled = Boolean.valueOf(((Map<String, Object>) alertParam.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) alertParam.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) alertParam.get(0)).get("ParameterValue").toString());
                log.debug("alert params--->" + alertEnabled);
            }
            if (alertEnabled) {
                List<String> recipients = new ArrayList(2);
                User user = userManager.getUser(a.getUserId().toString());
                recipients.add(user.getEmail());
                if (!StringUtils.isEmpty(a.getEmail())) {
                    recipients.add(user.getEmail());
                }
                Map<String, Object> model = new LinkedHashMap<>();
                model.put("agent", a.getAgentName());
                model.put("phoneNumber", fwp.getPhoneNumber());
                model.put("userMail", user.getEmail());
                model.put("agentMail", a.getEmail());
                model.put("alertType", "mail");
                model.put("time", System.currentTimeMillis());

                redisAgentManager.rpush("alert:event", new Gson().toJson(model));
                log.info("saving in redis--" + new Gson().toJson(model));
            }
            params.clear();
            params.put("event", "logout");
            pushToStream(username, a.getAgentId(), params, System.currentTimeMillis());
            return "Success";

        } else {
            log.debug("Error: [" + username + "][" + a.getAgentId() + "] is NULL");
            log.debug("Error: tbAgentLogin");
            return "Error";
        }

    }

    @Override
    public String tbAgentRelease(String username, String agentId, Long agentUniqId, Event.AgentMode mode, String releaseMessage) {
        Agent a = agentManager.get(agentUniqId);

        try {
            chatService.checkIfAgentIsInChat(username, a.getAgentId(), a.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        Long ucid = a.getUcid();

        if (null != a && a.isLoggedIn()) {

            agentManager.saveReleasedAgent(agentUniqId, Agent.Mode.valueOf(mode.toString()));
            chatService.deleteChatSessionCount(username, a.getAgentId());
            Map< String, Object> releaseEvent = new HashMap<>();
            releaseEvent.put("type", "toolbarRelease");
            releaseEvent.put("mode", Agent.Mode.valueOf(mode.toString()));
            releaseEvent.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(new Date()));
            redisAgentManager.hset(username + ":agent:events", agentId, new Gson().toJson(releaseEvent));

            eventManager.logEvent("release", a.getUserId(), username, agentUniqId, agentId, Agent.Mode.valueOf(mode.toString()), new Date(), null, releaseMessage, null);

            if (ucid != null && (StringUtils.equalsIgnoreCase(releaseMessage, "forceReleaseByAgent") || (StringUtils.containsIgnoreCase(releaseMessage, "releaseByAdmin") && !StringUtils.equalsIgnoreCase(releaseMessage, "releaseByAdmin:DispositionAPI")))) {

                List<Report> reports = reportManager.getReportByMonitorUcid(a.getUcid());
                if (reports.size() >= 1) {
                    StatusMessage statusMessage = telephonyManager.kickCall(username, agentId, "" + a.getUcid(), "" + a.getUcid(), reports.get(0).getDid(), a.getPhoneNumber());
                    log.debug("Kick status:" + a.getUcid() + " | " + statusMessage);
                }
            }

            log.debug("Success: [" + username + "][" + agentId + "] is released with " + a);
            return "Success: tbAgentRelease~" + a.getMode();

        } else {
            log.debug("Error: [" + username + "][" + agentId + "] is NULL");
            return "Error: tbAgentRelease[" + agentId + "] ";
        }
    }

    /**
     * @param agentId
     * @return
     */
    @Override
    public StatusMessage tbAgentPause(String username, String agentId, Long agentUniqId, String reason, Event.AgentMode mode) {
        Agent a = agentManager.get(agentUniqId);
        //        log.debug("tbAgentPause " + agentId + " is " + a);
        if (null != a && a.isLoggedIn()) {

            if (agentManager.savePauseAgent(agentUniqId, reason) == 0) {//unable to pause agent, may be nextFlag =1(means call might have initiated already)
                return new StatusMessage(Status.ERROR, "Unable to pause . Call might have initiated alreday.");
            }

            Map<String, Object> pauseEvent = new HashMap<>();
            pauseEvent.put("type", "toolbarPause");
            pauseEvent.put("mode", a.getMode());
            pauseEvent.put("pauseReason", reason);
            pauseEvent.put("agentStatus", "PAUSED");
            pauseEvent.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(new Date()));
            redisAgentManager.hset(username + ":agent:events", agentId, new Gson().toJson(pauseEvent));

            eventManager.logEvent("pause", a.getUserId(), username, agentUniqId, agentId, a.getMode(), new Date(), null, reason, null);
            log.debug("Success: [" + username + "][" + agentId + "] Paused with " + reason);
            return new StatusMessage(Status.SUCCESS, "tbAgentPause");
        } else {//-> won't happen .
            log.error("Error: [" + username + "][" + agentId + "] is NULL");
            return new StatusMessage(Status.ERROR, "Can't pause,Reason:" + (a == null ? "No agent found with the agent id '" + agentId + "'" : "Agent is not logged in with this id:" + agentId));
        }
    }

    /**
     * @param agentId
     * @return
     */
    public String tbAgentBusy(String username, String agentId) {
        Agent a = agentManager.getAgentByAgentIdV2(username, agentId);
        log.debug("Agent with id " + agentId + " is " + a);
        log.debug("tbAgentBusy");
        if (null != a && a.isLoggedIn()) {
            a.setState(State.BUSY);
            agentManager.save(a);
//            redisAgentManager.saveAsJson(username + ":" + agentId, a);
            //redisAgentManager.moveSet(username + ":state:" + origState, username + ":state:" + destState, a.getAgentId());

            log.debug("Success: [" + username + "][" + agentId + "] is BUSY");
            return "Success: tbAgentBusy";
        } else {
            log.debug("Error: tbAgentBusy");
            return "Error: tbAgentBusy";
        }
    }

    /**
     * @param agentId
     * @return
     */
    @Override
    public String tbSetAgentMode(String username, String agentId, Long agentUniqId, String mode, String state, boolean reconnect, String reconnectDetails) {
        Agent a = agentManager.get(agentUniqId);

        /*if(a.getNextFlag() == 1l){
            log.error("Agent next flat is not 0:"+a.toInfoString());
            return "Error";
        }*/
        //getting the original mode. We will then move th agent from this mode to the new mode
        Agent.Mode agentMode = Agent.Mode.INBOUND;
        log.debug("tbSetAgentMode :[" + username + "][" + agentId + "][" + mode + "]");
        if (null != a && a.isLoggedIn()) {
            if (mode.equalsIgnoreCase("preview")) {
                agentMode = Agent.Mode.PREVIEW;
            } else if (mode.equalsIgnoreCase("manual")) {
                agentMode = Agent.Mode.MANUAL;
            } else if (mode.equalsIgnoreCase("progressive")) {
                agentMode = Agent.Mode.PROGRESSIVE;

            } else if (mode.equalsIgnoreCase("blended")) {
                agentMode = Agent.Mode.BLENDED;

            } else if ("chat".equalsIgnoreCase(mode)) {
                agentMode = Agent.Mode.CHAT;
            }
            agentManager.saveAgentMode(agentUniqId, agentMode, reconnect);
            if (!reconnect) {

                String event = "release";
                String eventData = a.getStateReason();
                if (StringUtils.equalsIgnoreCase(state, Constants.READY)) {
                    event = "release";
                } else if (StringUtils.equalsIgnoreCase(state, Constants.PAUSED)) {
                    event = "pause";
                } else if (a.getState() == Agent.State.EXCEPTION) {
                    event = Agent.State.EXCEPTION.toString();
                    event = "changeMode";
                } else {
                    event = "AUX";
                    eventData = "changeMode";
                }

                eventManager.logEvent(event, a.getUserId(), username, agentUniqId, agentId, agentMode, new Date(), null, StringUtils.isBlank(eventData) ? "changeMode" : eventData, null);

            }

            log.debug("Success: [" + username + "][" + agentId + "] mode changed to [" + mode + "]");
            return "Success";
        } else {
            log.debug("Error: [" + username + "][" + agentId + "][" + mode + "]");
            return "Error";
        }
    }

    @Override
    public List<Report> getAgentCallHistory(String user, String agentId) {

        if (userManager.hasRole(user, Constants.AGENT_CALL_HISTORY_ROLE)) {
            return redisReportManager.getList(user + ":agentcallhistory:" + agentId, 0, 20);
        }

        return null;
    }

    /**
     * Returns the dispositions.
     *
     * @param username
     * @param agentId
     * @param did
     * @param type
     * @param campaignId
     * @return
     */
    @Override
    public Map<String, String> getDispositions(String username, Long agentUniqueId, String agentId, String did, String type, String campaignId) {
        Map<String, String> dispositionMap = new LinkedHashMap<>();
        Agent a = null;
        if (agentUniqueId != null) {
            a = agentManager.get(agentUniqueId);
        } else {
            a = agentManager.getAgentByAgentIdV2(username, agentId);
        }
        Campaign c = null;
        if (a != null) {
//            Campaign c = (a.getSkill() != null? a.getSkill().getCampaign():(a.getCampaign() != null? a.getCampaign():null));
            if (campaignId != null && !campaignId.isEmpty()) {
                c = campaignManager.get(new Long(campaignId));
            } else if (type.equalsIgnoreCase("manual")) {
                c = campaignManager.getCampaignsByDid(did);
            } else {
                c = campaignManager.getCampaignsByDid(did, type);
            }
            if (c != null) {
                List<Disposition> dispositions = dispositionManager.getDispositionsByCampaign(c.getCampaignId());

                dispositions
                        .stream()
                        .filter(disposition -> StringUtils.isNotBlank(disposition.getReason()))
                        .forEach(disposition -> dispositionMap.put(disposition.getReason(), disposition.getReason()));

                log.debug("Success : [" + username + "][" + agentId + "][" + did + "][" + type + "] returned Dispositions = " + dispositions.size());

            } else {
                log.debug("Error : [" + username + "][" + agentId + "][" + did + "][" + type + "] have no Dipositions or campaign is null :" + (c == null ? "Campaing is null:" : "Dispositions are empty."));
            }
        } else {
            log.debug("Error : [" + username + "][" + agentId + "][" + did + "][" + type + "] agent is Null to return Dispositons");
        }
        if (userManager.hasRole(username, Constants.CALLBACKS_ROLE)) {
            dispositionMap.put("-300", "callBack");
        }
        return dispositionMap;
    }

    @Override
    public String setDisposition(Long agentUniqueId, String dataId, String dispositionCode, String callBackTime, String ucid, String did, String dispComments, String agentId, String username, Long ticketID, String uui, String monitorUcid, String callBackTz) {
        PreviewData data = null;
        if (dataId != null && !dataId.isEmpty()) {
            data = previewDataManager.get(new Long(dataId));
            log.debug("^^^@Before:" + data.toLongString());
            data.setDisposition(dispositionCode != null ? dispositionCode : "");
            data = previewDataManager.save(data);// Saved Dispositon in PreviewData Table
            log.debug("^^^@After:" + data.toLongString());

        }
        try {
            if (ucid != null && !ucid.isEmpty()) {
                String disposition = dispositionCode != null ? dispositionCode.trim() : "";
                String query = "update Report r set r.disposition=?, r.comment=?, r.refId=?, r.wrapUpDuration=timestampdiff(second,r.endTime,?) where r.ucid =? and r.did =? and (r.disposition is NULL or r.disposition like '%Wrap up time exceeded%')";
                int updateCount = reportManager.bulkUpdate(query, new Object[]{disposition, dispComments, ticketID, new Date(), new Long(ucid), did});
                log.debug("Updated disposition for params :" + Arrays.toString(new Object[]{query, disposition, dispComments, ticketID, new Date(), new Long(ucid), did}) + " |Update count ->" + updateCount);
            } else {
                log.debug("Unable to update disposition as ucid is empty:" + ucid + " | Other params:" + Arrays.toString(new Object[]{dataId, dispositionCode, callBackTime, ucid, did, dispComments, agentId, username, ticketID}));
            }
        } catch (Exception e) {
            log.error("Exception in saving disposition:" + e.getMessage(), e);
        }
        
        try {
            //
            // ------ > Save call back details only if the user has the permissions.
            if (userManager.hasRole(username, Constants.CALLBACKS_ROLE)) {
                Date cbt = null;
                log.debug("callBackTime=" + callBackTime);
                if (StringUtils.isNotBlank(callBackTime)) {
                    Report r = reportManager.getReportByUCID(new Long(ucid));

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    try {
                        cbt = sdf.parse(callBackTime);
                       // cbt = DateUtil.convertFromOneTimeZoneToOhter(cbt, r.getUser().getUserTimezone(), r.getUser().getServerTimezone());
                    } catch (Exception e) {
                        log.error("Uh ya!! unable to Convert CallBackTime.." + cbt);
                    }
//                    String phonNumber = data != null ? data.getPhoneNumber() : r.getDest();
//                    Campaign c = data != null ? data.getCampaign() : r.getCampaign();
                    String phonNumber = r.getDest();

                    Campaign c = campaignManager.get(r.getCampaignId());

                    log.debug("Before Saving the Callbacks Checking Phone number and UUI");
                    log.debug("phonNumber : " + phonNumber + "  uui : " + uui);
                    if (c.getCampaignType().equals("Progressive")) {
//                        if(data.getPhoneNumber().equals(uui)){
//                            uui = null;
//                        }
                        log.debug("Campaign Type is Progressive and uui : " + uui);
                    } else {
                        uui = null;
                    }
                    log.debug("Finally UUI Parameter : " + uui);
                    CallBack callBack = callBackManager.getCallBackByPhoneNumberAndAgentId(c.getUser().getId(), phonNumber, agentId);
                    if (callBack == null) {
                        callBack = new CallBack();
                    } else {
                        callBack.setDeleted(false);
                        callBack.setDeleteComment(null);
                        callBack.setDateDeleted(null);
                        callBack.setDateRescheduled(null);
                        callBack.setRescheduleComment(null);
                    }
                    callBack.setCallbackNumber(phonNumber);
                    callBack.setComments(dispComments);
                    callBack.setUser(userManager.getUserByUsername(username));
                    callBack.setCallbackDate(cbt);
                    callBack.setCallbackTz(callBackTz);
                    callBack.setCampaign(c);
                    callBack.setCalled(false);
                    callBack.setDateCreated(new Date());
                    callBack.setUui(uui);
                    callBack.setAgent(agentManager.get(agentUniqueId));
                    callBack = callBackManager.save(callBack);
                    log.debug("<-Saved call back:" + callBack.toShortString());
                }
            }
            
        } catch (Exception e) {
            log.error("Exception in saving call back:" + e.getMessage(), e);
        }
            
        Long count = redisAgentManager.zrem(RedisKeys.CUSTOMER_CALLBACKSV3_TODISPOSE_SET, monitorUcid);
        log.debug("remove from todispose set monitorUcid: "+monitorUcid+" count: "+count);

        if (count > 0) 
            log.debug(monitorUcid+" Adding in callback set : "+redisAgentManager.addToSet(RedisKeys.CUSTOMER_CALLBACKSV3_SET, monitorUcid));
            
        log.debug("success : [" + username + "][" + agentId + "][" + ucid + "] has set Disp = [" + dispositionCode + "]");
        return "success";
    }

    public PreviewData getNextPreviewNumber(String agentId, Long campaignId) {
//        log.debug("Called the getNextPreviewNumber");
        //check whether the campaign is Agent wise or Non AgentWise
//        boolean agentWise = false;

        //FIXME get 'isAgentWise' flag from toolbar itself.Don't query always.
        Campaign c = campaignManager.get(campaignId);
        PreviewData previewData = null;
        if (c.getDialMethod() == Campaign.DialMethod.Agentwise) {
            previewData = previewDataManager.getNumberToDialForAgentFromDialer(campaignId, agentId);
        } else {
            previewData = previewDataManager.getNumberToDialNonAgentWiseFromDialer(campaignId);
        }

        log.debug("Giving  " + previewData + " <==> to agent:" + agentId);

//        if (c != null) {
//            agentWise = c.isAgentWise();
//        }
//        PreviewData previewData = previewDataManager.getNextPreviewData(agentId, campaignId, agentWise);
//        log.debug("Preview Data =" + previewData);
//        if (previewData != null) {
//            previewData.setNextFlag(true);
//            previewData.setLastSelected(new Date());
//            previewDataManager.saveAsJson(previewData);
//        } else {
//            log.debug("Is Camp Completed " + previewDataManager.isPreviewCampaignCompleted(campaignId));
//            if (previewDataManager.isPreviewCampaignCompleted(campaignId)) {
//                c.setPosition("COMPLETED");
//                campaignManager.saveAsJson(c);
//            }
//        }
        return previewData;
    }

    public int getPreviewDataSize(String agentId, Long campaignId) {

//        boolean agentWise = false;
        Campaign c = campaignManager.get(campaignId);
        if (c != null) {
            if (c.getDialMethod() == Campaign.DialMethod.Agentwise) {
                return previewDataManager.getCountOfNumbersRemainingToDialForAgent(campaignId, agentId);
            } else {
                return previewDataManager.getCountOfNumbersRemainingToDial(campaignId);
            }
        }
//        return previewDataManager.getPreviewDataSize(agentId, campaignId, agentWise);
        return 0;

    }

    @Override
    public String tbResetPreviewNumber(String pId) {
        log.debug("@@@@@Called reset preview number..");
        previewDataManager.resetPreviewNumber(Long.valueOf(pId));
        return "Success";
    }

    @Override
    public StatusMessage sendSMS(String user, BigInteger ucid, Long campaignId, String agentId, String destination, String msg, String entityId, String templateId) {
        Map<String, Object> params = new LinkedHashMap();
        params.put("user_id", userManager.getUserByUsername(user).getId());

        List paramList = campaignManager.executeProcedure("call Get_UserParamters(?)", params);

        if (!paramList.isEmpty()) {
            boolean sendSMS = false;
            String smsURL = null;
            String requestType = "GET";

            Map item;
            for (Object param : paramList) {
                item = (Map) param;

                switch (item.get("ParameterCode").toString()) {
                    case "SEND_SMS":
                        sendSMS = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                        break;
                    case "SMS_URL":
                        smsURL = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? item.get("DefaultValue").toString() : item.get("ParameterValue").toString();
                        requestType = item.get("RequestType") == null || StringUtils.isEmpty(item.get("RequestType").toString())
                                ? item.get("DefaultRequestType").toString() : item.get("RequestType").toString();
                        break;
                }

            }

            if (sendSMS) {
                return sMSManager.sendSMS(user, ucid, campaignId, smsURL, requestType, agentId, destination, msg, entityId, templateId);
            } else {
                return new StatusMessage(Status.ERROR, "This feature has been disabled by Admin");
//                return "Error:This feature has been disabled by Admin.";
            }
        }

//        return "Error:This feature is not enabled for you.";
        return null;
    }

     @Override
    public StatusMessage sendWhatsappMSG(String user, String recipient, String templateName, String replacementText) {
        logger.debug("came in toolbarmanager impl with values: "+user+"---"+recipient+"---"+templateName+"---"+replacementText);
        Map<String, Object> params = new LinkedHashMap();
        params.put("user_id", userManager.getUserByUsername(user).getId());

        List paramList = campaignManager.executeProcedure("call Get_UserParamters(?)", params);

        if (!paramList.isEmpty()) {
            boolean sendMSG = false;
            String whatsappURL = null;
            String requestType = "POST";

            Map item;
            for (Object param : paramList) {
                item = (Map) param;

                switch (item.get("ParameterCode").toString()) {
                    case "SEND_WHATSAPP":
                        logger.debug("got case SEND_WHATSAPP");
                        sendMSG = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                        break;
                    case "WHATSAPP_URL":
                        logger.debug("got case WHATSAPP_URL");
                        whatsappURL = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? item.get("DefaultValue").toString() : item.get("ParameterValue").toString();
                        requestType = item.get("RequestType") == null || StringUtils.isEmpty(item.get("RequestType").toString())
                                ? item.get("DefaultRequestType").toString() : item.get("RequestType").toString();
                        break;
                }

            }
            logger.debug("after condition sendMSG "+sendMSG+"-- whatsappUrl: "+whatsappURL);
            if (sendMSG) {
                return sMSManager.sendWhatsappMSG(user, whatsappURL, requestType,  recipient, templateName, replacementText);
            } else {
                return new StatusMessage(Status.ERROR, "This feature has been disabled by Admin");
            }
        }

        return null;
    }

    @Override
    public void alertAgentExceededPauseTime(Long agentUniqueId, String username, String agentId, String reason, int timeout) {
        User user = userManager.getUserByUsername(username);
        Agent agent = null;
        if (agentUniqueId != null) {
            agent = agentManager.get(agentUniqueId);
        } else {
            agent = agentManager.getAgentByAgentIdV2(username, agentId);
        }
        List<String> recipients = new ArrayList(2);
        recipients.add(user.getEmail());
        if (!StringUtils.isEmpty(agent.getEmail())) {
            recipients.add(agent.getEmail());
        }
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("agent", agent);
        model.put("pauseReason", reason);
        model.put("timeout", TimeConverter.secondsToTime((long) timeout));

        Map<String, Object> eventMap = new LinkedHashMap<>();
        eventMap.put("event", "pauseExceeded");
        eventMap.put("pauseReason", reason);
        eventMap.put("timeout", TimeConverter.secondsToTime((long) timeout));
        pushToStream(user.getUsername(), agent.getAgentId(), eventMap, System.currentTimeMillis());
        try {
//            System.out.println("Pause alert vm:"+puaseAlertTemplateResource.getFile().getAbsolutePath());
            log.info("Sending pause time exceeded alert:" + recipients + " | " + model);
            mailEngine.sendMimeMessage(recipients.toArray(new String[0]), null, null, null, null, "pauseAlert.vm",
                    model, "CloudAgent Alert", null);
            log.info("Sent pause time exceeded alert:" + recipients + " | " + model);

        } catch (MessagingException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public String tbSkipPreviewNumber(Long pid, String message) {
        log.debug("Skipping number with id:" + pid + " -> " + message);
        previewDataManager.skipPreviewNumber(pid, message);
        return "Success";
    }
    
    @Override
    public String tbClosePreviewNumber(Long pid, String message, String disp, String comment){
        log.debug("closing preview data : "+pid+" with reason : "+disp+" comments : "+comment);
        previewDataManager.closePreviewNumber(pid, disp, comment, message);
        return "Success";
    }

    public List tbGetCustomerData(String dataId) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dataId", new Long(dataId));
        log.debug("Getting Customer Data List ");
        log.debug("previewExtraDataManager=" + previewExtraDataManager);
        List<PreviewExtraData> custDataList = previewExtraDataManager.findByNamedQuery("getCustomerData", params);
        log.debug("Customer Data List =" + custDataList);
        List<String> l = new ArrayList<String>();

        if (custDataList.size() > 0) {
            PreviewExtraData extraData = custDataList.get(0);
//            PreviewDataMap dataMap = extraData.getPreviewData().getCampaign().getPreviewDataMap();
            PreviewDataMap dataMap = campaignManager.getPreviewDataMap(extraData.getPreviewData().getCampaign().getCampaignId());
            String[] mapDataNames = dataMap.getValue().split(",");
            String[] mapDataValues = extraData.getData().split("~");
            int i = 0;
            String col;
            for (String s : mapDataNames) {
//                mp.put(s, (mapDataValues.length > i) ? mapDataValues[i] : "");
                col = s + "~" + ((mapDataValues.length > i) ? mapDataValues[i].toString() : "");
                l.add(col);
                i++;
            }
        }
        return l;
    }

    @Override
    public List<String> tbGetCallBackList(String username, String agentId) {
        return callBackManager.getCallBackList(username, agentId);

    }

    @Override
    public List<CallbacksGrouped> tbGetGroupedCallBackList(String username, String agentId) {
        return callBackManager.tbGetGroupedCallBackList(username, agentId);
    }

    @Override
    public boolean tbDeleteCallback(String username, String agentId, Long cbId) {
        return callBackManager.deleteCallback(username, agentId, cbId);
    }

    public JsonObject tbResechduleCallback(String username, String agentId, String time, Long callbackId, String rescheduleComment,String callBackTz) {
        return callBackManager.resechduleCallback(username, agentId, time, callbackId, rescheduleComment,callBackTz);
    }

    public String tbFailCallback(String username, String agentId, Long callbackId) {
        return callBackManager.failCallback(username, agentId, callbackId);
    }

    public Long generateTicketId(Long ucid) {
        Report report = reportManager.getReportByUCID(ucid);
        return report.getReport_id();
    }

    public void setsMSManager(SMSManager sMSManager) {
        this.sMSManager = sMSManager;
    }

    public void setRedisReportManager(RedisManager<Report> redisReportManager) {
        this.redisReportManager = redisReportManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }

    public void setPreviewExtraDataManager(GenericManager<PreviewExtraData, Long> previewExtraDataManager) {
        this.previewExtraDataManager = previewExtraDataManager;
    }

    public void setOccManager(OCCManager occManager) {
        this.occManager = occManager;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setsMSTemplateManager(SMSTemplateManager sMSTemplateManager) {
        this.sMSTemplateManager = sMSTemplateManager;
    }
    private SMSTemplateManager sMSTemplateManager;
    private RedisManager<Report> redisReportManager;
    private SMSManager sMSManager;
    //    private EventManager eventManager;
    private DispositionManager dispositionManager;
    private PreviewDataManager previewDataManager;
    private GenericManager<PreviewExtraData, Long> previewExtraDataManager;
    private URL ticketSystemURL;
    private OCCManager occManager;
    private MailEngine mailEngine;
    // ----- > Redis managers
    private RedisAgentManager redisAgentManager;
    private ChatServiceImpl chatService;
    private TelephonyManager telephonyManager;
//    private BeanstalkService beanstalkService;

    public void setTelephonyManager(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    public void setMailEngine(MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    public URL getTicketSystemURL() {
        return ticketSystemURL;
    }

    public void setTicketSystemURL(URL ticketSystemURL) {
        this.ticketSystemURL = ticketSystemURL;
    }

    public ToolBarManagerImpl() {
        super();

    }
}
