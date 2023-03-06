/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ozonetel.occ.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aparna
 */
public class CallCompletedEvent {

    private String monitorUcid;
    private String user;
    private Long campaignId;
    private Date dateAdded;
    private List<Map<String, Object>> callbackActions;

    public String getMonitorUcid() {
        return monitorUcid;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Map<String, Object>> getCallbackActions() {
        return callbackActions;
    }

    public void setCallbackActions(List<Map<String, Object>> callbackActions) {
        this.callbackActions = callbackActions;
    }

}
