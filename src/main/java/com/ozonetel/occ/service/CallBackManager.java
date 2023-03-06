package com.ozonetel.occ.service;

import com.google.gson.JsonObject;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.model.CallbackScheduleDetailsGroup;
import com.ozonetel.occ.model.CallbacksGrouped;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.StatusMessage;
import java.util.Date;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CallBackManager extends GenericManager<CallBack, Long> {

    List<CallBack> getCallbackByAgentAndCurrentDate(String username, String agentId, Date serverDate);

    public CallBack getCallBackByPhoneNumber(String number);

    public CallBack getCallBackByPhoneNumberAndAgentId(Long userId, String number, String agentId);

    boolean sendDataToCustomer(String agentMonitorUcid, String username);

    public List<String> getCallBackList(String username, String agentId);

    public List<CallbacksGrouped> tbGetGroupedCallBackList(String username, String agentId);

    public boolean deleteCallback(String username, String agentId, Long cbId);

    public JsonObject resechduleCallback(String username, String agentId, String time, Long callbackId, String rescheduleComment, String callBackTz);

    public StatusMessage rescheduleCallback(String username, String agentId, Date newDate, Long callbackId, String rescheduleComment,String callBackTz);

    public String failCallback(String username, String agentId, Long callbackId);

    /**
     * Schedules call back for the customer <code>customerNumber</code> with the
     * agent <code>agent</code>.
     *
     * @param customerNumber Customer number
     * @param comment comments while setting the disposition
     * @param callbackDate time of call
     * @param campaign campaign the call back is scheduled with
     * @param agent Agent containing user object
     * @return <code>true</code> if call back is scheduled <code>false</code>
     * otherwise.
     */
    public boolean scheduleCallback(String customerNumber, String comment, Date callbackDate, Campaign campaign, Agent agent);

    public List<CallbackScheduleDetailsGroup> getCallbackScheduleDetails(String username, String agentId);

    public StatusMessage callBackDial(String user, String agentId, String agentPhoneNumber, Long callBackId);
}
