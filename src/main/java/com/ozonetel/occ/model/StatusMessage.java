package com.ozonetel.occ.model;

import com.ozonetel.occ.service.impl.Status;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pavanj
 */
public class StatusMessage {

    protected Status status;

    protected String message;

    public StatusMessage() {
    }

    public StatusMessage(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getAsMap() {
        Map<String, Object> responseMap = new HashMap<>(2);
        responseMap.put("status", status);
        responseMap.put("message", message);
        return responseMap;
    }

    @Override
    public String toString() {
        return "StatusMessage{" + "status=" + status + ", message=" + message + '}';
    }

    public String toOneString() {
        return status.toReadableString() + ":" + message;
    }

}
