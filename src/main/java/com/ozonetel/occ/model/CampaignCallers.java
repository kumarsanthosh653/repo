/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author chaitanya
 */
public class CampaignCallers {

    private String campaignId;
    private List callers = Collections.synchronizedList(new ArrayList());

    public CampaignCallers(String campaignId){
        this.campaignId=campaignId;
    }

    /**
     * @return the campaignId
     */
    public String getCampaignId() {
        return campaignId;
    }

    /**
     * @param campaignId the campaignId to set
     */
    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * @return the callers
     */
    public List getCallers() {
        return callers;
    }

    /**
     * @param callers the callers to set
     */
    public void setCallers(List callers) {
        this.callers = callers;
    }


}
