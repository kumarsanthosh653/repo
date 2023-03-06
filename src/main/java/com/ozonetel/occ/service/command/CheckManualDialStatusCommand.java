package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class CheckManualDialStatusCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public CheckManualDialStatusCommand(String username, String agentId, String ucid, ManualDialService manualDialService) {
        super(username, agentId);
        this.ucid = ucid;
        this.manualDialService = manualDialService;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(manualDialService.checkDialStatus(ucid));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("User", username)
                .append("AgentId", agentId)
                .append("UCID", ucid)
                .toString();
    }

    private final String ucid;
    private final ManualDialService manualDialService;
}
