/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import com.ozonetel.occ.model.CampaignConfig;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public interface CampaignConfigManager  extends GenericManager<CampaignConfig, Long> {
    
        public List<CampaignConfig> getCampaignConfigByCampaignId(Long campaignId);
        
        public List<CampaignConfig> getCampaignConfigByCampaignIdAndType(Long campaignId, String type);
}
