/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Rajesh
 */
public class InBoundAction extends BaseAction {

private AgentManager agentManager;

private CampaignManager campaignManager;

private List<Campaign> campaigns;

private Campaign campaign;

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }


   public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public String list() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", new String[]{"InBound"});
        params.put("status", false);
        params.put("username", getRequest().getRemoteUser());
        campaigns = campaignManager.findByNamedQuery("campaignsByTypeAndUserAndStatus", params);
        return SUCCESS;
    }




      public String startCampaign() {
            String campId = getRequest().getParameter("campId");
            campaign = campaignManager.get(new Long(campId));
            campaign.setPosition("STARTED");
            campaignManager.save(campaign);
            return SUCCESS;
            }
      public String stopCampaign() {
            String campId = getRequest().getParameter("campId");
            campaign = campaignManager.get(new Long(campId));
            campaign.setPosition("STOPPED");
            campaignManager.save(campaign);
            return SUCCESS;
         }
      public String resumeCampaign() {
            String campId = getRequest().getParameter("campId");
            campaign = campaignManager.get(new Long(campId));
            campaign.setPosition("STARTED");
            campaignManager.save(campaign);
            return SUCCESS;
         }

}
