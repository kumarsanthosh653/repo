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
public class AgentReleaseCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public AgentReleaseCommand(String username, String agentId, String releaseMessage, Event.AgentMode mode, AgentManager agentManager) {
        super(username, agentId);
        this.releaseMessage = releaseMessage;
        this.mode = mode;
        this.agentManager = agentManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(agentManager.releaseAgent(username, agentId, mode, releaseMessage));

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Username", username)
                .append("AgentId", agentId)
                .append("Mode", mode)
                .append("ReleaseMsg", releaseMessage)
                .toString();
    }

    private final String releaseMessage;
    private final Event.AgentMode mode;
    private final AgentManager agentManager;
}
