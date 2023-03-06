package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "agent_logins")
public class AgentEventCache implements Serializable{

    private Long agentId;
    private Long loginId;
    private Long lastEventId;

    @Id
    @Column(name = "agent_id")
    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    @Column(name = "loginid")
    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    @Column(name = "lasteventid")
    public Long getLastEventId() {
        return lastEventId;
    }

    public void setLastEventId(Long lastEventId) {
        this.lastEventId = lastEventId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.agentId);
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
        final AgentEventCache other = (AgentEventCache) obj;
        if (!Objects.equals(this.agentId, other.agentId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AgentEventCache{" + "agentId=" + agentId + ", loginId=" + loginId + ", lastEventId=" + lastEventId + '}';
    }

}
