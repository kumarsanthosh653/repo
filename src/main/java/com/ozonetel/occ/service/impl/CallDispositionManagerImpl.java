package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.CallDispositionDao;
import com.ozonetel.occ.model.CallDisposition;
import com.ozonetel.occ.service.CallDispositionManager;
import com.ozonetel.occ.service.impl.GenericManagerImpl;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "CallDispositionService", endpointInterface = "com.ozonetel.occ.service.CallDispositionManager")
public class CallDispositionManagerImpl extends GenericManagerImpl<CallDisposition, Long> implements CallDispositionManager {
    CallDispositionDao callDispositionDao;

    public CallDispositionManagerImpl(CallDispositionDao callDispositionDao) {
        super(callDispositionDao);
        this.callDispositionDao = callDispositionDao;
    }

	public List<CallDisposition> getDispositionsByCampaign(Long campaignId2) {
		return callDispositionDao.getDispositionsByCampaign(campaignId2);
	}

    
}