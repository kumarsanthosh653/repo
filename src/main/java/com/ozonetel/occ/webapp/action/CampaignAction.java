package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.List;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.OCCManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CampaignAction extends BaseAction implements Preparable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Log log = LogFactory.getLog(CampaignAction.class);
    private CampaignManager campaignManager;
    private Campaign campaign;
    List<Skill> skillList = new ArrayList<Skill>();
    private OCCManager occManager;
    private FwpNumberManager fwpNumberManager;

    /**
     * Grab the entity from the database before populating with request
     * parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            String campaignId = getRequest().getParameter("campaign.campaignId");
            if (campaignId != null && !campaignId.equals("")) {
                campaign = campaignManager.get(new Long(campaignId));
            }
        }
    }

    /**
     * @return the occManager
     */
    public OCCManager getOccManager() {
        return occManager;
    }

    /**
     * @param occManager the occManager to set
     */
    public void setOccManager(OCCManager occManager) {
        this.occManager = occManager;
    }

    public String release() {
        String agentId = getRequest().getParameter("agentId");
        String agentUniqueId = getRequest().getParameter("agentUniqueId");
        String resp = occManager.releaseAgentByAdmin(getRequest().getRemoteUser(), agentUniqueId, getRequest().getRemoteUser());
        return SUCCESS;
    }

    public String releasePhoneNumber() {
        String phoneId = getRequest().getParameter("id");
        FwpNumber fwp = fwpNumberManager.get(new Long(phoneId));
        fwp.setContact(null);
        fwp.setNextFlag(new Long(0));
        fwp.setState(Agent.State.IDLE);
        fwpNumberManager.save(fwp);
        return SUCCESS;
    }

    public String logoff() {
        String agentId = getRequest().getParameter("agentId");
        Long agentUniqueId = new Long(getRequest().getParameter("agentUniqueId"));
        String resp = occManager.logoffAgentByAdmin(getRequest().getRemoteUser(),agentUniqueId, getRequest().getRemoteUser());
        return SUCCESS;
    }
    /**
     * @return the fwpNumberManager
     */
    public FwpNumberManager getFwpNumberManager() {
        return fwpNumberManager;
    }

    /**
     * @param fwpNumberManager the fwpNumberManager to set
     */
    public void setFwpNumberManager(FwpNumberManager fwpNumberManager) {
        this.fwpNumberManager = fwpNumberManager;
    }
}
