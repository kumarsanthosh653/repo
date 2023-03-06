/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.CampaignConfigDao;
import com.ozonetel.occ.model.CampaignConfig;
import com.ozonetel.occ.service.CampaignConfigManager;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public class CampaignConfigManagerImpl extends GenericManagerImpl<CampaignConfig, Long>
        implements CampaignConfigManager {
    private final CampaignConfigDao campaignConfigDao;

    public CampaignConfigManagerImpl(CampaignConfigDao campaignConfigDao) {
        super(campaignConfigDao);
        this.campaignConfigDao = campaignConfigDao;
    }

    public List<CampaignConfig> getCampaignConfigByCampaignId(Long campaignId) {
        return campaignConfigDao.getCampaignConfigByCampaignId(campaignId);
    }
    
    public List<CampaignConfig> getCampaignConfigByCampaignIdAndType(Long campaignId, String type){
        return campaignConfigDao.getCampaignConfigByCampaignIdAndType(campaignId, type);
    }
}
