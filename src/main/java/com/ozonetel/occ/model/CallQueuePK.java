/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 *
 * @author satishadireddy
 */
@Embeddable
public class CallQueuePK extends BaseObject implements Serializable {

    private Long ucid;
    private String callerId;
    private String did;
    private String skillName;
//    private User user;

    public CallQueuePK() {
    }

    public CallQueuePK(Long ucid, String callerId, String did, String skillName) {
        this.ucid = ucid;
        this.callerId = callerId;
        this.did = did;
        this.skillName = skillName;
//        this.user = user;
    }

    @Column(name = "CallID")
    public Long getUcid() {
        return ucid;
    }

    public void setUcid(Long ucid) {
        this.ucid = ucid;
    }

    /**
     * @return the caller_id
     */
    @Column(name = "CallerID")
    public String getCallerId() {
        return callerId;
    }

    /**
     * @param caller_id the caller_id to set
     */
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    /**
     * @return the did
     */
    @Column(name = "CalledNo")
    public String getDid() {
        return did;
    }

    /**
     * @param did the did to set
     */
    public void setDid(String did) {
        this.did = did;
    }

    /**
     * @return the skillName
     */
    @Column(name = "SkillName")
    public String getSkillName() {
        return skillName;
    }

    /**
     * @param skillName the skillName to set
     */
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

      /**
     * @return the user
     */
//    @ManyToOne(targetEntity = User.class)
//    @JoinColumn(name = "UserID", insertable = true, updatable = true)
//    
//    @Transient
//    public User getUser() {
//        return user;
//    }
//
//    /**
//     * @param user the user to set
//     */
//    public void setUser(User user) {
//        this.user = user;
//    }
    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
