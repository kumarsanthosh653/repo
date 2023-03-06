package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.GetTransferAgentListResponse;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Returns all the idle agents irrespective of their skills/campaigns.
 * @author pavanj
 */
public class GetTransferAgentListCommand extends AbstractAgentToolbarCommand<GetTransferAgentListResponse> {

    public GetTransferAgentListCommand(String username,Long agentUniqueId, String agentId, AgentManager agentManager) {
        super(username,agentUniqueId);
        this.agentManager = agentManager;
    }

    @Override
    public GetTransferAgentListResponse execute() {
//-------------------------------------        
        return new GetTransferAgentListResponse(Status.SUCCESS, agentManager.getTransferAgentList(username,agentUniqueId));
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentUniqId", agentUniqueId)
                .toString();
    }


    private AgentManager agentManager;
}
