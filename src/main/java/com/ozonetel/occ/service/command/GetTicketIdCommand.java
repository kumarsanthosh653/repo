package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetTicketIdCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public GetTicketIdCommand(String username, String agentId, Long ucid, ReportManager reportManager) {
        super(username, agentId);
        this.ucid = ucid;
        this.reportManager = reportManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(Status.SUCCESS, "" + reportManager.getReportIdByUcid(ucid));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("UCID", ucid)
                .toString();
    }
    
    private final Long ucid;
    private final ReportManager reportManager;
}
