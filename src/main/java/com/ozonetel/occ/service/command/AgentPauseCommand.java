package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class AgentPauseCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public AgentPauseCommand(String username, String agentId, String pauseReason, Event.AgentMode mode, AgentManager agentManager) {
        super(username, agentId);
        this.pauseReason = pauseReason;
        this.mode = mode;
        this.agentManager = agentManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(agentManager.pauseAgent(username, agentId, pauseReason, mode));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Username", username)
                .append("AgentId", agentId)
                .append("Mode", mode)
                .append("PauseReason", pauseReason)
                .toString();
    }

    private final String pauseReason;
    private final Event.AgentMode mode;
    private final AgentManager agentManager;
}
