package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class AgentCommandStatus extends AgentToolbarResponse {

    protected String message;

    public AgentCommandStatus(StatusMessage statusMessage) {
        super(statusMessage.getStatus());
        this.message = statusMessage.getMessage();
    }

    public AgentCommandStatus(Status status, String message) {
        super(status);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("command", reqType).append("Namespace", ns).append("Status", status).append("Message", message).toString();
    }

}
