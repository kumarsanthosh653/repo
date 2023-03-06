package com.ozonetel.occ.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PreviewData.java
 *
 * @author rajeh Date : Oct 28, 2009 Email : rajesh@ozonetel.com,
 * nb.nalluri@yahoo.com
 */
@Entity
@Table(name = "preview_data")
public class PreviewData extends BaseObject {

    private Long id;
    private String name;
    private String phoneNumber;
    private String Status;
    private Campaign campaign;
    private Date createDate;
    private Agent agent;
    private boolean isDone;
    private boolean nextFlag;
    private Date lastSelected;
    private Long ucid;
    private String dialMessage;
    private String disposition;
    private Date dateUpdated;
    private Long currentTrail;
    //    private Agent agentSelected;
    private Integer priority;
    private boolean deleted;
    private String comments;

    public PreviewData() {
    }

    public PreviewData(Long id, String name, String phoneNumber, Date createDate) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createDate = createDate;
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

    @Column(name = "is_delete")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "date_updated")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Column(name = "current_trail")
    public Long getCurrentTrail() {
        return currentTrail;
    }

    public void setCurrentTrail(Long currentTrail) {
        this.currentTrail = currentTrail;
    }

    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the phoneNumber
     */
    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Column(name = "date_created")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @ManyToOne(targetEntity = Campaign.class)
    @JoinColumn(name = "campaign_id", insertable = true, updatable = true)
    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PreviewData other = (PreviewData) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.phoneNumber, other.phoneNumber)) {
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
        return new ToStringBuilder(this).append("id", id).
                append("dialed", nextFlag).
                append("status", Status).
                append("Number", phoneNumber).
                append("Name", name).
                append("dateUpdated", dateUpdated).
                toString();
    }

    public String toLongString() {
        return new ToStringBuilder(this).append("id", id).
                append("dialed", nextFlag).
                append("status", Status).
                append("Number", phoneNumber).
                append("Name", name).
                append("dateUpdated", dateUpdated).
                toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the agent
     */
    @ManyToOne(targetEntity = Agent.class, optional = true)
    @JoinColumn(name = "agent_id", nullable = true)
    public Agent getAgent() {
        return agent;
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    /**
     * @return the isDone
     */
    @Column(name = "is_done")
    public boolean isIsDone() {
        return isDone;
    }

    /**
     * @param isDone the isDone to set
     */
    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * @return the nextFlag
     */
    @Column(name = "next_flag")
    public boolean isNextFlag() {
        return nextFlag;
    }

    /**
     * @param nextFlag the nextFlag to set
     */
    public void setNextFlag(boolean nextFlag) {
        this.nextFlag = nextFlag;
    }

    /**
     * @return the lastSelected
     */
    @Column(name = "last_selected")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getLastSelected() {
        return lastSelected;
    }

    /**
     * @param lastSelected the lastSelected to set
     */
    public void setLastSelected(Date lastSelected) {
        this.lastSelected = lastSelected;
    }

    /**
     * @return the ucid
     */
    @Column(name = "ucid")
    public Long getUcid() {
        return ucid;
    }

    /**
     * @param ucid the ucid to set
     */
    public void setUcid(Long ucid) {
        this.ucid = ucid;
    }

    /**
     * @return the dialMessage
     */
    @Column(name = "dial_message")
    public String getDialMessage() {
        return dialMessage;
    }

    /**
     * @param dialMessage the dialMessage to set
     */
    public void setDialMessage(String dialMessage) {
        this.dialMessage = dialMessage;
    }

    /**
     * @return the disposition
     */
    @Column(name = "disposition")
    public String getDisposition() {
        return disposition;
    }

    /**
     * @param disposition the disposition to set
     */
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

}
