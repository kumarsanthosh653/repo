/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "AgentCallQueue")
public class AgentCallQueue implements Serializable {

    private Long callID;
    private String callerID;
    private String did;
    private String skillName;
//    private User user;
//    private Campaign campaign;
    private String agent;
    private String phoneName;
    private Date startTime;
    private Date endTime;
    private boolean active;
    private Long userId;

    @Column(name = "ISActive")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "Agent")
    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Id
    @Column(name = "CallID")
    public Long getCallID() {
        return callID;
    }

    public void setCallID(Long callID) {
        this.callID = callID;
    }

    @Column(name = "CallerID")
    public String getCallerID() {
        return callerID;
    }

    public void setCallerID(String callerID) {
        this.callerID = callerID;
    }

//    @ManyToOne(targetEntity = Campaign.class)
//    @JoinColumn(name = "CampaignID")
//    public Campaign getCampaign() {
//        return campaign;
//    }
//
//    public void setCampaign(Campaign campaign) {
//        this.campaign = campaign;
//    }
    @Column(name = "PhoneName")
    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    @Column(name = "SkillName")
    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Column(name = "StartTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

//    @ManyToOne(targetEntity = User.class)
//    @JoinColumn(name = "UserID")
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
    @Column(name = "CalledNo")
    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    @Column(name = "EndTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "UserID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgentCallQueue other = (AgentCallQueue) obj;
        if (this.callID != other.callID && (this.callID == null || !this.callID.equals(other.callID))) {
            return false;
        }
        if ((this.callerID == null) ? (other.callerID != null) : !this.callerID.equals(other.callerID)) {
            return false;
        }
        if ((this.skillName == null) ? (other.skillName != null) : !this.skillName.equals(other.skillName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.callID != null ? this.callID.hashCode() : 0);
        hash = 97 * hash + (this.callerID != null ? this.callerID.hashCode() : 0);
        hash = 97 * hash + (this.skillName != null ? this.skillName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "AgentCallQueue{" + "callID=" + callID + ", callerID=" + callerID + ", skillName=" + skillName + ",  agent=" + agent + ", phoneName=" + phoneName + ", startTime=" + startTime + ", active=" + active + '}';
    }
}
