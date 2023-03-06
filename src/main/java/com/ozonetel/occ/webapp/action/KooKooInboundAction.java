/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.webapp.action;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.CampaignCallers;
import com.ozonetel.occ.service.AgentManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KooKooInboundAction extends BaseAction {
    private AgentManager agentManager;
    
    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public List getPersons() {
        return null;
    }

    public String list() {
        String customerId=getRequest().getParameter("did");
        String callerId=getRequest().getParameter("cid");
        Map campaignCallers=(Map)getRequest().getSession().getServletContext().getAttribute(Constants.CAMPAIGN_CALLERS);

       CampaignCallers callers=(CampaignCallers)campaignCallers.get(customerId);
        List list=callers.getCallers();
        synchronized(list) {
            list.add(callerId);
        }
        List agents=agentManager.getAgentsByCampaign(new Long(callers.getCampaignId()));
        if(agents.size()>0)
            return SUCCESS;
        else
            return ERROR;
    }
}
