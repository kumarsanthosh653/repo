package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.service.impl.Status;

/**
 *
 * @author pavanj
 */
public class AgentToolbarResponse {

    protected Status status;
    /**
     * Request type/ AgentToolbar command
     */
    public String reqType;
    /**
     * Name space
     */
    protected String ns;

    protected String callbackfn;
    protected String callbackArgs;

    public AgentToolbarResponse(Status status) {
        this.status = status;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCallbackfn() {
        return callbackfn;
    }

    public void setCallbackfn(String callbackfn) {
        this.callbackfn = callbackfn;
    }

    public String getCallbackArgs() {
        return callbackArgs;
    }

    public void setCallbackArgs(String callbackArgs) {
        this.callbackArgs = callbackArgs;
    }

}
