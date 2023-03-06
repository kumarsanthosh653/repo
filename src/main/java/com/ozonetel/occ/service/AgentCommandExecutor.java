package com.ozonetel.occ.service;

import com.ozonetel.occ.service.command.response.AgentToolbarResponse;

/**
 *
 * @author pavanj
 */
public interface AgentCommandExecutor {

    public <T extends AgentToolbarResponse> T executeCommand(AgentToolbarCommand<T> agentCommand);
}
