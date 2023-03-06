package com.ozonetel.occ.dao.hibernate;

import java.util.List;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;

import com.ozonetel.occ.dao.CampaignDao;
import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.Skill;

public class CampaignDaoHibernate extends GenericDaoHibernate<Campaign, Long>
        implements CampaignDao {

    public CampaignDaoHibernate() {
        super(Campaign.class);
    }

    @Override
    public List<Campaign> getCampaignsByUserId(Long userId) {
        return (List<Campaign>) getHibernateTemplate().find("from Campaign ca where ca.isDelete = False and  ca.user.id=?", userId);
    }

    @Override
    public List<Campaign> getCampaignsByDid(String did) {
        return (List<Campaign>) getHibernateTemplate().find("from Campaign ca where ca.isDelete = False and ca.dId =?",did);
    }

    @Override
    public List<Campaign> getCampaignsByDid(String did, String type) {
        return (List<Campaign>) getHibernateTemplate().find("from Campaign ca where ca.isDelete = False and ca.dId =? and ca.campaignType = ?",did,type);
    }

    @Override
    public List<Data> getDataByGroupId(Long callGroupId) {
        return (List<Data>) getHibernateTemplate().find("from Data d where d.callgroup.callGroupId = ?", callGroupId);
    }

    @Override
    public List<Campaign> getCampaigns() {
//            return getHibernateTemplate().find("from Campaign c where c.isDelete = False and upper(c.campaignType) not like 'INBOUND'");
        return (List<Campaign>) getHibernateTemplate().find("from Campaign c where c.isDelete = False ");
    }

    @Override
    public List<Campaign> getPreviewCampaignsByAgentId(String username, String agentId) {
        return (List<Campaign>) getHibernateTemplate().find("select Distinct c from Campaign c , IN (c.skills) s , IN (s.agents) a where c.isDelete is not true and  c.campaignType = 'preview' and c.position = 'RUNNING' and c.user.username = '" + username + "' and a.agentId = '" + agentId + "' ");
    }

    @Override
    public List<Campaign> getCampaignsByAgentId(String username, String agentId) {
        return (List<Campaign>) getHibernateTemplate().find("select Distinct c from Campaign c , IN (c.skills) s , IN (s.agents) a where c.isDelete is not true and  c.position = 'RUNNING' and c.user.username = '" + username + "' and a.agentId ='" + agentId + "' and a.active is true order by c.campignName");
    }

    @Override
    public List<Skill> getCampaignSkills(Long campaignId) {
        return (List<Skill>) getHibernateTemplate().find("select distinct s from Campaign c,IN(c.skills) s where c.campaignId=?", campaignId);
    }

    @Override
    public List<CampaignInfo> getAllOnlineCampaignsInfoByAgentId(String username, String agentId) {
        return (List<CampaignInfo>) getHibernateTemplate().find("select new com.ozonetel.occ.model.CampaignInfo(c.campaignId,c.campignName)"
                + " from Campaign c , IN (c.skills) s , IN (s.agents) a  "
                + " where c.isDelete is not true and c.offLineMode is not true  and  c.position = 'RUNNING' and c.user.username =? and a.agentId = ?   order by c.campignName ", username, agentId);
    }

    @Override
    public List<CampaignInfo> getPreviewCampaignsInfoByAgentId(String username, String agentId) {
        return (List<CampaignInfo>) getHibernateTemplate().find("select new com.ozonetel.occ.model.CampaignInfo(c.campaignId,c.campignName,c.agentWise)"
                + " from Campaign c , IN (c.skills) s , IN (s.agents) a  "
                + " where c.isDelete is not true and  c.campaignType = 'preview' and  c.position = 'RUNNING' and c.user.username =? and a.agentId = ?   order by c.campignName ", username, agentId);
    }

    public List<PreviewDataMap> getPreviewDataMap(Long campaignId){
        return getHibernateTemplate().find("select previewDataMap from Campaign c where c.campaignId=?", campaignId);
    }
}
