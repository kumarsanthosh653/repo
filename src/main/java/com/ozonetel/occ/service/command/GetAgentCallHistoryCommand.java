package com.ozonetel.occ.service.command;

import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.command.response.GetAgentCallHistoryResponse;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetAgentCallHistoryCommand extends AbstractAgentToolbarCommand<GetAgentCallHistoryResponse> {

    public GetAgentCallHistoryCommand(String username, String agentId, AgentManager agentManager) {
        super(username, agentId);
        this.agentManager = agentManager;
    }

    @Override
    public GetAgentCallHistoryResponse execute() {
//-------------------------------------        
        List<Report> callHist = agentManager.getAgentCallHistory(username, agentId);
        return new GetAgentCallHistoryResponse((callHist == null || callHist.isEmpty()) ? Status.ERROR : Status.SUCCESS, callHist);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .toString();
    }

    private final AgentManager agentManager;

}
