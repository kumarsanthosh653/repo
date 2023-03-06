package com.ozonetel.occ.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallDisposition;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.util.DateUtil;

/**
 * @author Sudhakar
 */
public class CallDispositionAction extends  BaseAction {

    private GenericManager<CallDisposition, Long> callDispositionManager;
    private CampaignManager campaignManager;
    private AgentManager agentManager;
    private String date;
    private Long campaignId;
    
    private String agentId;
    
    private List<CallDisposition> callDispositions = new ArrayList<CallDisposition>();

    public void setCallDispositionManager(GenericManager<CallDisposition, Long> callDispositionManager) {
        this.callDispositionManager = callDispositionManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    
    public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}
    public List<CallDisposition> getCallDispositions() {
        return callDispositions;
    }

    public List<Campaign> getCampaigns() {
        return campaignManager.getCampaigns();
    }
    
    public List<Agent> getAgents() {
		return agentManager.getAll();
	}

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public Long getCampaignId() {
        return campaignId;
    }
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }
    

    public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String list() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Entering 'list' method");
        }
        boolean isExec = true;
        Map<String, Object> params = new HashMap<String, Object>();
        StringBuilder queryString = new StringBuilder("select c from CallDisposition c where ");
        if (getDate() != null && !getDate().equals("")) {
            queryString.append("c.callDate between :fromDate and :toDate and ");
            params.put("fromDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getDate()+" 00:00:00"));
            params.put("toDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getDate()+" 23:59:59"));
        }
       /* if (getCampaignId() != null && !getCampaignId().equals("")) {
            queryString.append("c.campaign.id = :campaignId ");
            params.put("campaignId", getCampaignId());
        }*/
        if(getAgentId() != null && !getAgentId().equals("")){
        	queryString.append("c.agent.agentId = :agentId ");
            params.put("agentId", getAgentId());
        }
        
        if (queryString.toString().endsWith("where ")) {
            queryString.delete(queryString.toString().length() - 6, queryString.toString().length());
            isExec = false;
        } else if (queryString.toString().endsWith("and ")) {
            queryString.delete(queryString.toString().length() - 4, queryString.toString().length());
        }
        queryString.append("order by c.callDate desc");
//        log.debug("Query String : "+queryString.toString());
        if (isExec) {
            callDispositions = callDispositionManager.findByNamedParams(queryString.toString(), params);
        }
//        log.debug("callDispositions : "+callDispositions);
        return SUCCESS;
    }
}