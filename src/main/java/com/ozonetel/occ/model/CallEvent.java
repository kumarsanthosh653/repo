package com.ozonetel.occ.model;

import java.util.List;

/**
 *
 * @author pavanj
 */
public class CallEvent {

    private String monitorUcid;
    private String ucid;
    private User user;
    private List<Report> reports;
    private Campaign campaign;
    private String callStatus;
    private Integer callBackTries;
    private String agentId;

    public CallEvent() {
    }

    public CallEvent(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public CallEvent(String monitorUcid, String ucid, User user, List<Report> reports, Campaign campaign, String callStatus, Integer callBackTries, String agentId) {
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.user = user;
        this.reports = reports;
        this.campaign = campaign;
        this.callStatus = callStatus;
        this.callBackTries = callBackTries;
        this.agentId = agentId;
    }

    public String getMonitorUcid() {
        return monitorUcid;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public Integer getCallBackTries() {
        return callBackTries;
    }

    public void setCallBackTries(Integer callBackTries) {
        this.callBackTries = callBackTries;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String toString() {
        return "CallEvent{" + "monitorUcid=" + monitorUcid + ", ucid=" + ucid + ", user=" + user + ", reports=" + reports + ", campaign=" + campaign + ", callStatus=" + callStatus + ", callBackTries=" + callBackTries + ", agentId=" + agentId + '}';
    }

}
