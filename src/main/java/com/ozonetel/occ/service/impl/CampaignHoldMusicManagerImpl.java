/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.CampaignHoldMusicDao;
import com.ozonetel.occ.model.CampaignHoldMusic;
import com.ozonetel.occ.service.CampaignHoldMusicManager;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public class CampaignHoldMusicManagerImpl extends GenericManagerImpl<CampaignHoldMusic, Long> implements CampaignHoldMusicManager{
    public CampaignHoldMusicManagerImpl(CampaignHoldMusicDao campaignHoldMusicDao) {
        super(campaignHoldMusicDao);
        this.campaignHoldMusicDao = campaignHoldMusicDao;
    }

    @Override
    public List<CampaignHoldMusic> getCampaignMusicByUser(String username) {
        
        
        return campaignHoldMusicDao.getCampaignMusicByUser(username);
    }
    
        public List<CampaignHoldMusic> getCampaignHoldMusicByUser(String username) {
        
        
        return campaignHoldMusicDao.getCampaignHoldMusicByUser(username);
    }
    
        @Override
    public List<CampaignHoldMusic> getCampaignTransferMusicByUser(String username) {
        
        
        return campaignHoldMusicDao.getCampaignTransferMusicByUser(username);
    }
    
    @Override
    public List<CampaignHoldMusic> getAudioUrlById(Long id){
        return campaignHoldMusicDao.getAudioUrlById(id);
    }
    




    private final CampaignHoldMusicDao campaignHoldMusicDao;
}
