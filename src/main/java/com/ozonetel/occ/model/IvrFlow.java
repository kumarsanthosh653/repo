/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author rajesh
 */
@Entity
@Table(name = "IVR_FlowDetails")
@NamedQueries({
    @NamedQuery(name = "getFeedbackIvrList", query = "select i from IvrFlow i where i.type = :ivrType")
//    @NamedQuery(name = "getFeedbackIvrList", query = "select i from IvrFlow i where i.user.id = :userId and i.type = :ivrType")
//    @NamedQuery(name = "campaignsByInBound", query = "select c from Campaign c where c.campaignType like 'InBound' and  c.isDelete = False"),
//    @NamedQuery(name = "campaignsByTypeAndUserAndStatus", query = "select c from Campaign c where c.campaignType in (:type) and  c.isDelete = :status and c.user.username = :username"),
//    @NamedQuery(name = "getCampaignStatusByDid", query = "select c from Campaign c where c.dId like :did"),
//    @NamedQuery(name = "getCampaignByDidAndUser", query = "select c from Campaign c where c.dId = :did and c.user.id = :userId and c.isDelete = :status"),
//    @NamedQuery(name = "getCampaignByUser", query = "select c from Campaign c where c.user.username = :username")
})
public class IvrFlow extends BaseObject{
    private Long flowId;
    private String flowName;
    private String flowDetail;
    private User user;
    private Integer type;
    private Boolean isCustom;
    private String appUrl;
    private boolean isTransfer;

    /**
     * @return the flowId
     */
    @Id
    @Column(name = "IVRFlowID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getFlowId() {
        return flowId;
    }

    /**
     * @param flowId the flowId to set
     */
    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    /**
     * @return the flowName
     */
    @Column(name = "IVRFlowName")
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName the flowName to set
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * @return the flowDetail
     */
    @Column(name = "IVRFlow")
    public String getFlowDetail() {
        return flowDetail;
    }

    /**
     * @param flowDetail the flowDetail to set
     */
    public void setFlowDetail(String flowDetail) {
        this.flowDetail = flowDetail;
    }

    @Column(name = "IVRFlowType")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "App_Url")
    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    @Column(name = "is_custom")
    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    @Override
    public String toString() {
//        return user.getUsername()+"~"+flowName;
        return flowName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IvrFlow)) {
            return false;
        }

        final IvrFlow ivrFlow = (IvrFlow) o;

        return !(flowName != null ? !flowName.equals(ivrFlow.flowName) : ivrFlow.flowName != null);
    }

    @Override
    public int hashCode() {
        return 0; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the user
     */
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "User_Id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the isTransfer
     */
    @Column(name = "is_transfer")
    public Boolean isIsTransfer() {
        return isTransfer;
    }

    /**
     * @param isTransfer the isTransfer to set
     */
    public void setIsTransfer(Boolean isTransfer) {
        this.isTransfer = isTransfer;
    }
    
    
}
