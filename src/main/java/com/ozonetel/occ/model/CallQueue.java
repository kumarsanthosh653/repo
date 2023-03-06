/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author rajeshchary
 */
@Entity
@Table(name = "GeneralCallQueue")
public class CallQueue extends BaseObject {

    protected CallQueuePK callQueuePK;
    private Date startTime;
    private Date endTime;
    private Long reqCount;
    private boolean isActive;
    private Long ucid;
    private Long userId;

//    private User user;
    public CallQueue() {
    }

    public CallQueue(String callerId, String did, String skillName, Date startTime, Long agentmonitorUcid, Long userId) {
        callQueuePK = new CallQueuePK(agentmonitorUcid, callerId, did, skillName);
        ucid = agentmonitorUcid;
        this.startTime = startTime;
        this.userId = userId;
    }

    @EmbeddedId
    public CallQueuePK getCallQueuePK() {
        return callQueuePK;
    }

    public void setCallQueuePK(CallQueuePK callQueuePK) {
        this.callQueuePK = callQueuePK;
    }

    /**
     * @return the startTime
     */
    @Column(name = "StartTime")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    @Column(name = "EndTime")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return this.callQueuePK.getSkillName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CallQueue)) {
            return false;
        }

        final CallQueue cq = (CallQueue) o;

        return !(callQueuePK != null ? !callQueuePK.equals(cq.callQueuePK) : cq.callQueuePK != null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @return the reqCount
     */
    @Column(name = "RequestCounter")
    public Long getReqCount() {
        return reqCount;
    }

    /**
     * @param reqCount the reqCount to set
     */
    public void setReqCount(Long reqCount) {
        this.reqCount = reqCount;
    }

    /**
     * @return the isActive
     */
    @Column(name = "ISActive")
    public boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

////    @ManyToOne(targetEntity = User.class)
////    @JoinColumn(name = "UserID", insertable = false, updatable = false)
//    
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
    @Column(name = "UserID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
