/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.impl.Participant;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author aparna
 */
@Entity
@Table(name = "Chat_Details")
@NamedQueries({
    @NamedQuery(name = "getLastUpdatedDetailsBySessId", query = "select cd from ChatDetails cd where cd.monitorUcid=:monitorUcid order by id desc limit 1"),})

public class ChatDetails extends BaseObject implements Serializable {

    private Long id;
    private String sessionId;
    private Date startTime;
    private Date endTime;
    private Date startDate;
    private Date endDate;
    private ChatStates node;
    private String agentId;
    private Long campaignId;
    private String skillName;
    private String chat;
    private String disposition;
    private String comments;
    private String endBy;
    private String phoneNo;
    private String uui;
    private String email;
    private String custName;
    private Long userId;
    private String monitorUcid;
    private String feedback;
    private Date agentTta;
    private Long transferType = (long) Participant.OTHER.ordinal();
    private Long transferAgentId;
    private Long transferSkillId;
    private String customerId;
    private String channelType;
    private Long agentUniqueId;
    private Long skillId;
//    SeqId , SessionId, StartDate, StartTime, EnDate, EndTime, Node, Campaign_ID, Skill, AgentID,
//            Transcript, Disposition, Comments, EndBy, PhoneNumer, UUI

    public ChatDetails() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SeqId")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "SessionId")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name = "StartTime")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "EndTime")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "StartDate")
    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "EndDate")
    @Temporal(TemporalType.DATE)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "Node")
    public ChatStates getNode() {
        return node;
    }

    public void setNode(ChatStates node) {
        this.node = node;
    }

    @Column(name = "AgentID")
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    @Column(name = "agent_id")
    public Long getAgentUniqueId() {
        return agentUniqueId;
    }

    public void setAgentUniqueId(Long agentUniqueId) {
        this.agentUniqueId = agentUniqueId;
    }

    @Column(name = "Campaign_ID")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "Skill")
    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Column(name = "skill_id")
    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    @Column(name = "Transcript")
    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    @Column(name = "Disposition")
    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }
    
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "monitorUcid")
    public String getMonitorUcid() {
        return monitorUcid;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    @Column(name = "Comments")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "EndBy")
    public String getEndBy() {
        return endBy;
    }

    public void setEndBy(String endBy) {
        this.endBy = endBy;
    }

    @Column(name = "PhoneNumer")
    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Column(name = "UUI")
    public String getUui() {
        return uui;
    }

    @Column(name="CustMail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name="CustName")
    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public void setUui(String uui) {
        this.uui = uui;
    }
    
    @Column(name="Feedback")
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Column(name="Agent_TTA")
    public Date getAgentTta() {
        return agentTta;
    }

    public void setAgentTta(Date agentTta) {
        this.agentTta = agentTta;
    }

    @Column(name="transfer_type")
    public Long getTransferType() {
        return transferType;
    }

    public void setTransferType(Long transferType) {
        this.transferType = transferType;
    }

    @Column(name="transfer_agentId")
    public Long getTransferAgentId() {
        return transferAgentId;
    }

    public void setTransferAgentId(Long transferAgentId) {
        this.transferAgentId = transferAgentId;
    }

    @Column(name="transfer_skillId")
    public Long getTransferSkillId() {
        return transferSkillId;
    }

    public void setTransferSkillId(Long transferSkillId) {
        this.transferSkillId = transferSkillId;
    }
    
    @Column(name="CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    @Column(name="ChannelType")
    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
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
        final ChatDetails other = (ChatDetails) obj;
        if (!Objects.equals(this.sessionId, other.sessionId)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChatDetails{" + "id=" + id + ", sessionId=" + sessionId + ", startTime=" + startTime + ", endTime=" + endTime + ", startDate=" + startDate + ", endDate=" + endDate + ", node=" + node + ", agentId=" + agentId + ", campaignId=" + campaignId + ", skillName=" + skillName + ", chat=" + chat + ", disposition=" + disposition + ", comments=" + comments + ", endBy=" + endBy + ", phoneNo=" + phoneNo + '}';
    }

}
