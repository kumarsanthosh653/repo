package com.ozonetel.occ.model;

import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ConferenceStatus extends StatusMessage {

    private String conferenceNumber;

    public ConferenceStatus(Status status, String message) {
        super(status, message);
    }

    public ConferenceStatus(Status status, String message, String conferenceNumber) {
        super(status, message);
        this.conferenceNumber = conferenceNumber;
    }

    public String getConferenceNumber() {
        return conferenceNumber;
    }

    public void setConferenceNumber(String conferenceNumber) {
        this.conferenceNumber = conferenceNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("Status", status).
                append("message", message).
                append("ConferenceNumber", conferenceNumber).
                toString();
    }

}
