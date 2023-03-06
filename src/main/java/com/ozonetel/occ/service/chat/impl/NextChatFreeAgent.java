package com.ozonetel.occ.service.chat.impl;

import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.impl.UpdateReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class NextChatFreeAgent {

    public NextChatFreeAgent(String _monitorUcid, String sessionId, String chatCustomerName, String did, String user, int customerLimitPerAgent, AgentManager agentManager, CallQueueManager callQueueManager, ChatServiceImpl chatService) {
        this.agentManager = agentManager;
        this.monitorUcid = _monitorUcid;
        timeElapsed = -2l;
        this.sessionId = sessionId;
        this.did = did;
        this.user = user;
        this.ucid = monitorUcid + "" + fallBackCounter;
        this.customerLimitPerAgent = customerLimitPerAgent;
        this.chatCustomerName = chatCustomerName;
        this.callQueueManager = callQueueManager;
        this.chatService = chatService;
    }

    public Map<String, String> getNextFreeChatAgent(String skillName) {

        Map<String, String> returnMap = new LinkedHashMap<>();
        returnMap.put("status", "fail");
        returnMap.put("message", "NoAgentAvailable");
        boolean clientExists = false;
        try {
            logger.debug("Checking if client session exists : "+sessionId);
            clientExists = chatService.checkChatClientSessionExists(sessionId);
        } catch (Exception e) {
            logger.debug("Exception occured while checking if client exists | "+sessionId+" | "+e);
            clientExists = true;
        }
        if (clientExists) {
            timeElapsed += 2l;//increment 2 sec
            Map<String, Object> queryParams = new LinkedHashMap<>();
            queryParams.put("ucid", monitorUcid);
            queryParams.put("callerId", sessionId);
            queryParams.put("did", did);
            queryParams.put("skillName", skillName);
            queryParams.put("pPriority", 0);
            queryParams.put("acdchoice", 1);
            queryParams.put("maxSessionCount", this.customerLimitPerAgent);
            List l = new ArrayList();
            logger.debug("Query params:" + queryParams);
//            if (redisAgentManager.sismember("optimal:users", StringUtils.lowerCase(user))) {
//                logger.debug("ChatFreeAgent for optimal users : Get_ChatAgentV2");
//                l = agentManager.executeProcedure("{call Get_ChatAgentV2(?,?,?,?,?,?,?)}", queryParams);
//            } else {
//                l = agentManager.executeProcedure("{call Get_ChatAgent(?,?,?,?,?,?,?)}", queryParams);
//            }
            l = agentManager.executeProcedure("{call Get_ChatAgent(?,?,?,?,?,?,?)}", queryParams);

            if (l.iterator().hasNext()) {
                HashMap<String, String> mp = (HashMap<String, String>) l.iterator().next();
                logger.debug("Next free agent result:" + mp);
                if (mp.get("CampaignExists").equals("1") && mp.get("SkillExists").equals("1")) {
                    if (StringUtils.isNotEmpty(mp.get("Agent")) && !mp.get("Agent").equals("0")) {
                        returnMap.put("status", "success");
                        returnMap.put("message", mp.get("Agent"));
                        returnMap.put("ClientID", mp.get("ClientID"));
                        returnMap.put("ucid", ucid);
                        returnMap.put("SessionCount", mp.get("SessionCount"));
                        logger.debug("Got agent:" + returnMap);
                        return returnMap;
                    } else {
                        int queueSize = Integer.parseInt(mp.get("QueueSize"));
                        int queueTime = Integer.parseInt(mp.get("QueueTime"));
                        int position = Integer.parseInt(mp.get("QueuePosition"));
                        if ((queueSize > 0 && position > queueSize) || (queueTime > 0 && timeElapsed >= queueTime)) { //queue time/position exceeded.apply fallback.
                            logger.debug("queue size exceeded (" + position + ">" + queueSize + ")?" + (position > queueSize) + " | time exceeded (" + timeElapsed + " > " + queueTime + ")?" + (timeElapsed >= queueTime));
                            if (mp.get("FallbackRule").equals("4")) {//Falling back to skill
                                logger.debug("############### Falling back to skill:" + mp.get("FallbackValue"));
                                timeElapsed = -2l;
                                logger.debug("Delete from queue for skill:" + skillName);
                                callQueueManager.deleteCallQueue(sessionId, skillName, did, Long.valueOf(monitorUcid));
                                return getNextFreeChatAgent(mp.get("FallbackValue"));
                            } else if (mp.get("FallbackRule").equals("5")) {
                                logger.debug("Delete from queue and fallback to IVR");
                                callQueueManager.deleteCallQueue(sessionId, skillName, did, Long.valueOf(monitorUcid));
                                returnMap.put("status", "fallback");
                                return returnMap;
                            } else {//Disconnect
                                logger.debug("Delete from queue and disconnect");
                                callQueueManager.deleteCallQueue(sessionId, skillName, did, Long.valueOf(monitorUcid));
                                return returnMap;
                            }

                        }
                        try {
                            Thread.sleep(2000);
                        } catch (Exception ignore) {

                        }
                        logger.debug("@@@@Reched end");
                        return getNextFreeChatAgent(skillName);
                    }
                } else {//campaign or skill doesn't exist
                    returnMap.put("message", mp.get("CampaignExists").equals("0") ? "Campaign doesn't exist" : "Skill doesn't exist");
                    return returnMap;
                }
            }
        } else {
            returnMap.put("message", "Client closed");
            logger.debug("Client closed while getting free agent. Ending chat...");
            callQueueManager.deleteCallQueue(sessionId, skillName, did, Long.valueOf(monitorUcid));
            chatService.UpdateChatDetailsSystemEnd(sessionId, "System:Client closed", null);
            chatService.tearDownChatSession(sessionId);
        }
        return returnMap;
    }

    public void setCallQueueManager(CallQueueManager callQueueManager) {
        this.callQueueManager = callQueueManager;
    }

    UpdateReport r = new UpdateReport();
    private final String sessionId;
    private final String did;
    private final String user;
    private String chatCustomerName;
    private final AgentManager agentManager;
    private final String monitorUcid;
    private int fallBackCounter;
    private long timeElapsed;
    private String ucid;
    private final int customerLimitPerAgent;
    private static final Logger logger = Logger.getLogger(NextChatFreeAgent.class);
    private CallQueueManager callQueueManager;
    private ChatServiceImpl chatService;

}
