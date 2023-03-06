package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetAgentCallHistoryResponse extends AgentToolbarResponse {

    private final List<Report> agentCallHistReport;

    public GetAgentCallHistoryResponse(Status status, List<Report> agentCallHistReport) {
        super(status);
        this.agentCallHistReport = agentCallHistReport;
    }

    public List<Report> getAgentCallHistReport() {
        return agentCallHistReport;
    }

    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    public String getNs() {
        return super.getNs();
    }

    @Override
    public String getReqType() {
        return super.getReqType();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("Command", reqType).append("Status", status).append("Namespace", ns)
                .append("Call History", agentCallHistReport).toString();
    }

}
