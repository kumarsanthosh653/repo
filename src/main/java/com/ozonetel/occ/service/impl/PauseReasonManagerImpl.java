package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.PauseReasonDao;
import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.service.PauseReasonManager;
import com.ozonetel.occ.service.impl.GenericManagerImpl;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "PauseReasonService", endpointInterface = "com.ozonetel.occ.service.PauseReasonManager")
public class PauseReasonManagerImpl extends GenericManagerImpl<PauseReason, Long> implements PauseReasonManager {
    PauseReasonDao pauseReasonDao;

    public PauseReasonManagerImpl(PauseReasonDao pauseReasonDao) {
        super(pauseReasonDao);
        this.pauseReasonDao = pauseReasonDao;
    }

    public List<PauseReason> getPauseReasonByUser(String userName){
        return pauseReasonDao.getPauseReasonByUser(userName);
    }
}