package com.ozonetel.occ.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "CampaignConfigration")
public class CampaignConfiguration implements Serializable {

    private Long campaignId;
    private Boolean retryRules;
    private Boolean retryFirst;
    private Boolean didMasking;
    private Integer ringTime;
    private Integer allowBlockCheck;

    @Id
    @Column(name = "CampaignID")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "RetryRules")
    public Boolean isRetryRules() {
        return retryRules;
    }

    public void setRetryRules(Boolean retryRules) {
        this.retryRules = retryRules;
    }

    @Column(name = "retry_first")
    public Boolean isRetryFirst() {
        return retryFirst;
    }

    public void setRetryFirst(Boolean retryFirst) {
        this.retryFirst = retryFirst;
    }

    @Column(name = "DIDMasking")
    public Boolean isDidMasking() {
        return didMasking;
    }

    public void setDidMasking(Boolean didMasking) {
        this.didMasking = didMasking;
    }
    
    @Column(name = "customerRingingTime")
    public Integer getRingTime() {
        return ringTime;
    }

    public void setRingTime(Integer ringTime) {
        this.ringTime = ringTime;
    }

    @Column(name = "IS_BLOCK_ENABLED")
    public Integer getAllowBlockCheck() {
        return allowBlockCheck;
    }

    public void setAllowBlockCheck(Integer allowBlockCheck) {
        this.allowBlockCheck = allowBlockCheck;
    }

    @Override
    public String toString() {
        return "CampaignConfiguration{" + "campaignId=" + campaignId + ", retryRules=" + retryRules + '}';
    }

}
