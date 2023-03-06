/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.model;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author venkatrao
 */
@Entity
@Table(name = "Preview_Data_AuditLog")
public class PreviewDataAuditLog extends BaseObject {
    
    private Long id;
    private Long pid; // preview data id
    private String phoneNumber;
    private Long agentId; // id from agent table
    private Long userId;
    private Long campaignId;
    private String UUI;
    private Date dateAdded;
    private Date dateAssigned;
    private Action action;
    private Date dateUpdated;
    private String disposition;
    private String comments;

    public PreviewDataAuditLog() {
    }

    public PreviewDataAuditLog(Long id, Long pid, String phoneNumber, Long agentId, Long userId, Long campaignId, String UUI, Date dateAdded, Date dateAssigned, Action action, Date dateUpdated, String disposition, String comments) {
        this.id = id;
        this.pid = pid;
        this.phoneNumber = phoneNumber;
        this.agentId = agentId;
        this.userId = userId;
        this.campaignId = campaignId;
        this.UUI = UUI;
        this.dateAdded = dateAdded;
        this.dateAssigned = dateAssigned;
        this.action = action;
        this.dateUpdated = dateUpdated;
        this.disposition = disposition;
        this.comments = comments;
    }
    
    public static enum Action {

        ASSIGNED, DIAL, SKIP, RESET, CLOSE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "pid")
    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    @Column(name = "phonenumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "agent_id")
    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "UUI")
    public String getUUI() {
        return UUI;
    }

    public void setUUI(String UUI) {
        this.UUI = UUI;
    }

    @Column(name = "date_added")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Column(name = "date_assigned")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Column(name = "date_updated")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Column(name = "campaign_id")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "disposition")
    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    @Column(name = "comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PreviewDataAuditLog other = (PreviewDataAuditLog) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "PreviewDataAuditLog{" + "id=" + id + ", pid=" + pid + ", phoneNumber=" + phoneNumber + ", agentId=" + agentId + ", userId=" + userId + ", campaignId=" + campaignId + ", UUI=" + UUI + ", dateAdded=" + dateAdded + ", dateAssigned=" + dateAssigned + ", action=" + action + ", dateUpdated=" + dateUpdated + ", disposition=" + disposition + ", comments=" + comments + '}';
    }
    
}
