/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.ManyToOne;

/**
 *
 * @author aparna
 */
public class PredictiveCallMonitor {

    private BigInteger ucid;
    private String agentSelected;
    private String callStatus;
    private String agentStatus;
//    private PreviewData previewData;
    private String name;
    private String phone;
    private Long campaignId;
    private Date dateModified;
//    private FwpNumber fwpNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    
    public BigInteger getUcid() {
        return ucid;
    }

    public void setUcid(BigInteger ucid) {
        this.ucid = ucid;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(String agentStatus) {
        this.agentStatus = agentStatus;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

//    public FwpNumber getFwpNumber() {
//        return fwpNumber;
//    }
//
//    public void setFwpNumber(FwpNumber fwpNumber) {
//        this.fwpNumber = fwpNumber;
//    }

    public String getAgentSelected() {
        return agentSelected;
    }

    public void setAgentSelected(String agentSelected) {
        this.agentSelected = agentSelected;
    }

    @Override
    public String toString() {
        return "PredictiveCallMonitor{" + "ucid=" + ucid + ", agentSelected=" + agentSelected + ", callStatus=" + callStatus + ", agentStatus=" + agentStatus + ", name=" + name + ", phone=" + phone + ", campaignId=" + campaignId + ", dateModified=" + dateModified + '}';
    }

}
