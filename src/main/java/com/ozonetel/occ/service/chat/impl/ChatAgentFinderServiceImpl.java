package com.ozonetel.occ.service.chat.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.UserManager;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import com.ozonetel.occ.service.ChatAgentFinderService;
import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.impl.Status;
import com.ozonetel.occ.util.JsonUtil;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author pavanj
 */
public class ChatAgentFinderServiceImpl implements ChatAgentFinderService {

    @Override
    public StatusMessage findAgent(String apiKey, String chatCustName, String sessionId, String clientId, String did, String skill) {

        //FIXME change type to chat here
        Campaign campaign = campaignManager.getCampaignsByDid(did, "Chat");
        logger.debug("@@@@@ Got campaign as:" + campaign);
        Long campaignId = campaign.getCampaignId();
        User user = userManager.getUserByApiKey(apiKey);
        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);

        String monitorUcid = chatSessionDetails.getMonitorUcid();
        int chatCustLimitPerAgent = campaign.getRuleNot();

        Map<String, String> returnMap = new NextChatFreeAgent(monitorUcid, sessionId, chatCustName, did, user.getUsername(), chatCustLimitPerAgent, agentManager, callQueueManager, chatService).getNextFreeChatAgent(skill);
        if (StringUtils.equalsIgnoreCase(returnMap.get("status"), "success")) {
            String agent = returnMap.get("message");
            String agentClientId = returnMap.get("ClientID");
            String cachedAgentUniqId = redisAgentManager.getString(user.getUsername() + ":agent:" + agent);
            Agent a = null;
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                a = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                a = agentManager.getAgentByAgentIdV2(user.getUsername(), agent);
            }

            Map<String, Object> params = new LinkedHashMap();
            boolean autoAnswerEnabled = false;
            params.put("user_id", a.getUserId());
            params.put("isAdmin", 0);
            params.put("param_code", "CHAT_AUTO_ANSWER");
            List result = campaignManager.executeProcedure("call Get_UserParamterV2(?,?,?)", params);
            if (result != null && !result.isEmpty()) {
                autoAnswerEnabled = Boolean.valueOf(((Map<String, Object>) result.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) result.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) result.get(0)).get("ParameterValue").toString());
                logger.debug("is auto answer enabled --->" + autoAnswerEnabled);
            }
            if (autoAnswerEnabled) {
                new ChatOnCommand(monitorUcid, apiKey, did, campaignId, sessionId, clientId, agent, a.getId(), agentClientId, chatCustName, user, campaign, redisAgentManager, chatService, agentManager, dispositionManager, eventManager, facebookChatService).chatOn();
                chatService.addSessionState(sessionId, ChatStates.AGENT);
                chatService.UpdateChatDetailsSystemEnd(sessionId, null, null);
                chatService.saveChatDetails(sessionId, ChatStates.AGENT, did, skill);
            } else {
                logger.debug("set agentuniqueId in findAgent"+a.getId());
                chatService.updateChatSessionWithCallingState(sessionId, agent,a.getId());
                chatService.UpdateChatDetailsSystemEnd(sessionId, null, null);
                chatService.saveChatDetails(sessionId, ChatStates.CALLING, did, skill);

                //-----> Inform agent
                Token tokenResponse = TokenFactory.createToken();
                tokenResponse.setType("incomingChat");
                tokenResponse.setString("custName", chatCustName);
                tokenResponse.setString("sessionId", sessionId);
                tokenResponse.setString("monitorUcid", monitorUcid);
                tokenResponse.setString("did", did);
                tokenResponse.setLong("campaignId", campaignId);
                tokenResponse.setString("clientId", clientId);
                tokenResponse.setString("skillName", skill);
                tokenResponse.setString("currentChatHist", chatService.getChatJson(sessionId));
                tokenResponse.setString("chatDetails", jsonUtil.convertToJson(chatSessionDetails));
                logger.debug(sessionId + " -->Sending incoming chat event to agent::" + agent + "Agent msg server : " + user.getUrlMap().getLocalIp());
                chatService.sendTokenToMsgServer(user.getUrlMap().getLocalIp(), sessionId, tokenResponse, true);
            }
        } else if (StringUtils.equalsIgnoreCase(returnMap.get("status"), "fallback")) {

            chatService.addSessionState(sessionId, ChatStates.CHAT_BOT);
            chatService.UpdateChatDetailsSystemEnd(sessionId, null, null);
            chatService.saveChatDetails(sessionId, ChatStates.CHAT_BOT, did, skill);

            Token tokenResponse = TokenFactory.createToken();
            tokenResponse.setType("fallback");
            tokenResponse.setString("msg", "Sorry! No agent found this time.");
//            chatService.sendTokenToSession(sessionId, tokenResponse);
            chatService.sendTokenToMsgServer(chatService.getChatClientMsgSrvr(), sessionId, tokenResponse, false);
            try {
                chatBotService.askIvr(sessionId, "", apiKey, did, clientId, "fallback");
            } catch (Exception ex) {
                logger.debug(ex);
            }
        } else if (StringUtils.equalsIgnoreCase(returnMap.get("message"), "Client closed")) {//handle if client closes the chat
            logger.debug("Client closed:" + sessionId);

        } else {//reply to customer and delete session.
            if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, sessionId)) {
                logger.debug("--> This  is FB Chat bro...");
                facebookChatService.sendMsgToUser(sessionId, "queue_timeout", "disconnect", null);//sessionId --> FB sender ID
            } else {
                Token tokenResponse = TokenFactory.createToken();
                tokenResponse.setType("noagent");
                tokenResponse.setString("msg", "No agent found this time. Try again later.");
                chatService.sendTokenToMsgServer(chatService.getChatClientMsgSrvr(), sessionId, tokenResponse, false);
            }
            chatService.UpdateChatDetailsSystemEnd(sessionId, "System:No agent", chatService.getChatJson(sessionId));
            chatService.tearDownChatSession(sessionId);
        }
        return new StatusMessage(Status.SUCCESS, "Success");

    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setChatService(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setCallQueueManager(CallQueueManager callQueueManager) {
        this.callQueueManager = callQueueManager;
    }

    public void setChatBotService(ChatBotServiceImpl chatBotService) {
        this.chatBotService = chatBotService;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setFacebookChatService(FacebookChatServiceImpl facebookChatService) {
        this.facebookChatService = facebookChatService;
    }

    private EventManager eventManager;
    private UserManager userManager;
    private AgentManager agentManager;
    private ChatBotServiceImpl chatBotService;
    private RedisAgentManager redisAgentManager;
    private CampaignManager campaignManager;
    private ChatServiceImpl chatService;
    private DispositionManager dispositionManager;
    private CallQueueManager callQueueManager;
    private FacebookChatServiceImpl facebookChatService;
//    private String chatClientMsgSrvr;
    private static Logger logger = Logger.getLogger(ChatAgentFinderServiceImpl.class);
    private JsonUtil<ChatSessionDetails> jsonUtil = new JsonUtil<>();
}
