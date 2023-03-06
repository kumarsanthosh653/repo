package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.MiniTicket;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetTicketDetailsResponse extends AgentToolbarResponse {

    private final MiniTicket miniTicket;

    public GetTicketDetailsResponse(Status status, MiniTicket miniTicket) {
        super(status);
        this.miniTicket = miniTicket;
    }

    public MiniTicket getMiniTicket() {
        return miniTicket;
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
                .append("Ticekt", miniTicket).toString();
    }

}
