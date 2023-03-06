package com.ozonetel.occ.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.dao.CallBackDao;
import com.ozonetel.occ.model.CallbackScheduleDetails;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class CallBackDaoHibernate extends GenericDaoHibernate<CallBack, Long> implements CallBackDao {

    public CallBackDaoHibernate() {
        super(CallBack.class);
    }

    @Override
    public List<CallBack> getCallbackByAgentAndCurrentDate(String username, String agentId, Date startDate, Date endDate) {
        return (List<CallBack>) getHibernateTemplate().find("from CallBack c where c.callbackDate between ? and  ? and c.agent.agentId = ? and c.called = false and c.user.username = ? and c.deleted=false order by c.callbackDate", new Object[]{startDate, endDate, agentId, username});
    }

    @Override
    public List<Map<String, Object>> getCallbacksFromProcedure(Long userId, String agentId, String startDate, String endDate) {
        Map<String, Object> queryParams = new LinkedHashMap<>(4);
        queryParams.put("pUserID", userId);
        queryParams.put("pAgentID", agentId);
        queryParams.put("pStartTime", startDate);
        queryParams.put("pEndTime", endDate);
        //return executeProcedure("call Get_CallBackDetails(?,?,?,?)", queryParams);
         List<Map<String, Object>> callBackList =executeProcedure("call Get_CallBackDetailsV2(?,?,?,?)", queryParams);
         log.debug("Result from callback : "+callBackList);
         return callBackList;

    }

    @Override
    public List<CallbackScheduleDetails> getTodayCallbackScheduleDetailsByAgent(String username, String agentId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        return (List<CallbackScheduleDetails>) getHibernateTemplate().find("select new com.ozonetel.occ.model.CallbackScheduleDetails(c.id,c.callbackDate,c.campaign.campignName,c.callbackNumber,c.comments,c.dateCreated,c.rescheduleComment,c.dateRescheduled) from CallBack c"
                + " where c.agent.agentId=? and c.user.username=? and c.callbackDate >= ? and c.callbackDate <= ?  and c.called = false and c.deleted=false order by c.callbackDate asc ", agentId, username, startDate, calendar.getTime());
    }

    public List<CallBack> getCallBackByPhoneNumber(final String number) {

        return (List<CallBack>) getHibernateTemplate().find("from CallBack c where c.callbackNumber like '%" + number + "%'");
    }

    @Override
    public CallBack getCallBackByPhoneNumberAndAgentId(final Long userId, final String number, final String agentId) {

        Map<String, Object> params = new LinkedHashMap();
        params.put("userId", userId);
        params.put("agentId", agentId);
        params.put("number", number);
        List<Map<String, Object>> list = this.executeProcedure("{call Get_CallBackDetailsToAgent(?,?,?)} ", params);
        
        if (list == null || list.isEmpty())
            return null;
        
        Long id = Long.valueOf(list.get(0).get("id").toString());
        log.debug("callback id from procedure Get_CallBackDetailsToAgent : "+id);
        
        List<CallBack> callbacks = (List<CallBack>) getHibernateTemplate().find("from CallBack c where c.id =? ", id);

        if (CollectionUtils.isNotEmpty(callbacks)) {
            return callbacks.get(0);
        }
        return null;

    }

    @Override
    public CallBack getCallbackById(String username, Long id) {
        List<CallBack> callbacks = (List<CallBack>) getHibernateTemplate().find("SELECT c FROM CallBack c WHERE c.id=? AND c.user.username=?", id, username);
        if (callbacks != null && !callbacks.isEmpty()) {
            return callbacks.get(0);
        }

        return null;
    }

}
