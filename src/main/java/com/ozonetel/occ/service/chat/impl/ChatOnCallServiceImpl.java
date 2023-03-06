/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.chat.impl;

import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.impl.TokenServerLocalImpl;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.JsonUtil;
import java.text.MessageFormat;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author aparna
 */
public class ChatOnCallServiceImpl {

    public void initChatOnCall(String agentId, Long agentUniqId, String agentClientId, String callerId, String did, String skill, String monitorUcid) {
        logger.debug("Initializing chat on call for agent : " + agentId + " | " + agentUniqId + " | " + " ucid : " + monitorUcid);
        String sessionId = redisAgentManager.hget("ca:sip-sessions", callerId);

        //update ucid same as call ucid
        chatService.updateChatSessionWithMonitorucid(sessionId, monitorUcid);
        chatService.updateChatSessionWithAgent(sessionId, agentId, agentUniqId);
        chatService.addSessionState(sessionId, ChatStates.AGENT);
//        chatService.UpdateChatDetailsSystemEnd(sessionId, null, null);
        chatService.saveChatDetails(sessionId, ChatStates.AGENT, did, skill);

        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("chatOnCall");
        tokenResponse.setString("sessionId", sessionId);
        tokenResponse.setString("chatDetails", jsonUtil.convertToJson(chatSessionDetails));
//        if (tokenServer == null) {
//            tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
//        }
        tokenServer = getTokenServer();
        logger.debug(sessionId + " -->Sending CHATONCALL event to agent : " + agentId + " client id : " + agentClientId + " Token : " + tokenResponse);
        tokenServer.sendToken(tokenServer.getConnector(agentClientId), tokenResponse);

        Token clientResponse = TokenFactory.createToken();
        clientResponse.setString("type", "chaton");
        clientResponse.setString("agent", agentId);
        logger.debug(sessionId + " -->Sending new CHATONCALL event to client::" + "Chat client msg server : " + chatService.getChatClientMsgSrvr());
        chatService.sendTokenToMsgServer(chatService.getChatClientMsgSrvr(), sessionId, clientResponse, false);

    }

    public void endChatOnCall(String callerId, String hangupBy) {
        String sessionId = redisAgentManager.hget("ca:sip-sessions", callerId);
        // -> Inform client
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("endChat");
        tokenResponse.setBoolean("agentClosed", true);
        chatService.sendTokenToMsgServer(chatService.getChatClientMsgSrvr(), sessionId, tokenResponse, false);

        logger.debug("Deleting chat details after call end | callerId : " + callerId + " | sessId: " + sessionId);
        chatService.UpdateChatDetailsSystemEnd(sessionId, hangupBy, chatService.getChatJson(sessionId));
        //clear chat history
        redisAgentManager.del(chatService.getChatStoreKey(sessionId));
        MessageFormat messageFormat = new MessageFormat("");
        //clear user chat sessions
        messageFormat.applyPattern(RedisKeys.USER_CHAT_SESSIONS_SET);
        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
        redisAgentManager.srem(messageFormat.format(new Object[]{chatSessionDetails.getCaUserName()}), sessionId);
        //clear agent chat session set
        redisAgentManager.srem("cachat:" + chatSessionDetails.getCaUserName() + ":" + chatSessionDetails.getAgentId() + ":chat-sessions", sessionId);
        redisAgentManager.hdel(RedisKeys.AGENT_CHAT_SESSION_COUNTS, chatSessionDetails.getCaUserName() + ":" + chatSessionDetails.getAgentId());
        //clear chat session details
        redisAgentManager.hdel(RedisKeys.CLIENT_SESSION_CHAT_DETAILS, sessionId);
        //sip details
        redisAgentManager.hdel("ca:sip-sessions", callerId);
        redisAgentManager.srem("ca:sip-chat-sessions", sessionId);
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setChatService(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    private TokenServerLocalImpl getTokenServer() {
        if (tokenServer == null) {
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }
        return tokenServer;

    }
    private RedisAgentManager redisAgentManager;
    private ChatServiceImpl chatService;
    private static Logger logger = Logger.getLogger(ChatOnCallServiceImpl.class);
    private JsonUtil<ChatSessionDetails> jsonUtil = new JsonUtil<>();
//    private static TokenServer tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
    private static TokenServerLocalImpl tokenServer;
}
