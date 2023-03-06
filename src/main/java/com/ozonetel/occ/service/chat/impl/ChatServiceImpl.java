package com.ozonetel.occ.service.chat.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ChatDetails;
import com.ozonetel.occ.model.ChatLog;
import com.ozonetel.occ.service.*;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.model.UserIntegration;
import com.ozonetel.occ.service.command.ChatSkillTransferCommand;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.impl.Participant;
import com.ozonetel.occ.service.impl.PluginManager;
import com.ozonetel.occ.service.impl.TokenServerLocalImpl;
import com.ozonetel.occ.util.AppContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * This is for customer to agent chat.
 *
 * @author pavanj
 */
public class ChatServiceImpl {

    public boolean findAnAgent(String apiKey, String sessionId, String clientId, String _chatCustName) {
        return false;
    }

    /**
     * Checks whether session closed was of agent chatting.
     *
     * @param user
     * @param agentId
     * @param agentUniqId
     */
    public void checkIfAgentIsInChat(String user, String agentId, Long agentUniqId) {

        Set<String> callingSessions = getAgentCallingChatSessions(user, agentId);
        if (callingSessions != null && !callingSessions.isEmpty()) { // agent's in calling mode and now will be put back in queue :)
            for (String sessionId : callingSessions) {
                try {
                    logger.debug("♞♞♞" + user + ":" + agentId + " is in calling state with session->" + sessionId + " | so sending chat back to queue..^^^^");
                    ChatSessionDetails csd = getChatSessionDetails(sessionId);
                    logger.debug(new ChatSkillTransferCommand(user, agentId, agentUniqId, sessionId, sessionId, csd.getApiKey(), csd.getDid(), csd.getSkill(), true, chatTransferService).execute());
                } catch (Exception e) {
                    logger.error("Session:" + sessionId + " caused error", e);

                }
            }
        }
        Set<String> sessions = getAgentChatSessions(user, agentId);
        if (sessions != null && !sessions.isEmpty()) { // agent's been chatting and now will be closed :(
            for (String customer_sessionid : sessions) {
                //if its a sip chat it will be ended in UpdateReport
                if (!redisAgentManager.sismember("ca:sip-chat-sessions", customer_sessionid)) {
                    try {
                        logger.debug("♞♞♞" + user + ":" + agentId + " is in chat with session->" + customer_sessionid + " | so ending chat..^^^^");
                        //new ChatOffCommand(agentManager, this, redisAgentManager, customer_sessionid, user, agentId, chatSessionDetails.getDid(), chatSessionDetails.getCampaignId(), true).chatOff();
                        this.UpdateChatDetailsSystemEnd(customer_sessionid, "System:Agent Disconnect", getChatJson(customer_sessionid));
                        new ChatOffCommand(eventManager, agentManager, this, redisAgentManager, customer_sessionid, user, agentId, agentUniqId, true, chatClientMsgSrvr, facebookChatService).chatOff();
                    } catch (Exception e) {
                        logger.error("Session:" + customer_sessionid + " caused error", e);

                    } finally {
                        redisAgentManager.srem("cachat:" + user + ":" + agentId + ":chat-sessions", customer_sessionid);
                    }
                }
                deleteChatSessionCount(user, agentId);
            }
        }
    }

    public void sendTokenToSession(String sessionId, Token tokenResponse) {
        tokenServer = getTokenServer();
        Set<String> clientWsIds = getClientWsidsforSession(sessionId);
        for (String clientWsId : clientWsIds) {
            tokenServer.sendChatToken(tokenServer.getConnector(clientWsId), tokenResponse);
        }
    }

    public void sendTokenToAgent(String msg, String sessId, String agentWsId) {
        tokenServer = getTokenServer();
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setString("type", "inchatmulti");
        tokenResponse.setString("chatmsg", msg);
        tokenResponse.setString("chatCustSessionId", sessId);
        tokenResponse.setString("timestamp", new SimpleDateFormat("HH:mm").format(new Date().getTime()));
        tokenServer.sendToken(tokenServer.getConnector(agentWsId), tokenResponse);
    }

    public void sendTokenToAgent(Token tokenResponse, String agentWsId) {
        tokenServer = getTokenServer();
        tokenServer.sendChatToken(tokenServer.getConnector(agentWsId), tokenResponse);
    }

    public void sendTokenToMsgServer(String msgSrvUrl, String sessionId, Token tokenResponse, Boolean toAgent) {
        try {
            logger.debug(tokenResponse.getMap());
            JsonObject reqObj = new JsonObject();
            reqObj.addProperty("tokenResponseJson", new Gson().toJson(tokenResponse.getMap()));
            //reqObj.addProperty("tokenResponse", tokenResponse.getMap().toString());
            reqObj.addProperty("sessionId", sessionId);
            reqObj.addProperty("toAgent", toAgent);
            //reqObj.addProperty("callBackUrl", "http://172.16.15.39:8080/OCCDV2/agentChatMsg.html");
            logger.debug("Sending data with HTTP : " + reqObj.toString());
            HttpClient client = new DefaultHttpClient();
            //logger.debug(client);
            HttpPost p = new HttpPost(getChatMsgSenderUrl(msgSrvUrl));
            p.setHeader("Content-Type", "application/json; charset=UTF-8");
            //logger.debug(p);
            p.setEntity(new StringEntity(reqObj.toString(), "UTF-8"));
//            p.setEntity(new StringEntity(reqObj.toString(),
//                    ContentType.create("application/json")));
            logger.debug("Posting TO : " + p + " with token : " + reqObj);
            HttpResponse response = client.execute(p);
            logger.debug("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            logger.debug(result);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    public String getChatMsgSenderUrl(String msgSrvr) {
        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.applyPattern(chatMsgSenderUrl);
        return messageFormat.format(new Object[]{msgSrvr});
    }

    public String getChatStoreKey(String sessionId) {
        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.applyPattern(RedisKeys.CHAT_DATA);
        return messageFormat.format(new Object[]{sessionId});
    }

    public void saveChatMessage(String sessionId, boolean agent, long timestamp, String message, String type, String agentId) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("agent", agent);
        jsonObject.addProperty("ts", timestamp);
        jsonObject.addProperty("msg", message);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("agentId", agentId);

        redisAgentManager.rpush(getChatStoreKey(sessionId), jsonObject.toString());
        redisAgentManager.zadd(RedisKeys.CACHAT_SESSION_LASTACTIVE_TMES, timestamp, sessionId);

    }

    public void saveChatReport(Long monitorUcid, String chatMessage) {
        chatLogManager.save(new ChatLog(monitorUcid, chatMessage));
    }

    public void saveChatDetails(String sessionId, ChatStates mode, String did, String skill) {
        ChatDetails cd = new ChatDetails();
        cd.setSessionId(sessionId);
        cd.setNode(mode);
        Campaign campaign = campaignManager.getCampaignsByDid(did);
        if (campaign != null) {
            cd.setCampaignId(campaign.getCampaignId());
        }
        cd.setSkillName(skill);
        ChatSessionDetails csd = getChatSessionDetails(sessionId);
        if (!StringUtils.isBlank(csd.getPhoneNumber())) {
            cd.setPhoneNo(csd.getPhoneNumber());
        }
        logger.debug("got skillId in saveChatDetails "+csd.getSkillId());
        cd.setSkillId(csd.getSkillId());
        cd.setUui(csd.getChatCustId());
        cd.setCustName(csd.getChatCustName());
        cd.setEmail(csd.getEmail());
        cd.setStartDate(new Date());
        cd.setStartTime(new Date());
        cd.setMonitorUcid(csd.getMonitorUcid());
        cd.setUserId(csd.getCaUserId());
        cd.setChannelType(csd.getChannelType());
        cd.setCustomerId(csd.getCustomerId());
        if (mode != ChatStates.NEXT_FREE_AGENT) {
            cd.setAgentId(csd.getAgentId());
            logger.debug("got agentuniqueid in saveChatDetails "+csd.getAgentUniqueId());
            cd.setAgentUniqueId(csd.getAgentUniqueId());
        }

        chatDetailManager.save(cd);

        logger.debug("Saved chat details ---->Session : " + sessionId + " State : " + mode + " --->" + cd);

    }

    public void UpdateChatDetailsSystemEnd(String sessionId, String who, String transcript) {
        UpdateChatDetailsSystemEnd(sessionId, who, transcript, null, null, null);
    }

    public void UpdateChatDetailsSystemEnd(String sessionId, String who, String transcript, Participant participant, Long transferId, Boolean isDecline) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", getChatSessionDetails(sessionId).getMonitorUcid());
        List<ChatDetails> list = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params);
        if (!list.isEmpty()) {
            ChatDetails cd = list.get(0);
            cd.setEndDate(new Date());
            cd.setEndTime(new Date());
            cd.setChat(transcript);
            cd.setEndBy(who);
            
            if ((isDecline == null || !isDecline) && participant != null) {
                cd.setTransferType((long) participant.ordinal());

                if (participant == Participant.AGENT)
                    cd.setTransferAgentId(transferId);
                else if (participant == Participant.SKILL)
                    cd.setTransferSkillId(transferId);
            }
            
            chatDetailManager.save(cd);
            logger.debug("Updated chat details : System end :---->" + cd);

        }
    }

    public static String encodeStringUrl(String str) {
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return encodedUrl;
        }
        return encodedUrl;
    }

    public void updateDisposition(String sessionId, String disposition, String comment, String monitorUcid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", monitorUcid);
        ChatDetails cd = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params).get(0);
        logger.debug("Updating disp for record" + cd);
        cd.setComments(comment);
        cd.setDisposition(disposition);
        chatDetailManager.save(cd);
    }

    public void UpdateChatDetails_agentEnd(String sessionId, String disposition, String comments, String agentId) {
        logger.debug("Update caht details:" + sessionId + "|disp:" + disposition + "|comm:" + comments);

        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", chatSessionDetails.getMonitorUcid());

        ChatDetails cd = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params).get(0);
        cd.setEndDate(new Date());
        cd.setEndTime(new Date());
        cd.setChat(getChatJson(sessionId));
        cd.setComments(comments);
        cd.setDisposition(disposition);
        
        if (cd.getNode() != ChatStates.NEXT_FREE_AGENT){
            cd.setAgentId(agentId);
            logger.debug("got agentuniqueid in UpdateChatDetails_agentEnd "+chatSessionDetails.getAgentUniqueId());
            cd.setAgentUniqueId(chatSessionDetails.getAgentUniqueId());
        }

//        cd.setChat(getChatJson(sessionId));
        cd.setEndBy("Agent");
        chatDetailManager.save(cd);
        logger.debug("Updated chat details : Agent end:---->" + cd);

    }

    public void UpdateChatDetails_custEnd(String sessionId) {
        logger.debug("Update caht details:" + sessionId);
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", chatSessionDetails.getMonitorUcid());

        ChatDetails cd = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params).get(0);
        cd.setEndDate(new Date());
        cd.setEndTime(new Date());
        cd.setChat(getChatJson(sessionId));
        
        if (cd.getNode() != ChatStates.NEXT_FREE_AGENT){
            cd.setAgentId(chatSessionDetails.getAgentId());
            logger.debug("got agentuniqueid in UpdateChatDetails_custEnd "+chatSessionDetails.getAgentUniqueId());
            cd.setAgentUniqueId(chatSessionDetails.getAgentUniqueId());
        }

        //        cd.setChat(getChatJson(sessionId));
        cd.setEndBy("Customer");
        chatDetailManager.save(cd);
        logger.debug("Updated chat details : Cust end:---->" + cd);

    }

    public void updateChatDetailsWithAgentTta(String sessionId, long timestamp) {
        logger.debug("updating agent tta for : " + sessionId + " timestamp : " + timestamp + " date : " + new Date(timestamp));
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", chatSessionDetails.getMonitorUcid());

        ChatDetails cd = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params).get(0);
        cd.setAgentTta(new Date(timestamp));
        chatDetailManager.save(cd);
        logger.debug("Updated chat details : Agent tta :---->" + cd);
    }

    public String getChatJson(String sessionId) {
        String chat_msg_store_key = getChatStoreKey(sessionId);
//        return new Gson().toJson(redisAgentManager.lrange(chat_msg_store_key, 0, -1));

        Type t = new TypeToken<Map<String, String>>() {
        }.getType();

        List<Map<String, String>> list = new ArrayList<>();
        for (Object s : redisAgentManager.lrange(chat_msg_store_key, 0, -1)) {
            list.add((Map<String, String>) new Gson().fromJson((String) s, t));
        }

        return new Gson().toJson(list);

    }

    public Set<String> getClientWsidsforSession(String sessionId) {
        return getChatSessionDetails(sessionId).getClientWsIds();
    }

    public boolean checkChatClientSessionExists(String sessionId) {
        if (getChatSessionDetails(sessionId) != null) {
            return getChatSessionDetails(sessionId).getClientWsIds().size() > 0;
        } else {
            return false;
        }

    }

    /**
     * Set maintains all the web socket IDs(multiple tabs) for a session.
     *
     * @param sessionId
     * @param clientId
     *
     */
    public void addClientIdToSess(String sessionId, String clientId) {
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.addClientWsId(clientId);
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
    }

    public void removeClientIdFromSess(String sessionId, String clientId) {
        if (getChatSessionDetails(sessionId) != null) {
            ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
            chatSessionDetails.removeClientWsId(clientId);
            saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
        }
    }

    public void deleteSession(String sessionId) {
        //redisAgentManager.del(getSessionClientIdsSetKey(sessionId));

        redisAgentManager.hdel(RedisKeys.CLIENT_SESSION_CHAT_DETAILS, sessionId);

    }

    public void setUpChatSession(String sessionId, String chatCustId, String apiKey, String user, String chatCustName, String email, String phone, String clientId, Long campaignId, String did, String skill, String monitorUcid, Long caUserId, String channel, String callbackUrl, String customerId, String recipient) {

        ChatSessionDetails chatSessionDetails = new ChatSessionDetails();
        chatSessionDetails.setSessionId(sessionId);
        chatSessionDetails.setApiKey(apiKey);
        chatSessionDetails.setCaUserName(user);
        chatSessionDetails.setCampaignId(campaignId);
        chatSessionDetails.setDid(did);
        chatSessionDetails.setChatCustName(chatCustName);
        chatSessionDetails.setEmail(email);
        chatSessionDetails.setPhoneNumber(phone);
        chatSessionDetails.addClientWsId(clientId);
        chatSessionDetails.setSkill(skill);
        logger.debug("In setUpChatSession skill name came : "+skill+"and user came : "+user);
        Long skillId = StringUtils.isNotEmpty(skill) && StringUtils.isNotEmpty(user) ? skillManager.getSkillsByUserAndSkillName(skill, user).getId() : null;
        logger.debug("set skillId in setUpChatSession "+skillId);
        chatSessionDetails.setSkillId(skillId);
        chatSessionDetails.setMonitorUcid(monitorUcid);
        chatSessionDetails.setChatCustId(chatCustId);
        chatSessionDetails.setCaUserId(caUserId);
        chatSessionDetails.setChannelType(channel);
        chatSessionDetails.setCallbackUrl(callbackUrl);
        chatSessionDetails.setCustomerId(customerId);
        chatSessionDetails.setRecipient(recipient);
        logger.debug("Saving details:" + chatSessionDetails);
        redisAgentManager.hset(RedisKeys.CLIENT_SESSION_CHAT_DETAILS, sessionId, convertChatDetailsToJSON(chatSessionDetails));

        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.applyPattern(RedisKeys.USER_CHAT_SESSIONS_SET);
        redisAgentManager.sadd(messageFormat.format(new Object[]{user}), sessionId);

    }

    public String getUserSessionStateKey(String username) {
        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.applyPattern(RedisKeys.USER_SESSION_STATE_MAP);
        return messageFormat.format(new Object[]{username});
    }

    public Long addSessionState(String sessionId, ChatStates state) {
//        logger.debug("Changing chat state to : " + state + " | " + sessionId + " | " + username);
//        return redisAgentManager.hset(getUserSessionStateKey(username), sessionId, state);

        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setChatState(state);
        chatSessionDetails.setStateStartTime(System.currentTimeMillis());
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
        return 0L;
    }

    public Long delUserSessionState(String username, String sessionId) {
        logger.debug("Deleting session from user : " + sessionId + " | " + username);
        return redisAgentManager.hdel(getUserSessionStateKey(username), sessionId);
    }

    public void updateChatSessionWithSkill(String sessionId, String skill,Long skillId) {

        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setSkill(skill);
        chatSessionDetails.setSkillId(skillId);
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
    }

    public void updateChatSessionWithMonitorucid(String sessionId, String monitorUcid) {
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setMonitorUcid(monitorUcid);
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
    }
    
    public void updateChatSessionWithTimeout(String sessionId, Long secs){
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setAutoTimeout(secs);
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
    }

    public void updateChatSessionWithUserInfo(String sessionId, String name, String email, String phone, String extraData) {
        logger.debug("Updating Chat with UserInfo : Session : " + sessionId + " Name : " + name + " Email : " + email + " Phone : " + phone + " ExtraData : " + extraData);

        //Update in redis
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setChatCustName(name);
        chatSessionDetails.setEmail(email);
        chatSessionDetails.setPhoneNumber(phone);
        chatSessionDetails.setChatCustId(extraData);//uui
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", chatSessionDetails.getMonitorUcid());
        //Update in DB
        ChatDetails cd = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params).get(0);
        if (!StringUtils.isBlank(chatSessionDetails.getPhoneNumber())) {
            cd.setPhoneNo(chatSessionDetails.getPhoneNumber());
        }
        cd.setUui(chatSessionDetails.getChatCustId());
        cd.setCustName(chatSessionDetails.getChatCustName());
        cd.setEmail(chatSessionDetails.getEmail());
        chatDetailManager.save(cd);
    }

    /**
     *
     * @param sessionId chat session ID
     * @param agentId agent id
     */
    public void updateChatSessionWithAgent(String sessionId, String agentId, Long agentUniqueId) {
        logger.debug("Updating Chat Session : " + sessionId + " with agent : " + agentId);
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setAgentId(agentId);
        logger.debug("set agentuniqueId in updateChatSessionWithAgent"+agentUniqueId);
        chatSessionDetails.setAgentUniqueId(agentUniqueId);
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);

        logger.debug(redisAgentManager.hincrBy(RedisKeys.AGENT_CHAT_SESSION_COUNTS, chatSessionDetails.getCaUserName() + ":" + agentId, 1l));
        logger.debug(redisAgentManager.srem("cachat:" + chatSessionDetails.getCaUserName() + ":" + agentId + ":calling-chat-sessions", sessionId));
        logger.debug(redisAgentManager.sadd("cachat:" + chatSessionDetails.getCaUserName() + ":" + agentId + ":chat-sessions", sessionId));

    }

    public void updateChatSessionWithCallingState(String sessionId, String agentId,Long agentUniqueId) {
        logger.debug("Moving Chat Session : " + sessionId + " to calling state with agent : " + agentId);
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setAgentId(agentId);
        chatSessionDetails.setAgentUniqueId(agentUniqueId);
        chatSessionDetails.setChatState(ChatStates.CALLING);
        chatSessionDetails.setStateStartTime(System.currentTimeMillis());
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);
        logger.debug(redisAgentManager.sadd("cachat:" + chatSessionDetails.getCaUserName() + ":" + agentId + ":calling-chat-sessions", sessionId));
    }

    public void saveChatSessionDegailsToRedis(String sessionId, ChatSessionDetails chatSessionDetails) {
        redisAgentManager.hset(RedisKeys.CLIENT_SESSION_CHAT_DETAILS, sessionId, convertChatDetailsToJSON(chatSessionDetails));

    }

    public int getChatCountHandledSofar(String user, String agentId) {
        return NumberUtils.toInt(redisAgentManager.hget(RedisKeys.AGENT_CHAT_SESSION_COUNTS, user + ":" + agentId), 0);

    }

    private ChatSessionDetails convertJsonToChatDetails(String jsonDetails) {
        return new Gson().fromJson(jsonDetails, ChatSessionDetails.class);
    }

    private String convertChatDetailsToJSON(ChatSessionDetails chatSessionDetails) {
        return new Gson().toJson(chatSessionDetails);
    }

    public void setCustomerEnded(String sessionId) {
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        chatSessionDetails.setCustomerEnded(true);
        saveChatSessionDegailsToRedis(sessionId, chatSessionDetails);

    }

    public ChatSessionDetails getChatSessionDetails(String sessionId) {
        return convertJsonToChatDetails(redisAgentManager.hget(RedisKeys.CLIENT_SESSION_CHAT_DETAILS, sessionId));
    }

    public void deleteChatSessionCount(String user, String agentId) {
        redisAgentManager.hdel(RedisKeys.AGENT_CHAT_SESSION_COUNTS, user + ":" + agentId);
    }

    /**
     * Gives the no.of chat sessions being handled by the agent with
     * <code>agentId</code>
     *
     * @param user username
     * @param agentId agent login id
     * @return number
     */
    public long getActiveChatSessionsCountByAgent(String user, String agentId) {
        logger.debug("Getting Active sessions for : " + user + " agent: " + agentId);
        return redisAgentManager.scard("cachat:" + user + ":" + agentId + ":chat-sessions");
    }

    public long getActiveCallingChatSessionsCountByAgent(String user, String agentId) {
        logger.debug("Getting Active Calling sessions for : " + user + " agent: " + agentId);
        return redisAgentManager.scard("cachat:" + user + ":" + agentId + ":calling-chat-sessions");
    }

    /**
     * pass customer session id to clear some fields and persist chat messages.
     *
     * @param sessionId
     */
    public void tearDownChatSession(String sessionId) {
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        beforeTearDown(sessionId);

        String user_colon_agentId = chatSessionDetails.getCaUserName() + ":" + chatSessionDetails.getAgentId();
        redisAgentManager.srem("cachat:" + user_colon_agentId + ":calling-chat-sessions", sessionId);
        redisAgentManager.srem("cachat:" + user_colon_agentId + ":chat-sessions", sessionId);
        redisAgentManager.del(getChatStoreKey(sessionId));
        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.applyPattern(RedisKeys.USER_CHAT_SESSIONS_SET);
        redisAgentManager.srem(messageFormat.format(new Object[]{chatSessionDetails.getCaUserName()}), sessionId);
        deleteSession(sessionId);

        redisAgentManager.srem(RedisKeys.FB_CHAT_SENDER_ID_SET, sessionId);//FB clean up
        redisAgentManager.hdel(RedisKeys.FB_CHAT_SENDER_ID_ACCESSTOKEN_MAP, "" + sessionId);//FB clean up

        redisAgentManager.zrem(RedisKeys.CACHAT_SESSION_LASTACTIVE_TMES, sessionId);

    }

    public void beforeTearDown(String sessionId) {
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);
        Campaign c = campaignManager.get(chatSessionDetails.getCampaignId());
        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, sessionId) && c.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
            try {
                informPlugin(sessionId, true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!StringUtils.isBlank(c.getCallbackUrl())) {
            addForCallBack(chatSessionDetails.getMonitorUcid());
        }
    }

    public void informPlugin(String sessionId, Boolean callCompleted) {
        logger.debug("Informing plugin for session Id : " + sessionId);
        ChatSessionDetails csd = getChatSessionDetails(sessionId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", csd.getMonitorUcid());
        ChatDetails cd = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params).get(0);

        Campaign c = campaignManager.get(csd.getCampaignId());
        String cachedAgentUniqId = redisAgentManager.getString(csd.getCaUserName() + ":agent:" + csd.getAgentId());
        Agent a = null;
        if (!StringUtils.isBlank(cachedAgentUniqId)) {
            a = agentManager.get(Long.valueOf(cachedAgentUniqId));
        } else {
            a = agentManager.getAgentByAgentIdV2(csd.getCaUserName(), csd.getAgentId());
        }
        StringBuilder transcript = new StringBuilder();
        if (callCompleted) {
            List<Map<String, String>> list = new ArrayList<>();
            Type t = new TypeToken<List<Map<String, String>>>() {
            }.getType();
            list = new Gson().fromJson(cd.getChat(), t);
            for (Map<String, String> mp : list) {
                transcript.append("\n");
                transcript.append(mp.get("agent").equals("true") ? "Agent : " : "Customer : ");
                transcript.append(mp.get("msg"));
                //logger.debug(transcript);
            }
        }
        Calendar fromcal = Calendar.getInstance();
        fromcal.setTime(cd.getStartDate());
        fromcal.set(Calendar.HOUR, cd.getStartTime().getHours());
        fromcal.set(Calendar.MINUTE, cd.getStartTime().getMinutes());
        fromcal.set(Calendar.SECOND, cd.getStartTime().getSeconds());

        Calendar endcal = Calendar.getInstance();
        endcal.setTime(cd.getEndDate() != null ? cd.getEndDate() : new Date());
        endcal.set(Calendar.HOUR, cd.getEndTime() != null ? cd.getEndTime().getHours() : new Date().getHours());
        endcal.set(Calendar.MINUTE, cd.getEndTime() != null ? cd.getEndTime().getMinutes() : new Date().getMinutes());
        endcal.set(Calendar.SECOND, cd.getEndTime() != null ? cd.getEndTime().getSeconds() : new Date().getSeconds());

        logger.debug("Sending to plugin start time : " + fromcal.getTime() + " | end time : " + endcal.getTime());

        UserIntegration userIntegration = userIntegrationManager.getUserIntegrationById(csd.getCaUserId(), Long.valueOf(c.getScreenPopUrl()));
        Command plugin = new PluginManager(userIntegration, redisAgentManager, appProperty.getPluginUrl(), csd.getMonitorUcid(), csd.getUcid(), "SMS", (a != null ? a.getPhoneNumber() : ""), csd.getDid(),
                csd.getChatCustId(), "", callCompleted ? "updateCallStatus" : "busyAgent", "success", c.getUser().getApiKey(),
                csd.getCaUserName(), csd.getAgentId(), callCompleted, callCompleted, fromcal.getTime(), endcal.getTime(), transcript.toString(), "answered", csd.getAgentId() != null ? "answered" : "notAnswered", c.isOffLineMode(), "chat");
        plugin.execute();

    }

    public void addForCallBack(String monitorUcid) {
        logger.debug("Adding ucid:" + monitorUcid + " for immediate chat callback.");
        redisAgentManager.addToSet(RedisKeys.CUSTOMER_CHAT_CALLBACKS_SET, monitorUcid);
    }

    public void storeFeedback(String sessionId, String feedback) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("monitorUcid", getChatSessionDetails(sessionId).getMonitorUcid());
        List<ChatDetails> list = chatDetailManager.findByNamedQuery("getLastUpdatedDetailsBySessId", params);
        if (!list.isEmpty()) {
            ChatDetails cd = list.get(0);
            cd.setFeedback(feedback);
            chatDetailManager.save(cd);
            logger.debug("Updated chat details with feedback ---->" + cd);
        }
    }

    public String getChatHistoryByPhoneNumberAndUserId(String phoneNo, Long userId) {
        Map<String, Object> params = new LinkedHashMap();
        params.put("phone_no", phoneNo);
        params.put("user_id", userId);
        List<Map<String, Object>> list = chatDetailManager.executeProcedure("call Get_Transcripts(?,?)", params);
        logger.debug("Got chat history for phone no | " + phoneNo + " | " + list);
        return new Gson().toJson(list);
    }

    public void custEndChat(String sessionId) {
        setCustomerEnded(sessionId);
        // ----> Write to db
        UpdateChatDetails_custEnd(sessionId);
        ChatSessionDetails chatSessionDetails = getChatSessionDetails(sessionId);

        if (chatSessionDetails.getChatState() == ChatStates.AGENT || chatSessionDetails.getChatState() == ChatStates.CALLING) {
            if (chatSessionDetails.getAgentId() != null) { //talking to agent
                //inform agent
                String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                logger.debug("sending message to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId() + " | ws id:" + agentWsId);
                User u = userManager.getUser(chatSessionDetails.getCaUserId().toString());

                logger.debug("Agent msg server : " + u.getUrlMap().getLocalIp());
                Token tokenResponse = TokenFactory.createToken();
                tokenResponse.setString("type", "custEnd");
                tokenResponse.setString("chatCustSessionId", sessionId);
                tokenResponse.setLong("timestamp", System.currentTimeMillis());
                //tokenServer.sendToken(tokenServer.getConnector(agentWsId), tokenResponse);
                sendTokenToMsgServer(u.getUrlMap().getLocalIp(), sessionId, tokenResponse, true);
            }
        } else {
            //teardown chat only if its not with agent, else when toolbar refresh/reconnect happens unable to fetch chat details and is not visible on agent toolbar.
            // -----> Remove all cleintid set mapped to session id.
            tearDownChatSession(sessionId);
        }
    }

    public List<String> getSkillTransferList(String did) {
        List<String> skills = new ArrayList();
        String type = "chat";
        Campaign c = campaignManager.getCampaignsByDid(did, type);
        if (c != null) {
            List<Skill> skillList = campaignManager.getCampaignSkills(c.getCampaignId());
            for (Skill skill : skillList) {
                skills.add(skill.getSkillName());
            }
        }
        return skills;
    }

    public Set<String> getAgentChatSessions(String user, String agentId) {
        return redisAgentManager.smembers("cachat:" + user + ":" + agentId + ":chat-sessions");
    }

    public Set<String> getAgentCallingChatSessions(String user, String agentId) {
        return redisAgentManager.smembers("cachat:" + user + ":" + agentId + ":calling-chat-sessions");
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setChatLogManager(GenericManager<ChatLog, Long> chatLogManager) {
        this.chatLogManager = chatLogManager;
    }

    public void setChatDetailManager(GenericManager<ChatDetails, Long> chatDetailManager) {
        this.chatDetailManager = chatDetailManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    private TokenServerLocalImpl getTokenServer() {
        if (tokenServer == null) {
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }

        return tokenServer;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setChatMsgSenderUrl(String chatMsgSenderUrl) {
        this.chatMsgSenderUrl = chatMsgSenderUrl;
    }

    public void setChatClientMsgSrvr(String chatClientMsgSrvr) {
        this.chatClientMsgSrvr = chatClientMsgSrvr;
    }

    public String getChatClientMsgSrvr() {
        return chatClientMsgSrvr;
    }

    public void setUserIntegrationManager(UserIntegrationManager userIntegrationManager) {
        this.userIntegrationManager = userIntegrationManager;
    }

    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    public void setFacebookChatService(FacebookChatServiceImpl facebookChatService) {
        this.facebookChatService = facebookChatService;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setChatTransferService(ChatTransferServiceImpl chatTransferService) {
        this.chatTransferService = chatTransferService;
    }
    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }
    private static TokenServerLocalImpl tokenServer;
    private GenericManager<ChatLog, Long> chatLogManager;
    private GenericManager<ChatDetails, Long> chatDetailManager;
    private CampaignManager campaignManager;
    private RedisAgentManager redisAgentManager;
    private static Logger logger = Logger.getLogger(ChatServiceImpl.class);
    private AgentManager agentManager;
    private EventManager eventManager;
    private String chatMsgSenderUrl;
    private String chatClientMsgSrvr;
    private UserIntegrationManager userIntegrationManager;
    private AppProperty appProperty;
    private FacebookChatServiceImpl facebookChatService;
    private UserManager userManager;
    private ChatTransferServiceImpl chatTransferService;
    private SkillManager skillManager;
}
