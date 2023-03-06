package com.ozonetel.occ.model;

import java.util.Date;
import java.util.Objects;
import javax.persistence.*;

/**
 * data.java
 *
 * @author NBabu Date : Oct 28, 2009 Email : nbabu@ozonetel.com,
 * nb.nalluri@yahoo.com
 */
/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "Report")
@NamedQueries({
    @NamedQuery(name = "reportByDate", query = "select r from Report r where r.callDate between :fromDate and :toDate"),
    @NamedQuery(name = "reportByCampaign", query = "select r from Report r where r.campaignId=:campaignId"),
    @NamedQuery(name = "reportByStatus", query = "select r from Report r where r.status=:status"),
    @NamedQuery(name = "reportByDateAndCampaign", query = "select r from Report r where r.callDate between :fromDate and :toDate and r.campaignId=:campaignId"),
    @NamedQuery(name = "reportByDateAndStatus", query = "select r from Report r where r.callDate between :fromDate and :toDate and r.status=:status"),
    @NamedQuery(name = "reportByCampaignAndStatus", query = "select r from Report r where r.campaignId=:campaignId and r.status=:status"),
    @NamedQuery(name = "reportByChoice", query = "select r from Report r where r.callDate between :fromDate and :toDate and r.campaignId=:campaignId and r.status=:status"),
    @NamedQuery(name = "reportByCampaignAndTriedNumberAndStatus", query = "select r from Report r where r.campaignId =:campaignId and triedNumber=:triedNumber and status=:status and call_data!=:callData group by r.data_id"),
    @NamedQuery(name = "reportsByStautsAndDataID", query = "select r from Report r where r.campaignId =:campaignId and triedNumber=:triedNumber and status=:status and call_data!=:callData and data_id=:dataId"),
    @NamedQuery(name = "reportBySuccessStatus", query = "select r from Report r where r.campaignId =:campaignId and triedNumber=:triedNumber and status=:status"),
    @NamedQuery(name = "getReportByUcid", query = "select r from Report r where r.ucid =:ucid and r.did =:did"),
    @NamedQuery(name = "getReportByMonitorUcid", query = "select r from Report r where r.monitorUcid =:monitorUcid and r.did =:did"),
    @NamedQuery(name = "getTransferReportByUcid", query = "select r from Report r where r.ucid =:ucid and r.did =:did and r.transferNow is true")
})
public class Report extends BaseObject {

    private Long report_id;
    private String dest;
    private String Status;
    private String call_data;
    private Date callDate;
    private String agentId;
    private Integer triedNumber;
    private String audioFile;
    private Long data_id;
    private Long ucid;
    private String disposition;
    private Date endTime;
    private Date timeToAnswer;
    private String skillName;
    private String hangUpBy;
    private Long monitorUcid;
    private Agent agent;
    private Long skillId;
    private Long dialOutNumberId;
    private String comment;
    private String uui;
    private String did;
    private String type;
    private Long transferType;
    private Long transferAgentId;
    private Long transferSkillId;
    private boolean transferNow;
    private String transferToNumber;
    private int blindTransfer;
    private boolean offline;
    private String dialStatus;
    private boolean callCompleted;
    private String customerStatus;
    private String agentStatus;
    private User user;
    private Long priId;
    private Long refId; // for ticketing solution
    private FwpNumber fwpNumber;
    private Long campaignId;
    private Date agentAnswerTime;
    private Date customerAnswerTime;
    private Long wrapUpDuration;
    private String e164;

    @Column(name = "reference_no")
    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @Column(name = "tried_number")
    public Integer getTriedNumber() {
        return triedNumber;
    }

    public void setTriedNumber(Integer triedNumber) {
        this.triedNumber = triedNumber;
    }

    @Column(name = "agentid")
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Column(name = "campaign_id")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "report_id")
    public Long getReport_id() {
        return report_id;
    }

    public void setReport_id(Long report_id) {
        this.report_id = report_id;
    }

    @Column(name = "dest")
    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Column(name = "status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Column(name = "call_data")
    public String getCall_data() {
        return call_data;
    }

    public void setCall_data(String call_data) {
        this.call_data = call_data;
    }

    @Column(name = "starttime")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCallDate() {
        return callDate;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    }

    @Column(name = "audio_file")
    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    @Column(name = "data_id")
    public Long getData_id() {
        return data_id;
    }

    public void setData_id(Long data_id) {
        this.data_id = data_id;
    }

    @Transient
    public String getE164() {
        return e164;
    }

    public void setE164(String e164) {
        this.e164 = e164;
    }

    @Transient
    public String getDuration() {
        if (endTime != null && callDate != null) {
            long elapsedTime = endTime.getTime() - callDate.getTime();
            String format = String.format("%%0%dd", 2);
            elapsedTime = elapsedTime / 1000;
            String seconds = String.format(format, elapsedTime % 60);
            String minutes = String.format(format, (elapsedTime % 3600) / 60);
            String hours = String.format(format, elapsedTime / 3600);
            String duration = hours + ":" + minutes + ":" + seconds;
            return duration;
        }
        return null;
    }

    @Transient
    public String getAnsweringTime() {
        if (timeToAnswer != null && callDate != null) {
            long elapsedTime = timeToAnswer.getTime() - callDate.getTime();
            String format = String.format("%%0%dd", 2);
            elapsedTime = elapsedTime / 1000;
            String seconds = String.format(format, elapsedTime % 60);
            String minutes = String.format(format, (elapsedTime % 3600) / 60);
            String hours = String.format(format, elapsedTime / 3600);
            String answeringTime = hours + ":" + minutes + ":" + seconds;
            return answeringTime;
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.report_id);
        hash = 73 * hash + Objects.hashCode(this.ucid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Report other = (Report) obj;
        if (!Objects.equals(this.report_id, other.report_id)) {
            return false;
        }
        if (!Objects.equals(this.ucid, other.ucid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return dest;
    }

    public String toLongString() {
        return "Report{" + "report_id=" + report_id + ", dest=" + dest + ", Status=" + Status + ", agentId=" + agentId + ", data_id=" + data_id + ", ucid=" + ucid + ", monitorUcid=" + monitorUcid + ", agent=" + agent + ", uui=" + uui + ", did=" + did + ", type=" + type + ", dialStatus=" + dialStatus + ", callCompleted=" + callCompleted + ", tta= "+timeToAnswer+", cat=" + customerAnswerTime +", aat=" + agentAnswerTime + '}';
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

    /**
     * @return the endTime
     */
    @Column(name = "endtime")
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

    /**
     * @return the timeToAnswer
     */
    @Column(name = "TTA")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTimeToAnswer() {
        return timeToAnswer;
    }

    /**
     * @param timeToAnswer the timeToAnswer to set
     */
    public void setTimeToAnswer(Date timeToAnswer) {
        this.timeToAnswer = timeToAnswer;
    }

    /**
     * @return the skillName
     */
    @Column(name = "skillName")
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
     * @return the hangUpBy
     */
    @Column(name = "hangUpBy")
    public String getHangUpBy() {
        return hangUpBy;
    }

    /**
     * @param hangUpBy the hangUpBy to set
     */
    public void setHangUpBy(String hangUpBy) {
        this.hangUpBy = hangUpBy;
    }

    /**
     * @return the monitorUcid
     */
    @Column(name = "monitor_ucid")
    public Long getMonitorUcid() {
        return monitorUcid;
    }

    /**
     * @param monitorUcid the monitorUcid to set
     */
    public void setMonitorUcid(Long monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    /**
     * @return the agent
     */
    @ManyToOne(targetEntity = Agent.class)
    @JoinColumn(name = "agent_id", insertable = true, updatable = false)
    public Agent getAgent() {
        return agent;
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Column(name = "skill_id")
    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    @Column(name = "dialout_id")
    public Long getDialOutNumberId() {
        return dialOutNumberId;
    }

    public void setDialOutNumberId(Long dialOutNumberId) {
        this.dialOutNumberId = dialOutNumberId;
    }

    /**
     * @return the comment
     */
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
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

    /**
     * @return the did
     */
    @Column(name = "did")
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
     * @return the type
     */
    @Column(name = "type")
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "transfer_agentId")
    public Long getTransferAgentId() {
        return transferAgentId;
    }

    public void setTransferAgentId(Long transferAgentId) {
        this.transferAgentId = transferAgentId;
    }

    /**
     * @return the transferType
     */
    @Column(name = "transfer_type")
    public Long getTransferType() {
        return transferType;
    }

    /**
     * @param transferType the transferType to set
     */
    public void setTransferType(Long transferType) {
        this.transferType = transferType;
    }

    /**
     * @return the transferNow
     */
    @Column(name = "transfer_now")
    public boolean isTransferNow() {
        return transferNow;
    }

    /**
     * @param transferNow the transferNow to set
     */
    public void setTransferNow(boolean transferNow) {
        this.transferNow = transferNow;
    }

    @Column(name = "transferSkill_id")
    public Long getTransferSkillId() {
        return transferSkillId;
    }

    public void setTransferSkillId(Long transferSkillId) {
        this.transferSkillId = transferSkillId;
    }

    /**
     * @return the transferToNumber
     */
    @Column(name = "transfer_number")
    public String getTransferToNumber() {
        return transferToNumber;
    }

    /**
     * @param transferToNumber the transferToNumber to set
     */
    public void setTransferToNumber(String transferToNumber) {
        this.transferToNumber = transferToNumber;
    }

    /**
     * @return the blindTransfer 1 for Blind 2 for consultative
     */
    @Column(name = "blind_transfer")
    public int getBlindTransfer() {
        return blindTransfer;
    }

    /**
     * @param blindTransfer the blindTransfer to set
     */
    public void setBlindTransfer(int blindTransfer) {
        this.blindTransfer = blindTransfer;
    }

    /**
     * @return the offline
     */
    @Column(name = "is_offline")
    public boolean isOffline() {
        return offline;
    }

    /**
     * @param offline the offline to set
     */
    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    /**
     * @return the dialStatus
     */
    @Column(name = "dial_status")
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
     * @return the callCompleted
     */
    @Column(name = "call_completed")
    public boolean isCallCompleted() {
        return callCompleted;
    }

    /**
     * @param callCompleted the callCompleted to set
     */
    public void setCallCompleted(boolean callCompleted) {
        this.callCompleted = callCompleted;
    }

    /**
     * @return the customerStatus
     */
    @Column(name = "customer_status")
    public String getCustomerStatus() {
        return customerStatus;
    }

    /**
     * @param customerStatus the customerStatus to set
     */
    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }

    /**
     * @return the agentStatus
     */
    @Column(name = "agent_status")
    public String getAgentStatus() {
        return agentStatus;
    }

    /**
     * @param agentStatus the agentStatus to set
     */
    public void setAgentStatus(String agentStatus) {
        this.agentStatus = agentStatus;
    }

    /**
     * @return the user
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "pri_id")
    public Long getPriId() {
        return priId;
    }

    public void setPriId(Long priId) {
        this.priId = priId;
    }

    @ManyToOne(targetEntity = FwpNumber.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "fwp_id", insertable = true, updatable = true)
    public FwpNumber getFwpNumber() {
        return fwpNumber;
    }

    public void setFwpNumber(FwpNumber fwpNumber) {
        this.fwpNumber = fwpNumber;
    }
    
    @Column(name = "Agent_TTA")
    public Date getAgentAnswerTime() {
        return agentAnswerTime;
    }

    public void setAgentAnswerTime(Date agentAnswerTime) {
        this.agentAnswerTime = agentAnswerTime;
    }
    
    @Column(name="Customer_TTA")
    public Date getCustomerAnswerTime() {
        return customerAnswerTime;
    }

    public void setCustomerAnswerTime(Date customerAnswerTime) {
        this.customerAnswerTime = customerAnswerTime;
    }

    @Column(name="WrapUpDuration")
    public Long getWrapUpDuration() {
        return wrapUpDuration;
    }

    public void setWrapUpDuration(Long wrapUpDuration) {
        this.wrapUpDuration = wrapUpDuration;
    }

}
