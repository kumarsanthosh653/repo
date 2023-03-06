/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author rajeshchary
 */
@Entity
@Table(name = "CallerHoldDetails")
public class CallHoldDetail extends BaseObject {

    private Long id;
    private BigInteger monitorUcid;
    private BigInteger ucid;
    private Long did;
    private String callerNumber;
    private Agent agent;
    private Date startTime;
    private Date endTime;
    private BigInteger UserId;
    private BigInteger campId;

    public CallHoldDetail() {
    }

    @ManyToOne(targetEntity = Agent.class)
    @JoinColumn(name = "AgentID", insertable = true)
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Column(name = "CallerNo")
    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    @Column(name = "CallID")
    public Long getDid() {
        return did;
    }

    public void setDid(Long did) {
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SeqID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "StartTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    @Column(name = "UCID")
    public BigInteger getUcid() {
        return ucid;
    }

    public void setUcid(BigInteger ucid) {
        this.ucid = ucid;
    }

    @Column(name = "monitor_ucid")
    public BigInteger getMonitorUcid() {
        return monitorUcid;
    }

    /**
     *
     * @param monitorUcid monitor monitorUcid
     */
    public void setMonitorUcid(BigInteger monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CallHoldDetail other = (CallHoldDetail) obj;
        if (this.monitorUcid != other.monitorUcid && (this.monitorUcid == null || !this.monitorUcid.equals(other.monitorUcid))) {
            return false;
        }
        if (this.did != other.did && (this.did == null || !this.did.equals(other.did))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public String toString() {
        return monitorUcid + "-" + callerNumber;
    }

    /**
     * @return the UserId
     */
    @Column(name = "UserId")
    public BigInteger getUserId() {
        return UserId;
    }

    /**
     * @param UserId the UserId to set
     */
    public void setUserId(BigInteger UserId) {
        this.UserId = UserId;
    }

    /**
     * @return the campId
     */
    @Column(name = "CampId")
    public BigInteger getCampId() {
        return campId;
    }

    /**
     * @param campId the campId to set
     */
    public void setCampId(BigInteger campId) {
        this.campId = campId;
    }

    
}
