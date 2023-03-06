/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.IvrFlowDao;
import com.ozonetel.occ.model.IvrFlow;
import com.ozonetel.occ.service.IvrFlowManager;
import java.util.List;
import javax.jws.WebService;

/**
 *
 * @author rajeshdas
 */
@WebService(serviceName = "IvrFlowService", endpointInterface = "com.ozonetel.occ.service.IvrFlowManager")
public class IvrFlowManagerImpl extends GenericManagerImpl<IvrFlow, Long> implements IvrFlowManager {
    IvrFlowDao ivrFlowDao;

    public IvrFlowManagerImpl(IvrFlowDao ivrFlowDao) {
        super(ivrFlowDao);
        this.ivrFlowDao = ivrFlowDao;
    }
    
    public List<IvrFlow> getFeedbackIVRList(Long userId){
       return ivrFlowDao.getFeedbackIVRList(userId);
    }
}
