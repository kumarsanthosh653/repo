package com.ozonetel.occ.model;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author pavanj
 */
public class CallbackScheduleDetails {

    private Long id;
    private Date callbackDate;
    private String campaignName;
    private String callbackNumber;
    private String comments;
    private Date dateCreated;
    private String rescheduleComment;
    private Date dateRescheduled;

    public CallbackScheduleDetails() {
    }

    public CallbackScheduleDetails(Long id, Date callbackDate, String campaignName, String callbackNumber, String comments, Date dateCreated, String rescheduleComment, Date dateRescheduled) {
        this.id = id;
        this.callbackDate = callbackDate;
        this.campaignName = campaignName;
        this.callbackNumber = callbackNumber;
        this.comments = comments;
        this.dateCreated = dateCreated;
        this.rescheduleComment = rescheduleComment;
        this.dateRescheduled = dateRescheduled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCallbackDate() {
        return callbackDate;
    }

    public void setCallbackDate(Date callbackDate) {
        this.callbackDate = callbackDate;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCallbackNumber() {
        return callbackNumber;
    }

    public void setCallbackNumber(String callbackNumber) {
        this.callbackNumber = callbackNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getRescheduleComment() {
        return rescheduleComment;
    }

    public void setRescheduleComment(String rescheduleComment) {
        this.rescheduleComment = rescheduleComment;
    }

    public Date getDateRescheduled() {
        return dateRescheduled;
    }

    public void setDateRescheduled(Date dateRescheduled) {
        this.dateRescheduled = dateRescheduled;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.callbackNumber);
        hash = 83 * hash + Objects.hashCode(this.dateCreated);
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
        final CallbackScheduleDetails other = (CallbackScheduleDetails) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.callbackNumber, other.callbackNumber)) {
            return false;
        }
        if (!Objects.equals(this.dateCreated, other.dateCreated)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CallbackScheduledDetails{" + "id=" + id + ", callbackDate=" + callbackDate + ", campaignName=" + campaignName + ", callbackNumber=" + callbackNumber + ", comments=" + comments + ", dateCreated=" + dateCreated + ", rescheduleComment=" + rescheduleComment + ", dateRescheduled=" + dateRescheduled + '}';
    }

}
