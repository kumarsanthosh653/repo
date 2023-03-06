/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import com.ozonetel.occ.model.CallHoldDetail;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author rajeshchary
 */
public interface HoldDetailManager extends GenericManager<CallHoldDetail, Long> {

    public CallHoldDetail getCallHoldDetail(BigInteger monitorUcid, String callerId);

    public CallHoldDetail getCallHoldDetailByUCID(BigInteger ucid, String callerId);

    public int updateEndtimeWhoseEndtimeIsNull(BigInteger monitorUcid, Date endTime);

}
