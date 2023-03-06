package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Sudhakar
 */
@Entity
@Table (name="call_disposition")
public class CallDisposition extends BaseObject implements Serializable {

    private Long id;
    private String callerId;
    private String disposition;
    private String comments;
    private Date callDate;
    private Agent agent;
    private String callBack;
    private Campaign campaign;

    public CallDisposition() {
    }

    public CallDisposition(String callerId, String disposition, String comments,
            Date callDate, Agent agent, String callBack, Campaign campaign) {
        this.callerId = callerId;
        this.disposition = disposition;
        this.comments = comments;
        this.callDate = callDate;
        this.agent = agent;
        this.callBack = callBack;
        this.campaign = campaign;
    }

    @Id @GeneratedValue (strategy=GenerationType.AUTO)
    @Column (name="id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column (name="callerId")
    public String getCallerId() {
        return callerId;
    }
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    @Column (name="disposition")
    public String getDisposition() {
        return disposition;
    }
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    @Column (name="comments")
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column (name="datetime")
    @Temporal (TemporalType.TIMESTAMP)
    public Date getCallDate() {
        return callDate;
    }
    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    }

    @ManyToOne (targetEntity=Agent.class)
    @JoinColumn (name="agent_id",referencedColumnName="agent_id")
    public Agent getAgent() {
        return agent;
    }
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Column (name="callback")
    public String getCallBack() {
        return callBack;
    }
    public void setCallBack(String callBack) {
        this.callBack = callBack;
    }

    @ManyToOne (targetEntity=Campaign.class)
    @JoinColumn (name="campaign_id")
    public Campaign getCampaign() {
        return campaign;
    }
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CallDisposition [")
                .append("callerId = ").append(callerId).append(", ")
                .append("disposition = ").append(disposition).append(", ")
                .append("comments = ").append(comments).append(", ")
                .append("callDate = ").append(callDate).append(", ")
                .append("agent = ").append(agent).append(", ")
                .append("callBack = ").append(callBack).append(", ")
                .append("campaign = ").append(campaign).append(" ")
                .append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;
	if (!(o instanceof CallDisposition)) return false;
	final CallDisposition callDisposition = (CallDisposition) o;

	return (id != null ? !id.equals(callDisposition.id) : callDisposition.id != null)
		&& (callerId != null ? !callerId.equals(callDisposition.callerId) : callDisposition.callerId != null)
		&& (disposition != null ? !disposition.equals(callDisposition.disposition) : callDisposition.disposition != null)
		&& (comments != null ? !comments.equals(callDisposition.comments) : callDisposition.comments != null)
		&& (callDate != null ? !callDate.equals(callDisposition.callDate) : callDisposition.callDate != null)
		&& (agent != null ? !agent.equals(callDisposition.agent) : callDisposition.agent != null)
		&& (callBack != null ? !callBack.equals(callDisposition.callBack) : callDisposition.callBack != null)
		&& (campaign != null ? !campaign.equals(callDisposition.campaign) : callDisposition.campaign != null);
    }

    @Override
    public int hashCode() {
	int result = 17;

	result = 37 * result + (id != null ? id.hashCode() : 0);
	result = 37 * result + (callerId != null ? callerId.hashCode() : 0);
	result = 37 * result + (disposition != null ? disposition.hashCode() : 0);
	result = 37 * result + (comments != null ? comments.hashCode() : 0);
	result = 37 * result + (callDate != null ? callDate.hashCode() : 0);
	result = 37 * result + (agent != null ? agent.hashCode() : 0);
	result = 37 * result + (callBack != null ? callBack.hashCode() : 0);
	result = 37 * result + (campaign != null ? campaign.hashCode() : 0);

	return result;
    }
}