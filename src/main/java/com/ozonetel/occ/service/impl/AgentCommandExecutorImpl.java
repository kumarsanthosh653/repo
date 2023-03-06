package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.service.AgentCommandExecutor;
import com.ozonetel.occ.service.AgentToolbarCommand;
import com.ozonetel.occ.service.command.response.AgentToolbarResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class AgentCommandExecutorImpl implements AgentCommandExecutor {

    private static final Logger logger = Logger.getLogger(AgentCommandExecutorImpl.class);

    @Override
    public <T extends AgentToolbarResponse> T executeCommand(AgentToolbarCommand<T> agentCommand) {
        if (agentCommand == null) {
            throw new IllegalArgumentException("Command can't be null.");
        }
        logger.debug("--> Command:" + agentCommand);

        T response = agentCommand.execute();
        logger.debug("-->" + agentCommand + "" + "Command:" + agentCommand + " || Response:" + response);

        return response;
    }
}
