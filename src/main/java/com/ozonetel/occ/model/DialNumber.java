/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Rajesh
 */
@Entity
@Table(name = "dial_number")
public class DialNumber extends BaseObject{
    private Long id;
    private String ozAni;
    private Long dataId;
    private String callData;
    private String agentId;
    private String did;
    private Long campaignId;
    private Long userId;

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the ozAni
     */
    @Column(name="oz_ani")
    public String getOzAni() {
        return ozAni;
    }

    /**
     * @param ozAni the ozAni to set
     */
    public void setOzAni(String ozAni) {
        this.ozAni = ozAni;
    }

    /**
     * @return the dataId
     */
    @Column(name="data_id")
    public Long getDataId() {
        return dataId;
    }

    /**
     * @param dataId the dataId to set
     */
    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the callData
     */
    @Column(name="call_data")
    public String getCallData() {
        return callData;
    }

    /**
     * @param callData the callData to set
     */
    public void setCallData(String callData) {
        this.callData = callData;
    }

    /**
     * @return the agentId
     */
    @Column(name="agentId")
    public String getAgentId() {
        return agentId;
    }

    /**
     * @param agentId the agentId to set
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * @return the did
     */
    @Column(name="did")
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
     * @return the campaignId
     */
    @Column(name="camapignId")
    public Long getCampaignId() {
        return campaignId;
    }

    /**
     * @param campaignId the campaignId to set
     */
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * @return the userId
     */
    @Column(name="user_id")
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
		/*return "Agent Id:"+this.agentId+"<br/>"+
        "Agent Status:"+this.state;*/

        return this.ozAni+","+this.agentId+","+this.callData;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
