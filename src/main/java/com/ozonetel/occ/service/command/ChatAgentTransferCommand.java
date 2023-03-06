/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.chat.impl.ChatTransferServiceImpl;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;

/**
 *
 * @author ozone
 */
public class ChatAgentTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ChatAgentTransferCommand(String username, String fromAgent, Long fromAgentUniqueId, String toAgent, String sessionId, String clientId, String apiKey, String did, Boolean isDecline, ChatTransferServiceImpl chatTransferService) {
        super(username, fromAgent);
        this.sessionId = sessionId;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.did = did;
        this.fromAgentUniqueId = fromAgentUniqueId;
        this.toAgent = toAgent;
        this.chatTransferService = chatTransferService;
        this.isDecline = isDecline;
    }

    @Override
    public AgentCommandStatus execute() {
//        log.debug(agentId);
//        log.debug(fromAgentUniqueId);
//        log.debug(toAgent);
//        log.debug(username);
//        log.debug(sessionId);
//        log.debug(clientId);
//        log.debug(apiKey);
//        log.debug(did);
        return new AgentCommandStatus(chatTransferService.agentTransfer(agentId, fromAgentUniqueId, toAgent, username, sessionId, clientId, apiKey, did, isDecline));
    }

    public void setChatTransferService(ChatTransferServiceImpl chatTransferService) {
        this.chatTransferService = chatTransferService;
    }

    private ChatTransferServiceImpl chatTransferService;
    private final String sessionId;
    private final String clientId;
    private final String apiKey;
    private final String did;
    private final Long fromAgentUniqueId;
    private final String toAgent;
    private final Boolean isDecline;
}
