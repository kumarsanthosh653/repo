package com.ozonetel.occ.dao;

import java.util.List;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.Skill;

/**
 * An interface that provides a data management interface to the Campaign table.
 */
public interface CampaignDao extends GenericDao<Campaign, Long> {

    List<Campaign> getCampaignsByUserId(Long userId);

    List<Campaign> getCampaignsByDid(String did);

    List<Campaign> getCampaignsByDid(String did, String type);

    List<Data> getDataByGroupId(Long callGroupId);

    List<Campaign> getCampaigns();

    List<Campaign> getPreviewCampaignsByAgentId(String username, String agentId);

    List<Campaign> getCampaignsByAgentId(String username, String agentId);

    public List<Skill> getCampaignSkills(Long campaignId);

    public List<CampaignInfo> getPreviewCampaignsInfoByAgentId(String username, String agentId);

    public List<CampaignInfo> getAllOnlineCampaignsInfoByAgentId(String username, String agentId);

    public List<PreviewDataMap> getPreviewDataMap(Long campaignId);
}
