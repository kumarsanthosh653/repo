package com.ozonetel.occ.service.chat.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.util.JsonUtil;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author pavanj
 *
 * Sends agent that chat has initiated.
 */
public class ChatOnCommand {

    public ChatOnCommand(String _monitorUcid, String _apiKey, String _did, Long _campaignId, String _sessionId, String _clientId, String _agentId, Long agentUniqId, String _agentWsId, String _chatCustName, User _user, Campaign _campaign, RedisAgentManager _redisAgentManager, ChatServiceImpl _chatService, AgentManager _agentManager, DispositionManager _disDispositionManager, EventManager _evEventManager, FacebookChatServiceImpl _facebookChatService) {
        this.apiKey = _apiKey;
        this.sessionId = _sessionId;
        this.clientId = _clientId;
        this.user = _user;
        this.redisAgentManager = _redisAgentManager;
        this.agentId = _agentId;
        this.agentWsId = _agentWsId;
        this.chatService = _chatService;
        this.chatCustName = _chatCustName;
        this.campaignId = _campaignId;
        this.did = _did;
        this.agentManager = _agentManager;
        this.dispositionManager = _disDispositionManager;
        this.eventManager = _evEventManager;
        this.monitorUcid = _monitorUcid;
        this.agentUniqId = agentUniqId;
        this.campaign = _campaign;
        this.facebookChatService = _facebookChatService;
    }

    public boolean chatOn() {
        logger.debug(this.toString());

        if (user == null) {
            return false;
        }

//        chatService.setUpChatSession(sessionId, user.getUsername(), agentId, clientId, "" + campaignId, did, chatCustName);
        chatService.updateChatSessionWithAgent(sessionId, agentId, agentUniqId);

        List<Disposition> dispositions = dispositionManager.getDispositionsByCampaign(campaignId);
        boolean getMoreDispositions = false;
        
        Map<String, String> dispositionMap = new LinkedHashMap<>();
        for (Disposition disposition : dispositions) {
            //dispositionNames += (dispositionNames != null && !dispositionNames.equals("") ? "," : "") + (disposition.isActive() ? disposition.getReason() : "");
            if (StringUtils.isNotBlank(disposition.getReason())) {
                dispositionMap.put(disposition.getReason(), disposition.getReason());
                
                if(dispositionMap.size()>200){
                    getMoreDispositions = true;
                    logger.debug("dispositions count exceeded.."+sessionId);
                    break;
                }
            }
        }
        
        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);

        //-----> Inform agent
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("chatonmulti");
        tokenResponse.setString("custName", chatCustName);
        tokenResponse.setString("sessionId", sessionId);
        tokenResponse.setString("dispMap", new Gson().toJson(dispositionMap));
        tokenResponse.setBoolean("getMoreDispositions", getMoreDispositions);
        tokenResponse.setString("screenPop", campaign.getScreenPopUrl());
        tokenResponse.setString("currentChatHist", chatService.getChatJson(sessionId));
        tokenResponse.setString("previousChatHist", chatService.getChatHistoryByPhoneNumberAndUserId(chatSessionDetails.getPhoneNumber(), user.getId()));
        tokenResponse.setString("chatDetails", jsonUtil.convertToJson(chatSessionDetails));
        tokenResponse.setString("campaignName", campaign.getCampignName());

        logger.info("****Chat sessions for user:" + user.getUsername() + " | agent:" + agentId + "|->" + chatService.getActiveChatSessionsCountByAgent(user.getUsername(), agentId));

        if (chatService.getActiveChatSessionsCountByAgent(user.getUsername(), agentId) == 1) {//if this is the fisrst chat make agent busy in the backend.
            logger.debug("Making agent busy");
            Agent agent = agentManager.get(agentUniqId);
            eventManager.logEvent("Chat", agent.getUserId(), user.getUsername(), agentUniqId, agent.getAgentId(), agent.getMode(), new Date(), Long.valueOf(monitorUcid), null, "Chat");
            agent.setState(Agent.State.BUSY);
            agent.setIdleTime(System.currentTimeMillis());
            agentManager.save(agent);
            eventManager.logEvent("Busy", user.getId(), user.getUsername(), agentUniqId, agent.getAgentId(), agent.getMode(), new Date(), Long.valueOf(monitorUcid), null, "Chat");
            String existingString = redisAgentManager.hget(user.getUsername() + ":agent:events", agentId);
            Map busyMap = new Gson().fromJson(existingString, LinkedHashMap.class);
            logger.debug(busyMap);
            busyMap.put("inchat", true);
            redisAgentManager.hset(user.getUsername() + ":agent:events", agentId, new Gson().toJson(busyMap));

        }

        logger.debug(sessionId + " -->Sending new chat event to agent::" + agentId + "Agent msg server : " + user.getUrlMap().getLocalIp());
        chatService.sendTokenToMsgServer(user.getUrlMap().getLocalIp(), sessionId, tokenResponse, true);

        Token clientResponse = TokenFactory.createToken();
        clientResponse.setString("type", "chaton");
        clientResponse.setString("agent", agentId);
        logger.debug(sessionId + " -->Sending new chat event to client::" + "Chat client msg server : " + chatService.getChatClientMsgSrvr());
        chatService.sendTokenToMsgServer(chatService.getChatClientMsgSrvr(), sessionId, clientResponse, false);

        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, sessionId)) {
            logger.debug("--> This  is FB Chat bro...");
            facebookChatService.sendMsgToUser(sessionId, agentId, "agentConnected", null);//sessionId --> FB sender ID
            if (campaign.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
                try {
                    chatService.informPlugin(sessionId, false);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChatOnCommand{" + "apiKey=" + apiKey + ", sessionId=" + sessionId + ", clientId=" + clientId + ", agentId=" + agentId + ", agentWsId=" + agentWsId + ", chatCustName=" + chatCustName + '}';
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    private ChatServiceImpl chatService;
    private static Logger logger = Logger.getLogger(ChatOnCommand.class);
    private final String apiKey;
    private final String did;
    private final Long campaignId;
    private final String sessionId;
    private final String clientId;
    private final User user;
    private final RedisAgentManager redisAgentManager;
    private final String agentId;
    private final String agentWsId;
    private final String chatCustName;
    private AgentManager agentManager;
    private DispositionManager dispositionManager;
    private EventManager eventManager;
    private String monitorUcid;
    private Long agentUniqId;
    private Campaign campaign;
    private JsonUtil<ChatSessionDetails> jsonUtil = new JsonUtil<>();
    private FacebookChatServiceImpl facebookChatService;
//    private String chatClientMsgSrvr;
}
