package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.ConferenceStatus;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ConferenceCommandResponse extends AgentCommandStatus {

    private String conferenceNumber;

    public ConferenceCommandResponse(Status status, String message) {
        super(status, message);
    }

    public ConferenceCommandResponse(Status status, String message, String conferenceNumber) {
        super(status, message);
        this.conferenceNumber = conferenceNumber;
    }

    public ConferenceCommandResponse(StatusMessage statusMessage) {
        super(statusMessage);
    }

    public ConferenceCommandResponse(ConferenceStatus conferenceStatus) {
        super(conferenceStatus.getStatus(), conferenceStatus.getMessage());
        this.conferenceNumber = conferenceStatus.getConferenceNumber();
    }

    public String getConferenceNumber() {
        return conferenceNumber;
    }

    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Status", status)
                .append("Message", message)
                .append("Conference Number", conferenceNumber)
                .toString();
    }

}
