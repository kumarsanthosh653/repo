package com.ozonetel.occ.model;

import com.ozonetel.occ.util.DateUtil;
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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * CallBack.java rajesh Date : Feb 5, 2012 Email : rajesh@ozonetel.com
 */
@Entity
@Table(name = "call_back")
public class CallBack extends BaseObject {

//	/Id, agent_id, call_back_number, callback_date_time, comments.
    private Long id;
    private Agent agent;
    private String callbackNumber;
    private Date callbackDate;
    private String comments;
    private boolean called;
    private Campaign campaign;
    private Date dateCreated;
    private User user;
    private String rescheduleComment;
    private Date dateRescheduled;
    private boolean deleted;
    private String deleteComment;
    private Date dateDeleted;
    private String uui;
    private String callbackTz;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "reschedule_comment")
    public String getRescheduleComment() {
        return rescheduleComment;
    }

    public void setRescheduleComment(String rescheduleComment) {
        this.rescheduleComment = rescheduleComment;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "date_rescheduled")
    public Date getDateRescheduled() {
        return dateRescheduled;
    }

    public void setDateRescheduled(Date dateRescheduled) {
        this.dateRescheduled = dateRescheduled;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "date_created")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "is_deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "delete_comment")
    public String getDeleteComment() {
        return deleteComment;
    }

    public void setDeleteComment(String deleteComment) {
        this.deleteComment = deleteComment;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "date_deleted")
    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    @ManyToOne(targetEntity = Agent.class)
    @JoinColumn(name = "agent_id", insertable = true, updatable = true)
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Column(name = "call_back_number")
    public String getCallbackNumber() {
        return callbackNumber;
    }

    public void setCallbackNumber(String callbackNumber) {
        this.callbackNumber = callbackNumber;
    }

    @Column(name = "callback_date_time")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCallbackDate() {
        return callbackDate;
    }

    public void setCallbackDate(Date callbackDate) {
        this.callbackDate = callbackDate;
    }

    @Column(name = "comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "callback_tz")
    public String getCallbackTz() {
        return callbackTz;
    }

    public void setCallbackTz(String callbackTz) {
        this.callbackTz = callbackTz;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String toShortString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("User", this.user)
                .append("Agent", this.agent != null ? this.agent.toShortString() : this.agent)
                .append("CustomerNumber", this.callbackNumber)
                .append("Callback Date", this.callbackDate).toString();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        //returns
        //id~callBackdate~campaignId~campaignName~callBackNumber~comments
        StringBuilder s = new StringBuilder();

        s.append(this.id)
                .append("~").append(this.callbackDate != null ? DateUtil.getDateTime("dd-MM-yyyy H:mm", this.callbackDate) : "");
        if (this.campaign != null) {
            s.append("~").append(this.campaign.getCampignName());
        } else {
            s.append("~").append("").append("~").append("");
        }

        s.append("~").append(this.callbackNumber).append("~").append(this.comments).append("~").append(this.dateCreated != null ? DateUtil.getDateTime("dd-MM-yyyy H:mm", this.dateCreated) : "");
        s.append("~").append(this.rescheduleComment == null ? "" : this.rescheduleComment).append("~").append(this.dateRescheduled != null ? DateUtil.getDateTime("dd-MM-yyyy H:mm", this.dateRescheduled) : " ");

        return s.toString();
    }

    @ManyToOne(targetEntity = Campaign.class)
    @JoinColumn(name = "campaign_id", insertable = true, updatable = true)
    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @Column(name = "is_called")
    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    /**
     * @return the uui
     */
    @Column(name = "uui")
    public String getUui() {
        return uui;
    }

    /**
     * @param uui the uui to set
     */
    public void setUui(String uui) {
        this.uui = uui;
    }
}
