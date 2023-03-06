package com.ozonetel.occ.model;

/**
 *
 * @author pavanj
 */
public class AgentCallDropEvent implements AgentEvent{

    private String callStatus;
    private String dialStatus;
    private String ucid;
    private String agentMonitorUcid;
    private String callType;

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getDialStatus() {
        return dialStatus;
    }

    public void setDialStatus(String dialStatus) {
        this.dialStatus = dialStatus;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public String getAgentMonitorUcid() {
        return agentMonitorUcid;
    }

    public void setAgentMonitorUcid(String agentMonitorUcid) {
        this.agentMonitorUcid = agentMonitorUcid;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    @Override
    public String toString() {
        return "AgentDropCallEvent{" + "callStatus=" + callStatus + ", dialStatus=" + dialStatus + ", ucid=" + ucid + ", agentMonitorUcid=" + agentMonitorUcid + ", callType=" + callType + '}';
    }

}
