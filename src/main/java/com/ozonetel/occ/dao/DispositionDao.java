package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.Disposition;
import java.util.List;

/**
 * An interface that provides a data management interface to the Disposition table.
 */
public interface DispositionDao extends GenericDao<Disposition, Long> {

    List<Disposition> getDispositionsByCampaign(Long campaignId);
    
    /***
     * returns the Disposition list with out associated campaigns and users.
     * means disposition.getUser()/disposition.getCampaign() is null.
     * @param campaignId
     * @return 
     */
    public List<Disposition> getActiveDispositionsWithoutCampaigns(Long campaignId);
}