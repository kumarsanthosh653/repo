package com.ozonetel.occ.service;

import java.util.List;

import javax.jws.WebService;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.Skill;

@WebService
public interface CampaignManager extends GenericManager<Campaign, Long> {

    List<Campaign> getCampaignsByUserId(Long userID);

    Campaign getCampaignsByDid(String did);

    Campaign getCampaignsByDid(String did, String type);

    List<Data> getDataByGroupId(Long callGroupId);

///	public void startCampaign(List<Data> dataList);
//	public void stopCampaign();
    List<Campaign> getCampaigns();

    List<Campaign> getPreviewCampaignsByAgentId(String username, String agentId);

    List<Campaign> getCampaignsByAgentId(String username, String agentId);

    public List<Skill> getCampaignSkills(Long campaignId);

    public void setCampaignCompleted(Long campaignId);

    /**
     *
     * @param username
     * @param agentId
     * @return list of <code>CampaignInfo</code> containing only <code>name,
     * id and agentWise flag</code>. You will not have actual count.
     */
    public List<CampaignInfo> getPreviewCampaignsInfoByAgentId(String username, String agentId);

    /**
     *
     * @param username
     * @param agentId
     * @return list of <code>CampaignInfo</code> containing only <code>name &
     * id</code>. You will not have other properties.
     */
    public List<CampaignInfo> getAllOnlineCampaignsInfoByAgentId(String username, String agentId);

    public PreviewDataMap getPreviewDataMap(Long campaignId);
}
