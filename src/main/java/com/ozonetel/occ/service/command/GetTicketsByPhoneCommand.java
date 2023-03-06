package com.ozonetel.occ.service.command;

import com.ozonetel.occ.model.MiniTicket;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TicketManager;
import com.ozonetel.occ.service.command.response.GetTicketsByPhoneResponse;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetTicketsByPhoneCommand extends AbstractAgentToolbarCommand<GetTicketsByPhoneResponse> {

    public GetTicketsByPhoneCommand(String username, String agentId, String customerNumber, TicketManager ticketManager) {
        super(username, agentId);
        this.customerNumber = customerNumber;
        this.ticketManager = ticketManager;
    }

    @Override
    public GetTicketsByPhoneResponse execute() {
//-------------------------------------         
        List<MiniTicket> ticketList = ticketManager.getTicketsByCustomerNumber(username, customerNumber);
        return new GetTicketsByPhoneResponse((ticketList == null || ticketList.isEmpty()) ? Status.ERROR : Status.SUCCESS, ticketList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("CustomerNumber", customerNumber)
                .toString();
    }

    private final String customerNumber;
    private final TicketManager ticketManager;
}
