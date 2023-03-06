package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.Constants;
import java.util.List;

import javax.jws.WebService;

import com.ozonetel.occ.dao.CampaignDao;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.CampaignManager;

@WebService(serviceName = "CampaignService", endpointInterface = "com.ozonetel.occ.service.CampaignManager")
public class CampaignManagerImpl extends GenericManagerImpl<Campaign, Long> implements CampaignManager {

    CampaignDao campaignDao;

    public CampaignManagerImpl(CampaignDao campaignDao) {
        super(campaignDao);
        this.campaignDao = campaignDao;
    }

    public List<Campaign> getCampaignsByUserId(Long userID) {
        return campaignDao.getCampaignsByUserId(userID);
    }

    public Campaign getCampaignsByDid(String did) {

        List<Campaign> campaigns = campaignDao.getCampaignsByDid(did);
        Campaign c = null;
        //Check the Campaign is in Active and Running state
        for (Campaign campaign : campaigns) {
            if (campaign.getPosition().equalsIgnoreCase("Running") || campaign.getPosition().equalsIgnoreCase("STARTED") || campaign.getPosition().equalsIgnoreCase("STOPING")) {
                c = campaign;
                break;
            }
        }
        return c;
    }

    public Campaign getCampaignsByDid(String did, String type) {
//        log.debug("Getting campaign with did : " + did + " Type : " + type);
        List<Campaign> campaigns = campaignDao.getCampaignsByDid(did, type);
        Campaign c = null;
        //Check the Campaign is in Active and Running state
        for (Campaign campaign : campaigns) {
//            log.debug("Got Campaign position:" + campaign.getPosition() + " campaignDid = " + campaign.getdId());
            if (campaign.getPosition().equalsIgnoreCase("Running") || campaign.getPosition().equalsIgnoreCase("STARTED") || campaign.getPosition().equalsIgnoreCase("STOPING")) {
                c = campaign;
                break;
            }
        }
        return c;
    }

    public List<Skill> getCampaignSkills(Long campaignId) {
        return campaignDao.getCampaignSkills(campaignId);
    }

    public List<Data> getDataByGroupId(Long callGroupId) {
        return campaignDao.getDataByGroupId(callGroupId);
    }

    public List<Campaign> getCampaigns() {
        return campaignDao.getCampaigns();
    }

    public List<Campaign> getPreviewCampaignsByAgentId(String username, String agentId) {
        return campaignDao.getPreviewCampaignsByAgentId(username, agentId);
    }

    public List<Campaign> getCampaignsByAgentId(String username, String agentId) {
        return campaignDao.getCampaignsByAgentId(username, agentId);
    }

    public void setCampaignCompleted(Long campaignId) {
//-----------------------------------------------------------   
        log.trace("$$$$$$$$$$$$$Making Campaign completed:" + campaignId);
        Campaign campaign = campaignDao.get(campaignId);
        campaign.setPosition(Constants.CAMP_COMPLETED);
        save(campaign);

    }

    @Override
    public List<CampaignInfo> getPreviewCampaignsInfoByAgentId(String username, String agentId) {
        return campaignDao.getPreviewCampaignsInfoByAgentId(username, agentId);

    }

    @Override
    public List<CampaignInfo> getAllOnlineCampaignsInfoByAgentId(String username, String agentId) {
        return campaignDao.getAllOnlineCampaignsInfoByAgentId(username, agentId);
    }

    @Override
    public PreviewDataMap getPreviewDataMap(Long campaignId) {
        return campaignDao.getPreviewDataMap(campaignId).get(0);
    }

}
