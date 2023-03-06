package com.ozonetel.occ.service.chat.impl;

import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.RedisAgentManager;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author pavanj
 */
public class ChatOffCommand {

    public ChatOffCommand(EventManager _eventManager, AgentManager agentManager, ChatServiceImpl chatService, RedisAgentManager redisAgentManager, String sessionId, String user, String agentId, Long agentUniqId, boolean isAgentClosed, String chatClientMsgSrvr, FacebookChatServiceImpl facebookChatService) {
        this.chatService = chatService;
        this.redisAgentManager = redisAgentManager;
        this.sessionId = sessionId;
        this.user = user;
        this.agentId = agentId;
        this.isAgentClosed = isAgentClosed;
        this.agentManager = agentManager;
        this.eventManager = _eventManager;
        this.chatClientMsgSrvr = chatClientMsgSrvr;
        this.agentUniqId = agentUniqId;
        this.facebookChatService = facebookChatService;
    }

    public boolean chatOff() {
        boolean lastSupper = false;
        LOGGER.debug("Chat off called:" + this);

        LOGGER.debug("Sending end chat to client:" + sessionId);

        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, sessionId)) {
            LOGGER.debug("--> This  is FB Chat bro...");
            facebookChatService.sendMsgToUser(sessionId, "agent_disconnected", "disconnect", null);//sessionId --> FB sender ID
        } else {
            Token tokenResponse = TokenFactory.createToken();
            tokenResponse.setType("endChat");
            tokenResponse.setBoolean("agentClosed", this.isAgentClosed);
            // -> Inform client
            chatService.sendTokenToMsgServer(chatClientMsgSrvr, sessionId, tokenResponse, false);
        }
        try {
            agentManager.decrementAgentChatSessionsInDB(agentUniqId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        chatService.tearDownChatSession(sessionId);

        if (chatService.getActiveChatSessionsCountByAgent(user, agentId) <= 0) {
            LOGGER.debug("The last supper \uD83C\uDF72 \uD83C\uDF72 for the agent : so update event with chat coung");
            lastSupper = true;
            eventManager.updateChatSessionsCountInBusyEvent(user, agentUniqId, agentId, chatService.getChatCountHandledSofar(user, agentId));

        }
        return lastSupper;
    }

    @Override
    public String toString() {
        return "ChatOffCommand{" + "sessionId=" + sessionId + '}';
    }

    private ChatServiceImpl chatService;
    private boolean isAgentClosed;
    private static final Logger LOGGER = Logger.getLogger(ChatOffCommand.class);
    private final RedisAgentManager redisAgentManager;
    private final String sessionId;
    private final String user;
    private final String agentId;
    private AgentManager agentManager;
    private EventManager eventManager;
    private String chatClientMsgSrvr;
    private Long agentUniqId;
    private FacebookChatServiceImpl facebookChatService;
}
