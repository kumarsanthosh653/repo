package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.MiniTicket;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetTicketsByPhoneResponse extends AgentToolbarResponse {

    private List<MiniTicket> ticketList;

    public GetTicketsByPhoneResponse(Status status) {
        super(status);
    }

    public GetTicketsByPhoneResponse(Status status, List<MiniTicket> ticketList) {
        super(status);
        this.ticketList = ticketList;
    }

    public List<MiniTicket> getTicketList() {
        return ticketList;
    }

    @Override
    public String getReqType() {
        return reqType;
    }

    @Override
    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    @Override
    public String getNs() {
        return ns;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("Command", reqType).append("Status", status).append("Namespace", ns)
                .append("Tickets", ticketList).toString();
    }

}
