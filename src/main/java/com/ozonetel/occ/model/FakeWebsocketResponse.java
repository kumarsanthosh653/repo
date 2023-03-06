package com.ozonetel.occ.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pavanj
 */
public class FakeWebsocketResponse {

    private final Map<String, Object> responseToken;

    public FakeWebsocketResponse(String utid) {
        this.responseToken = new LinkedHashMap<>();
        responseToken.put("type", "response");
        if (utid != null) {
            responseToken.put("utid", utid);
        }
    }

    public void setString(String key, String value) {
        responseToken.put(key, value);
    }

    public void setLong(String key, Long value) {
        responseToken.put(key, value);
    }

    public void setInteger(String key, Integer value) {
        responseToken.put(key, value);
    }

    public void setList(String key, List value) {
        responseToken.put(key, value);
    }

    public void setBoolean(String key, Boolean value) {
        responseToken.put(key, value);
    }

    public Map getMap() {
        return responseToken;
    }

}
