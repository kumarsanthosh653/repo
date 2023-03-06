/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.HoldDetailDao;
import com.ozonetel.occ.model.CallHoldDetail;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author rajeshchary
 */
public class HoldDetailDaoHibernate extends GenericDaoHibernate<CallHoldDetail, Long> implements HoldDetailDao {

    public HoldDetailDaoHibernate() {
        super(CallHoldDetail.class);
    }

    @Override
    public CallHoldDetail getCallHoldDetail(BigInteger monitorUcid, String callerId) {
        List<CallHoldDetail> l = (List<CallHoldDetail>)getHibernateTemplate().find("select h from CallHoldDetail h where h.monitorUcid = " + monitorUcid + " and h.callerNumber='" + callerId + "' order by id desc");
        if (l.size() > 0) {
            return l.get(0);
        } else {
            return null;
        }
    }

    @Override
    public CallHoldDetail getCallHoldDetailByUCID(BigInteger ucid, String callerId) {
        List<CallHoldDetail> l = (List<CallHoldDetail>)getHibernateTemplate().findByNamedParam("select h from CallHoldDetail h where h.ucid = :ucid and h.callerNumber= :phoneNumber order by h.id desc", new String[]{"ucid","phoneNumber"},new Object[]{ucid, callerId});
        if (l.size() > 0) {
            return l.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int updateEndtimeWhoseEndtimeIsNull(BigInteger monitorUcid, Date endTime) {
        return getHibernateTemplate().bulkUpdate("update CallHoldDetail c set c.endTime=? where c.monitorUcid=? and c.endTime is NULL", endTime, monitorUcid);
    }

}
