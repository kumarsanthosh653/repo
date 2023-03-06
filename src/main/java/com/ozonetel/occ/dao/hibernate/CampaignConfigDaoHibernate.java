/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.CampaignConfigDao;
import com.ozonetel.occ.model.CampaignConfig;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public class CampaignConfigDaoHibernate extends GenericDaoHibernate<CampaignConfig, Long>
        implements CampaignConfigDao {
    public CampaignConfigDaoHibernate() {
        super(CampaignConfig.class);
    }

    public List<CampaignConfig> getCampaignConfigByCampaignId(Long campaignId) {
        return getHibernateTemplate().find("select c from CampaignConfig c where c.campaignId = ? ", campaignId);
    }
    
    public List<CampaignConfig> getCampaignConfigByCampaignIdAndType(Long campaignId, String type){
        return getHibernateTemplate().find("select c from CampaignConfig c where c.campaignId = ?  and configType = ?", campaignId, type);
    }
}
