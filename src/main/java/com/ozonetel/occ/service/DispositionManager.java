package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Disposition;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DispositionManager extends GenericManager<Disposition, Long> {

    List<Disposition> getDispositionsByCampaign(Long campaignId);

    public List<Disposition> getActiveDispositionsByCampaign(Long campaignId);

    /**
     * *
     * returns the Disposition list with out associated campaigns and users.
     * means disposition.getUser()/disposition.getCampaign() is null.
     *
     * @param campaignId
     * @return
     */
    public List<Disposition> getActiveDispositionsWithoutCampaigns(Long campaignId);

    public String setDispositionByApi(String dispositionCode, String dispComments, String ucid, String did,
            String customer, boolean pauseAfterDispose,  
            String agentId, String pauseReason, String apiKey, String responseType , String callBackTz);

}
