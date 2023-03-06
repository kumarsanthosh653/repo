package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetTransferAgentListResponse extends AgentToolbarResponse {

    private List<Agent> agentList;

    public GetTransferAgentListResponse(Status status) {
        super(status);
    }

    public GetTransferAgentListResponse(Status status, List<Agent> transferAgentList) {
        super(status);
        this.agentList = transferAgentList;
    }

    public List<Agent> getAgentList() {
        return agentList;
    }

    public void setAgentList(List<Agent> agentList) {
        this.agentList = agentList;
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
                .append("Transfer Agents", agentList).toString();
    }

}
