package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class SetAgentModeCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public SetAgentModeCommand(String username, String agentId, String mode, String state, AgentManager agentManager) {
        super(username, agentId);
        this.mode = mode;
        this.state = state;
        this.agentManager = agentManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------
        return new AgentCommandStatus(agentManager.setAgentMode(username, agentId, mode, state));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("Mode", mode)
                .append("State", state)
                .toString();
    }
    private AgentManager agentManager;
    private final String mode;
    private final String state;

}
