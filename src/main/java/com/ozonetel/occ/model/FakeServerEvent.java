package com.ozonetel.occ.model;

/**
 *
 * @author pavanj
 */
public class FakeServerEvent {

    private String sessionId;
    private String id;

    public FakeServerEvent(String sessionId, String id) {
        this.sessionId = sessionId;
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getConnectorCount() {
        return 0;
    }

}
