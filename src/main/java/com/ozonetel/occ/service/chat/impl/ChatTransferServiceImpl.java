/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.chat.impl;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.ChatAgentFinderService;
import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.impl.Participant;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ozone
 */
public class ChatTransferServiceImpl {

    public void setAgentFinderService(ChatAgentFinderService agentFinderService) {
        this.agentFinderService = agentFinderService;
    }

    public void setChatService(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    public StatusMessage skillTransfer(String fromAgent, Long fromAgentUniqId, String username, String sessionId, String clientId, String apiKey, String did, String skill, Boolean isDecline) {
        try {
            ChatSessionDetails csd = chatService.getChatSessionDetails(sessionId);
            
            boolean isTransfer = true;
            if (isDecline != null && isDecline && !csd.isIsTransferredChat()) 
                isTransfer = false;
            
            csd.setIsTransferredChat(isTransfer);
            csd.setTransferFromAgentId(fromAgent);
            chatService.saveChatSessionDegailsToRedis(sessionId, csd);
            
            clearSessionForCurrentAgent(fromAgent, fromAgentUniqId, username, sessionId, csd.getChatState());

            Long skillId = skillManager.getSkillsByUserAndSkillName(skill, username).getId();
            
            chatService.addSessionState(sessionId, ChatStates.NEXT_FREE_AGENT);
            chatService.UpdateChatDetailsSystemEnd(sessionId, null, chatService.getChatJson(sessionId), Participant.SKILL, skillId, isDecline);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    logger.debug("apiKey--" + apiKey + " clientsessionId--" + sessionId + " clientId--" + clientId + " did--" + did);
                    chatService.saveChatDetails(sessionId, ChatStates.NEXT_FREE_AGENT, did, skill);
                    logger.debug("set skillId in skillTransfer "+skillId);
                    chatService.updateChatSessionWithSkill(sessionId, skill,skillId);
                    StatusMessage statusMessage = agentFinderService.findAgent(apiKey, csd.getChatCustName(), sessionId, clientId, did, skill);
                }
            }
            ).start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new StatusMessage(Status.ERROR, e.getMessage());
        }
        return new StatusMessage(Status.SUCCESS, "queued");
    }

    public StatusMessage agentTransfer(String fromAgent, Long fromAgentUniqId, String toAgent, String username, String sessionId, String clientId, String apiKey, String did, Boolean isDecline) {

        //decrement active chat count for agent
        try {
            ChatSessionDetails csd = chatService.getChatSessionDetails(sessionId);
            csd.setIsTransferredChat(true);
            csd.setTransferFromAgentId(fromAgent);
            chatService.saveChatSessionDegailsToRedis(sessionId, csd);
            
            clearSessionForCurrentAgent(fromAgent, fromAgentUniqId, username, sessionId, csd.getChatState());
            Campaign campaign = campaignManager.getCampaignsByDid(did, "Chat");
            logger.debug("@@@@@ Got campaign as:" + campaign);
            Long campaignId = campaign.getCampaignId();
            User user = userManager.getUserByApiKey(apiKey);

            String cachedAgentUniqId = redisAgentManager.getString(user.getUsername() + ":agent:" + toAgent);
            Agent a = null;
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                a = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                a = agentManager.getAgentByAgentIdV2(user.getUsername(), toAgent);
            }

            new ChatOnCommand(csd.getMonitorUcid(), apiKey, did, campaignId, sessionId, clientId, toAgent, a.getId(), a.getClientId(), csd.getChatCustName(), user, campaign, redisAgentManager, chatService, agentManager, dispositionManager, eventManager, facebookChatService).chatOn();
            chatService.addSessionState(sessionId, ChatStates.AGENT);
            chatService.UpdateChatDetailsSystemEnd(sessionId, null, null, Participant.AGENT, a.getId(), isDecline);
            chatService.saveChatDetails(sessionId, ChatStates.AGENT, did, csd.getSkill());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new StatusMessage(Status.SUCCESS, "queued");
    }

    public void clearSessionForCurrentAgent(String agentId, Long agentUniqId, String username, String sessionId, ChatStates state) {

        agentManager.decrementAgentChatSessionsInDB(agentUniqId);
        logger.debug(redisAgentManager.srem("cachat:" + username + ":" + agentId + ":calling-chat-sessions", sessionId));
        if (state == ChatStates.AGENT) {
            redisAgentManager.srem("cachat:" + username + ":" + agentId + ":chat-sessions", sessionId);
            //Update last busy event misc. details with count of chats handled in this busy session
            if (chatService.getActiveChatSessionsCountByAgent(username, agentId) <= 0) {
                logger.debug("The last supper \uD83C\uDF72 \uD83C\uDF72 for the agent : so update event with chat coung");
                eventManager.updateChatSessionsCountInBusyEvent(username, agentUniqId, agentId, chatService.getChatCountHandledSofar(username, agentId));

            }
        }
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setFacebookChatService(FacebookChatServiceImpl facebookChatService) {
        this.facebookChatService = facebookChatService;
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    private ChatServiceImpl chatService;
    private ChatAgentFinderService agentFinderService;
    private static Logger logger = Logger.getLogger(ChatTransferServiceImpl.class);
    private CampaignManager campaignManager;
    private UserManager userManager;
    private RedisAgentManager redisAgentManager;
    private AgentManager agentManager;
    private DispositionManager dispositionManager;
    private EventManager eventManager;
    private FacebookChatServiceImpl facebookChatService;
    private SkillManager skillManager;
}
