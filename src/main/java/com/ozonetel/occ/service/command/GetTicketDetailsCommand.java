package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TicketManager;
import com.ozonetel.occ.service.command.response.GetTicketDetailsResponse;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetTicketDetailsCommand extends AbstractAgentToolbarCommand<GetTicketDetailsResponse> {

    public GetTicketDetailsCommand(String username, String agentId, Long ticketId, TicketManager ticketManager) {
        super(username, agentId);
        this.ticketId = ticketId;
        this.ticketManager = ticketManager;
    }

    @Override
    public GetTicketDetailsResponse execute() {
//-------------------------------------          
        return new GetTicketDetailsResponse(Status.SUCCESS, ticketManager.getTicketHistory(username, ticketId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("TicketId", ticketId)
                .toString();
    }

    private final Long ticketId;
    private final TicketManager ticketManager;
}
