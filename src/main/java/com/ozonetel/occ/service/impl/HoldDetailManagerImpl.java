/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.HoldDetailDao;
import com.ozonetel.occ.model.CallHoldDetail;
import com.ozonetel.occ.service.HoldDetailManager;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author rajeshchary
 */
public class HoldDetailManagerImpl extends GenericManagerImpl<CallHoldDetail, Long> implements HoldDetailManager {

    HoldDetailDao holdDetailDao;

    public HoldDetailManagerImpl(HoldDetailDao holdDetailDao) {
        super(holdDetailDao);
        this.holdDetailDao = holdDetailDao;
    }

    @Override
    public CallHoldDetail getCallHoldDetail(BigInteger monitorUcid, String callerId) {
        return holdDetailDao.getCallHoldDetail(monitorUcid, callerId);
    }

    @Override
    public CallHoldDetail getCallHoldDetailByUCID(BigInteger ucid, String callerId) {
        return holdDetailDao.getCallHoldDetailByUCID(ucid, callerId);
    }

    @Override
    public int updateEndtimeWhoseEndtimeIsNull(BigInteger monitorUcid, Date endTime) {
        return holdDetailDao.updateEndtimeWhoseEndtimeIsNull(monitorUcid, endTime);
    }

}
