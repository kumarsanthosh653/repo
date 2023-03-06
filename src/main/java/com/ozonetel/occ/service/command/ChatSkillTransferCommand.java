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
public class ChatSkillTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ChatSkillTransferCommand(String username, String agentId, Long agentUniqId, String sessionId, String clientId, String apiKey, String did, String skill, Boolean isDecline, ChatTransferServiceImpl chatTransferService) {
        super(username, agentId);
        this.sessionId = sessionId;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.did = did;
        this.skill = skill;
        this.agentUniqId = agentUniqId;
        this.chatTransferService = chatTransferService;
        this.isDecline = isDecline;
    }

    @Override
    public AgentCommandStatus execute() {
        return new AgentCommandStatus(chatTransferService.skillTransfer(agentId, agentUniqId, username, sessionId, clientId, apiKey, did, skill, isDecline));
    }

    public void setChatTransferService(ChatTransferServiceImpl chatTransferService) {
        this.chatTransferService = chatTransferService;
    }

    private ChatTransferServiceImpl chatTransferService;
    private final String sessionId;
    private final String clientId;
    private final String apiKey;
    private final String did;
    private final String skill;
    private final Long agentUniqId;
    private final Boolean isDecline;
}
