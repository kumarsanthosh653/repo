/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.CampaignHoldMusicDao;
import com.ozonetel.occ.model.CampaignHoldMusic;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public class CampaignHoldMusicDaoHibernate extends GenericDaoHibernate<CampaignHoldMusic, Long> implements CampaignHoldMusicDao{
    public CampaignHoldMusicDaoHibernate() {
        super(CampaignHoldMusic.class);
    }

    @Override
    public List<CampaignHoldMusic> getCampaignMusicByUser(String username) {
        return getHibernateTemplate().find("select distinct l from CampaignHoldMusic l where l.user.username = ? ", username);
    }
    
        @Override
    public List<CampaignHoldMusic> getCampaignHoldMusicByUser(String username) {
        return getHibernateTemplate().find("select distinct l from CampaignHoldMusic l where l.user.username = ?", username);
    }
    
        @Override
    public List<CampaignHoldMusic> getCampaignTransferMusicByUser(String username) {
        return getHibernateTemplate().find("select distinct l from CampaignHoldMusic l where l.user.username = ?", username);
    }
    
    @Override
    public List<CampaignHoldMusic> getAudioUrlById(Long id){
        return getHibernateTemplate().find("select c from CampaignHoldMusic c where c.id = ?", id);
    }
}
