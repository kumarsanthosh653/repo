package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.GetTransferNumberListResponse;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetTransferNumberListCommand extends AbstractAgentToolbarCommand<GetTransferNumberListResponse> {

    public GetTransferNumberListCommand(String username, String agentId, TransferNumberManager transferNumberManager) {
        super(username, agentId);
        this.transferNumberManager = transferNumberManager;
    }

    @Override
    public GetTransferNumberListResponse execute() {
//-------------------------------------        
        return new GetTransferNumberListResponse(Status.SUCCESS, transferNumberManager.getTransferNumbersByUser(username));
    }

     @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .toString();
    }

    private final TransferNumberManager transferNumberManager;
}
