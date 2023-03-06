/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "Sms_Report")
public class SMSReport extends BaseObject implements Serializable {

    private Long id;
    private Long userId;
    private String agentId;
    private BigInteger ucid;
    private Long campaignId;
    private Date dateSent;
    private String response;
    private String message;
    private String phoneNumber;

    public SMSReport() {
    }

    public SMSReport(Long userId, String agentId, BigInteger ucid, Long campaignId, Date dateSent, String response, String message,String destination) {
        this.userId = userId;
        this.agentId = agentId;
        this.ucid = ucid;
        this.campaignId = campaignId;
        this.dateSent = dateSent;
        this.response = response;
        this.message = message;
        this.phoneNumber=destination;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SeqID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="PhoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    

    @Column(name = "UserID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "AgentID")
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Column(name = "UCID")
    public BigInteger getUcid() {
        return ucid;
    }

    public void setUcid(BigInteger ucid) {
        this.ucid = ucid;
    }

    @Column(name = "CampaignID")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "DateSent")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    @Column(name = "Response")
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Column(name = "Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.userId);
        hash = 41 * hash + Objects.hashCode(this.agentId);
        hash = 41 * hash + Objects.hashCode(this.ucid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SMSReport other = (SMSReport) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (!Objects.equals(this.agentId, other.agentId)) {
            return false;
        }
        if (!Objects.equals(this.ucid, other.ucid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SMSReport{" + "id=" + id + ", userId=" + userId + ", agentId=" + agentId + ", ucid=" + ucid + ", campaignId=" + campaignId + ", dateSent=" + dateSent + ", response=" + response + ", message=" + message + '}';
    }
}
