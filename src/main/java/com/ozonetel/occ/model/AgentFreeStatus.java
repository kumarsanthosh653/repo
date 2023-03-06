package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "Agent_FreeStatus")
public class AgentFreeStatus extends BaseObject implements Serializable {

    private Long id;
    private String agentId;
    private String agentName;
    private long idleTime;
    private String clientId;
    private User user;
    private Long lastSelected;
    private Long isLocked;
    private String phoneNumber;
    private Long priority;
    private FwpNumber fwpNumber;
    private Mode mode;
    private Long directCallCount;
    private String requestId;
    private Integer sessionCount;

    public static enum Mode {
        //0 - Inbound
        //1 - Preview
        //2 - Manual
        //3 - Progressive
        //4 - Blended
        //8 - Chat -> matching with agent_data(event) table data_id

        INBOUND, PREVIEW, MANUAL, PROGRESSIVE, BLENDED, DUMMY, DUMMY1, DUMMY2, CHAT;

    }

    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "agent_id")
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Column(name = "agent_name")
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Column(name = "idle_time")
    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    @Column(name = "clientId")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "lastSelected")
    public Long getLastSelected() {
        return lastSelected;
    }

    public void setLastSelected(Long lastSelected) {
        this.lastSelected = lastSelected;
    }

    @Column(name = "is_locked")
    public Long getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Long isLocked) {
        this.isLocked = isLocked;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "priority")
    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    @ManyToOne(targetEntity = FwpNumber.class)
    @JoinColumn(name = "fwp_id", insertable = true, updatable = true)
    public FwpNumber getFwpNumber() {
        return fwpNumber;
    }

    public void setFwpNumber(FwpNumber fwpNumber) {
        this.fwpNumber = fwpNumber;
    }

    @Enumerated
    @Column(name = "mode")
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Column(name = "DirectCallCounter")
    public Long getDirectCallCount() {
        return directCallCount;
    }

    public void setDirectCallCount(Long directCallCount) {
        this.directCallCount = directCallCount;
    }

    @Column(name = "RequestID")
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Column(name = "sess_count")
    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.agentId);
        hash = 83 * hash + Objects.hashCode(this.user);
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
        final AgentFreeStatus other = (AgentFreeStatus) obj;
        if (!Objects.equals(this.agentId, other.agentId)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AgentFreeStatus{" + "id=" + id + ", agentId=" + agentId + ", agentName=" + agentName + ", idleTime=" + idleTime + ", clientId=" + clientId + ", user=" + user + ", lastSelected=" + lastSelected + ", isLocked=" + isLocked + ", phoneNumber=" + phoneNumber + ", priority=" + priority + ", fwpNumber=" + fwpNumber + ", mode=" + mode + ", directCallCount=" + directCallCount + ", requestId=" + requestId + '}';
    }

}
