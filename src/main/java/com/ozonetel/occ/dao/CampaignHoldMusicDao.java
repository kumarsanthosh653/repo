/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.CampaignHoldMusic;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public interface CampaignHoldMusicDao extends GenericDao<CampaignHoldMusic, Long>{
    public List<CampaignHoldMusic> getCampaignMusicByUser(String username);
    
    public List<CampaignHoldMusic> getCampaignHoldMusicByUser(String username);
    
    public List<CampaignHoldMusic> getCampaignTransferMusicByUser(String username);
    
    public List<CampaignHoldMusic> getAudioUrlById(Long id);
}
