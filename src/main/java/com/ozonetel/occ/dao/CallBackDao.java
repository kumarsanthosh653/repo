package com.ozonetel.occ.dao;

import java.util.List;


import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.model.CallbackScheduleDetails;
import java.util.Date;
import java.util.Map;

/**
 * An interface that provides a data management interface to the CallBack table.
 */
public interface CallBackDao extends GenericDao<CallBack, Long> {

    List<CallBack> getCallbackByAgentAndCurrentDate(String username, String agentId, Date startDate, Date endDate);

    List<CallBack> getCallBackByPhoneNumber(String number);

    public CallBack getCallbackById(String username, Long id);

    public CallBack getCallBackByPhoneNumberAndAgentId(Long userId, String number, String agentId);
    
    public List<CallbackScheduleDetails> getTodayCallbackScheduleDetailsByAgent(String username, String agentId);
    
    public List<Map<String,Object>> getCallbacksFromProcedure(Long userId, String agentId, String startDate, String endDate);
}