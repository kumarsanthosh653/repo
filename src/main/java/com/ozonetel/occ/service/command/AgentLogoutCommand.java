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
public class AgentLogoutCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public AgentLogoutCommand(String username, String agentId, String agentPhoneNumber, Event.AgentMode mode, String logoffMessage, AgentManager agentManager) {
        super(username, agentId);
        this.agentPhoneNumber = agentPhoneNumber;
        this.mode = mode;
        this.logoffMessage = logoffMessage;
        this.agentManager = agentManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(agentManager.logoutAgent(username, agentId, agentPhoneNumber, mode, logoffMessage));

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Username", username)
                .append("AgentId", agentId)
                .append("Mode", mode)
                .append("AgentNumber", agentPhoneNumber)
                .toString();
    }

    private final AgentManager agentManager;
    private final String agentPhoneNumber;
    private final Event.AgentMode mode;
    private final String logoffMessage;
}
