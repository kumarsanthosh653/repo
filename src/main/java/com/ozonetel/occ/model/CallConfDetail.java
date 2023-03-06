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
@Table(name = "CallerConfDetails")
public class CallConfDetail extends BaseObject {

    private Long id;
    private BigInteger ucid;
    private String did;
    private Agent confCreator;
    private Date startTime;
    private Agent agentParticipant;
    private FwpNumber phoneParticipant;
    private String otherParticipant;
    private String dialStatus;
    private Date pickUpTime;
    private String exitStatus;
    private Date endTime;
    private Long userId;
    private String aduioFile;

    @Override
    public int hashCode() {
        int hash = 7;
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
        final CallConfDetail other = (CallConfDetail) obj;
        if (this.ucid != other.ucid && (this.ucid == null || !this.ucid.equals(other.ucid))) {
            return false;
        }
        if (this.did != other.did && (this.did == null || !this.did.equals(other.did))) {
            return false;
        }
        return true;
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

    /**
     * @return the ucid
     */
    @Column(name = "UCID")
    public BigInteger getUcid() {
        return ucid;
    }

    /**
     * @param ucid the ucid to set
     */
    public void setUcid(BigInteger ucid) {
        this.ucid = ucid;
    }

    @Column(name = "UserID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the did
     */
    @Column(name = "CallID")
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
     * @return the confCreator
     */
    @ManyToOne(targetEntity = Agent.class)
    @JoinColumn(name = "ConfCreatorAgentID")
    public Agent getConfCreator() {
        return confCreator;
    }

    /**
     * @param confCreator the confCreator to set
     */
    public void setConfCreator(Agent confCreator) {
        this.confCreator = confCreator;
    }

    /**
     * @return the startTime
     */
    @Column(name = "StartTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
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
     * @return the agentParticipant
     */
    @ManyToOne(targetEntity = Agent.class)
    @JoinColumn(name = "ConfParticipantAgentID")
    public Agent getAgentParticipant() {
        return agentParticipant;
    }

    /**
     * @param agentParticipant the agentParticipant to set
     */
    public void setAgentParticipant(Agent agentParticipant) {
        this.agentParticipant = agentParticipant;
    }

    /**
     * @return the phoneParticipant
     */
    @ManyToOne(targetEntity = FwpNumber.class)
    @JoinColumn(name = "ConfParticipantPhoneID")
    public FwpNumber getPhoneParticipant() {
        return phoneParticipant;
    }

    /**
     * @param phoneParticipant the phoneParticipant to set
     */
    public void setPhoneParticipant(FwpNumber phoneParticipant) {
        this.phoneParticipant = phoneParticipant;
    }

    /**
     * @return the otherParticipant
     */
    @Column(name = "ConfCreatorPhoneNo")
    public String getOtherParticipant() {
        return otherParticipant;
    }

    /**
     * @param otherParticipant the otherParticipant to set
     */
    public void setOtherParticipant(String otherParticipant) {
        this.otherParticipant = otherParticipant;
    }

    /**
     * @return the dialStatus
     */
    @Column(name = "DialStatus")
    public String getDialStatus() {
        return dialStatus;
    }

    /**
     * @param dialStatus the dialStatus to set
     */
    public void setDialStatus(String dialStatus) {
        this.dialStatus = dialStatus;
    }

    /**
     * @return the pickUpTime
     */
    @Column(name = "PickupTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getPickUpTime() {
        return pickUpTime;
    }

    /**
     * @param pickUpTime the pickUpTime to set
     */
    public void setPickUpTime(Date pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    /**
     * @return the exitStatus
     */
    @Column(name = "ExitStatus")
    public String getExitStatus() {
        return exitStatus;
    }

    /**
     * @param exitStatus the exitStatus to set
     */
    public void setExitStatus(String exitStatus) {
        this.exitStatus = exitStatus;
    }

    /**
     * @return the endTime
     */
    @Column(name = "EndTime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "AudioFile")
    public String getAduioFile() {
        return aduioFile;
    }

    public void setAduioFile(String aduioFile) {
        this.aduioFile = aduioFile;
    }

    @Override
    public String toString() {
        return this.ucid + "-" + this.dialStatus;
    }
}
