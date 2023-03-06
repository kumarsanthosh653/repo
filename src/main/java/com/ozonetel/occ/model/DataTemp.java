package com.ozonetel.occ.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Administrator
 */
@Entity
@Table (name="data_temp")
@NamedQueries ({
    @NamedQuery(name="dataTemp", query="select d from DataTemp d where d.agentId =:agentId and d.callerId =:callerId")
})
public class DataTemp extends BaseObject implements Serializable {

    private Long dataId;
    private Long agentId;
    private Long callerId;
    private Long ucid;

    public DataTemp() {
    }

    public DataTemp(Long dataId, Long agentId, Long callerId) {
        this.dataId = dataId;
        this.agentId = agentId;
        this.callerId = callerId;
    }

    @Id
    @Column(name="data_id")
    public Long getDataId() {
        return dataId;
    }
    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }
    @Column(name="agent_id")
    public Long getAgentId() {
        return agentId;
    }
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    @Column(name="called_number")
    public Long getCallerId() {
        return callerId;
    }
    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object o) {
       return false;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the ucid
     */
    @Column(name="ucid")
    public Long getUcid() {
        return ucid;
    }

    /**
     * @param ucid the ucid to set
     */
    public void setUcid(Long ucid) {
        this.ucid = ucid;
    }
}