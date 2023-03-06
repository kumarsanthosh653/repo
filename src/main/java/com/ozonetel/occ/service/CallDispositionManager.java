package com.ozonetel.occ.service;

import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.model.CallDisposition;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CallDispositionManager extends GenericManager<CallDisposition, Long> {

	List<CallDisposition> getDispositionsByCampaign(Long campaignId2);
    
}