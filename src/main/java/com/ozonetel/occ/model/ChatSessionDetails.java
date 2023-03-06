package com.ozonetel.occ.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ozonetel.occ.service.ChatStates;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author PavanJ
 */
public class ChatSessionDetails {

    private String chatCustName;
    private String agentId;
    private String caUserName;
    private String sessionCounts;
    private Long campaignId;
    private String did;
    private String phoneNumber;
    private String email;
    private String sessionId;
    @JsonIgnore
    private Set<String> clientWsIds = new HashSet<>();
    private String monitorUcid;
    private String ucid;
    @JsonIgnore
    private String apiKey;
    private String skill;
    private ChatStates chatState;
    private String chatCustId;
    private boolean customerEnded;
    private long stateStartTime;
    private Long caUserId;
    private long autoTimeout;
    private boolean isTransferredChat;
    private String transferFromAgentId;
    private String customerId;
    private String channelType;
    private String callbackUrl;
    private String recipient;
    private Long agentUniqueId;
    private Long skillId;

    public ChatSessionDetails() {
    }

    public ChatSessionDetails(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getStateStartTime() {
        return stateStartTime;
    }

    public void setStateStartTime(long stateStartTime) {
        this.stateStartTime = stateStartTime;
    }

    public Long getCaUserId() {
        return caUserId;
    }

    public void setCaUserId(Long caUserId) {
        this.caUserId = caUserId;
    }
    
   
    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }
    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getChatCustName() {
        return chatCustName;
    }

    public void setChatCustName(String chatCustName) {
        this.chatCustName = chatCustName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Long getAgentUniqueId() {
        return agentUniqueId;
    }

    public void setAgentUniqueId(Long agentUniqueId) {
        this.agentUniqueId = agentUniqueId;
    }
    public String getCaUserName() {
        return caUserName;
    }

    public void setCaUserName(String caUserName) {
        this.caUserName = caUserName;
    }

    public String getSessionCounts() {
        return sessionCounts;
    }

    public void setSessionCounts(String sessionCounts) {
        this.sessionCounts = sessionCounts;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Set<String> getClientWsIds() {
        return clientWsIds;
    }

    public void setClientWsIds(Set<String> clientWsIds) {
        this.clientWsIds = clientWsIds;
    }

    public void addClientWsId(String wsId) {
        this.clientWsIds.add(wsId);
    }

    public void removeClientWsId(String wsId) {
        this.clientWsIds.remove(wsId);
    }

    public String getMonitorUcid() {
        return monitorUcid;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ChatStates getChatState() {
        return chatState;
    }

    public void setChatState(ChatStates chatState) {
        this.chatState = chatState;
    }

    public String getChatCustId() {
        return chatCustId;
    }

    public void setChatCustId(String chatCustId) {
        this.chatCustId = chatCustId;
    }

    public boolean isCustomerEnded() {
        return customerEnded;
    }

    public void setCustomerEnded(boolean customerEnded) {
        this.customerEnded = customerEnded;
    }

    public long getAutoTimeout() {
        return autoTimeout;
    }

    public void setAutoTimeout(long autoTimeout) {
        this.autoTimeout = autoTimeout;
    }

    public boolean isIsTransferredChat() {
        return isTransferredChat;
    }

    public void setIsTransferredChat(boolean isTransferredChat) {
        this.isTransferredChat = isTransferredChat;
    }

    public String getTransferFromAgentId() {
        return transferFromAgentId;
    }

    public void setTransferFromAgentId(String transferFromAgentId) {
        this.transferFromAgentId = transferFromAgentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.sessionId);
        return hash;
    }

    @Override
    public String toString() {
        return "ChatSessionDetails{" +
                "chatCustName='" + chatCustName + '\'' +
                ", agentId='" + agentId + '\'' +
                ", caUserName='" + caUserName + '\'' +
                ", sessionCounts='" + sessionCounts + '\'' +
                ", campaignId=" + campaignId +
                ", did='" + did + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", clientWsIds=" + clientWsIds +
                ", monitorUcid='" + monitorUcid + '\'' +
                ", ucid='" + ucid + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", skill='" + skill + '\'' +
                ", chatState=" + chatState +
                ", chatCustId='" + chatCustId + '\'' +
                ", customerEnded=" + customerEnded +
                ", stateStartTime=" + stateStartTime +
                ", caUserId=" + caUserId +
                ", autoTimeout=" + autoTimeout +
                ", isTransferredChat=" + isTransferredChat +
                ", transferFromAgentId='" + transferFromAgentId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", channelType='" + channelType + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", recipient='" + recipient + '\'' +
                ", agentUniqueId=" + agentUniqueId +
                ", skillId=" + skillId +
                '}';
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
        final ChatSessionDetails other = (ChatSessionDetails) obj;
        if (!Objects.equals(this.sessionId, other.sessionId)) {
            return false;
        }
        return true;
    }

}
