package com.ozonetel.occ.service.impl;

import com.google.gson.JsonObject;
import com.ozonetel.occ.dao.CallBackDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.model.CallbackScheduleDetails;
import com.ozonetel.occ.model.CallbackScheduleDetailsGroup;
import com.ozonetel.occ.model.CallbacksGrouped;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.UserManager;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.callBackManager;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.util.HttpUtils;
import com.ozonetel.occ.util.KookooUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import javax.jws.WebService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

@WebService(serviceName = "CallBackService", endpointInterface = "com.ozonetel.occ.service.CallBackManager")
public class CallBackManagerImpl extends GenericManagerImpl<CallBack, Long> implements CallBackManager, MessageSourceAware {

    public CallBackManagerImpl(CallBackDao callBackDao) {
        super(callBackDao);
        this.callBackDao = callBackDao;
    }

    @Override
    public List<CallBack> getCallbackByAgentAndCurrentDate(String username, String agentId, Date serverDate) {
        User user = userManager.getUserByUsername(username);
        Calendar calendar = Calendar.getInstance();
        if (user.getUserTimezone() != null && !user.getUserTimezone().trim().isEmpty()) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(user.getUserTimezone()));
        }
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();//--> user start date
        // ----> Server start date
        startDate = DateUtil.convertFromOneTimeZoneToOhter(startDate, user.getUserTimezone(), user.getServerTimezone());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();//--> user end date
        // ---> Server end date
        endDate = DateUtil.convertFromOneTimeZoneToOhter(endDate, user.getUserTimezone(), user.getServerTimezone());
        return callBackDao.getCallbackByAgentAndCurrentDate(username, agentId, startDate, endDate);
    }

    private List<Map<String, Object>> getCallbacksFromProcedure(String username, String agentId) {
        User user = userManager.getUserByUsername(username);

        Calendar calendar = StringUtils.isBlank(user.getUserTimezone())
                ? Calendar.getInstance() : Calendar.getInstance(TimeZone.getTimeZone(user.getUserTimezone()));
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String startDateString = DateUtil.convertDateToString(calendar.getTime(), DateUtil.ZODA_FORMAT, calendar.getTimeZone().getID());
       // startDateString = DateUtil.zodaConvertDateStringFromOneZoneToOtherZoneString(startDateString, user.getUserTimezone(), user.getServerTimezone(), "yyyy-MM-dd HH:mm:ss");

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        // ---> Server end date
        String endDateString = DateUtil.convertDateToString(calendar.getTime(), DateUtil.ZODA_FORMAT, calendar.getTimeZone().getID());
       // endDateString = DateUtil.zodaConvertDateStringFromOneZoneToOtherZoneString(endDateString, user.getUserTimezone(), user.getServerTimezone(), "yyyy-MM-dd HH:mm:ss");
        log.debug("Fetching callbacks for user:" + username + " agent:" + agentId + " | Starttime:" + startDateString + " | end date:" + endDateString);
        return callBackDao.getCallbacksFromProcedure(user.getId(), agentId, startDateString, endDateString);
    }

    public CallBack getCallBackByPhoneNumber(String number) {
        List<CallBack> list = callBackDao.getCallBackByPhoneNumber(number);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public CallBack getCallBackByPhoneNumberAndAgentId(Long userId, String number, String agentId) {
        return callBackDao.getCallBackByPhoneNumberAndAgentId(userId, number, agentId);
    }

    private String toToolBarCallbackString(CallBack callback) {
        StringBuilder s = new StringBuilder();

        s.append(callback.getId())
                .append("~").append(callback.getCallbackDate() != null ? DateUtil.getDateTime("dd-MM-yyyy H:mm", callback.getCallbackDate(), callback.getUser().getUserTimezone()) : "");
        if (callback.getCampaign() != null) {
            s.append("~").append(callback.getCampaign().getCampignName());
        } else {
            s.append("~").append("").append("~").append("");
        }

        s.append("~").append(callback.getCallbackNumber()).append("~").append(callback.getComments()).append("~").append(callback.getDateCreated() != null ? DateUtil.getDateTime("dd-MM-yyyy H:mm", callback.getDateCreated(), callback.getUser().getUserTimezone()) : "");
        s.append("~").append(callback.getRescheduleComment() == null ? "" : callback.getRescheduleComment()).append("~").append(callback.getDateRescheduled() != null ? DateUtil.getDateTime("dd-MM-yyyy H:mm", callback.getDateRescheduled(), callback.getUser().getUserTimezone()) : " ");

        return s.toString();

    }

    private String newToolbarCallbackAsString(Map<String, Object> callback) {
        StringBuilder s = new StringBuilder();

        s.append(callback.get("id"))
                .append("~").append(callback.get("CallBackDate"))
                .append("~").append(callback.get("CampaignName"))
                .append("~").append(callback.get("CallBackNumber"))
                .append("~").append(ObjectUtils.toString(callback.get("Comments"), ""))
                .append("~").append(callback.get("DateCreated"))
                .append("~").append(ObjectUtils.toString(callback.get("Reschedule_Comment"), ""))
                .append("~").append(ObjectUtils.toString(callback.get("Date_Rescheduled"), ""));
        log.debug("Callback details from procedure : "+s.toString());
        return s.toString();

    }

    private List<CallbackAsString> getCallbackAsStrings(String username, String agentId) throws ParseException {
        //User user = userManager.getUserByUsername(username);
//        List<CallBack> callBackList = getCallbackByAgentAndCurrentDate(username, agentId, DateUtil.convertFromOneTimeZoneToOhter(new Date(), user.getUserTimezone(), user.getServerTimezone()));
        //List<CallBack> callBackList = getCallbackByAgentAndCurrentDate(username, agentId, new Date());
        List<Map<String, Object>> callBackList = getCallbacksFromProcedure(username, agentId);
        log.debug("Got callbacks for user:" + username + " | agent:" + agentId + " -> " + callBackList);
        List<CallbackAsString> callbackAsStrings = new ArrayList<>();
        for (Map<String, Object> callBack : callBackList) {
            callbackAsStrings.add(new CallbackAsString(
                    // DateUtil.convertFromOneTimeZoneToOhter(callBack.getCallbackDate(), user.getServerTimezone(), user.getUserTimezone()),
                    //DateUtil.getDateTime("dd-MM-yyyy H:mm", callBack.get("CallBackDate"), user.getUserTimezone()),
                    DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", callBack.get("CallBackDate").toString()),
                    newToolbarCallbackAsString(callBack)));
        }

        return callbackAsStrings;
    }

    public List<String> getCallBackList(String username, String agentId) {
        //FIXME this method is not used so not converting date below.
        List<CallBack> callBackList = getCallbackByAgentAndCurrentDate(username, agentId, new Date());
        List<String> l = new ArrayList();
        for (CallBack callBack : callBackList) {
            l.add(callBack.toString());
        }

        return l;
    }

    @Override
    public boolean deleteCallback(String username, String agentId, Long cbId) {
        CallBack callBack = callBackDao.get(cbId);
        if (callBack != null) {
            callBack.setDateDeleted(new Date());
            callBack.setDeleteComment("Deleted  by agent:" + agentId);

            callBack.setDeleted(true);
            callBackDao.save(callBack);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public List<CallbacksGrouped> tbGetGroupedCallBackList(String username, String agentId) {
        List<CallbacksGrouped> callbacksGroupedList = new ArrayList<>();
        try {
            List<CallbackAsString> callbackAsStrings = getCallbackAsStrings(username, agentId);

            if (callbackAsStrings != null && !callbackAsStrings.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(callbackAsStrings.get(0).getDate());
                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                calendar.setTime(callbackAsStrings.get(callbackAsStrings.size() - 1).getDate());
                int endHour = calendar.get(Calendar.HOUR_OF_DAY) < 24 ? calendar.get(Calendar.HOUR_OF_DAY) + 1 : calendar.get(Calendar.HOUR_OF_DAY);
                int minHour = (startHour % 2 == 0) ? startHour : (startHour - 1);

                while (minHour < endHour) {
                    callbacksGroupedList.add(new CallbacksGrouped(minHour, minHour + 2));
                    minHour = minHour + 2;
                }

                List<CallbackAsString> addedList = new ArrayList<>();
                Calendar currentTime = Calendar.getInstance();

                for (CallbacksGrouped callbacksGrouped : callbacksGroupedList) {
//                log.trace("Checking group:" + callbacksGrouped);
                    //
                    // ------ > Remove already added list from iteration.
                    callbackAsStrings.removeAll(addedList);
                    addedList = new ArrayList<>();
//                log.trace("Remaining list:"+callbackAsStrings);
                    for (CallbackAsString callbackAsString : callbackAsStrings) {
                        currentTime.setTime(callbackAsString.getDate());
                        if (currentTime.get(Calendar.HOUR_OF_DAY) >= callbacksGrouped.getMinTime()
                                && currentTime.get(Calendar.HOUR_OF_DAY) < callbacksGrouped.getMaxTime()) {
                            callbacksGrouped.getCallbackDetails().add(callbackAsString.getData());
                            callbacksGrouped.setCount(callbacksGrouped.getCount() + 1);
                            addedList.add(callbackAsString);
                        } else {
                            break;
                        }

                    }
                }

                int i = 0;
                for (Iterator<CallbacksGrouped> it = callbacksGroupedList.iterator(); it.hasNext();) {
                    CallbacksGrouped callbacksGrouped = it.next();

                    if (callbacksGrouped.getCount() == 0) {
                        it.remove();
                    } else {
                        if (i == 0) {
                            callbacksGrouped.setMinTime(startHour);
                        }
                        callbacksGrouped.setSno(i++);
                    }

                }

            }

        } catch (Exception e) {
            log.error("Callback exception|" + e.getMessage(), e);

        }
        log.debug("Call backs grouped:" + callbacksGroupedList);

        return callbacksGroupedList;
    }

    @Override
    public List<CallbackScheduleDetailsGroup> getCallbackScheduleDetails(String username, String agentId) {

        List<CallbackScheduleDetails> callbackScheduleDetailsList = callBackDao.getTodayCallbackScheduleDetailsByAgent(username, agentId);
        Map<Integer, List<CallbackScheduleDetails>> hourlyMap = new LinkedHashMap<>();
        if (callbackScheduleDetailsList != null && !callbackScheduleDetailsList.isEmpty()) {

            //Group hour wise.
            int hour;
            Calendar calendar = Calendar.getInstance();
            for (CallbackScheduleDetails tmpDetails : callbackScheduleDetailsList) {
                calendar.setTime(tmpDetails.getCallbackDate());
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hourlyMap.get(hour) == null) {
                    hourlyMap.put(hour, new ArrayList<CallbackScheduleDetails>());
                }
                hourlyMap.get(hour).add(tmpDetails);
            }

            List<CallbackScheduleDetailsGroup> groupedCallbacks = new ArrayList<>();

            List<Integer> keyList = new ArrayList<>(hourlyMap.keySet());

            CallbackScheduleDetailsGroup callbacksGrouped;
            for (int i = 0; i < keyList.size(); i++) {
                callbacksGrouped = new CallbackScheduleDetailsGroup(keyList.get(i), keyList.get(i));
                callbacksGrouped.getCallbackScheduleDetailsList().addAll(hourlyMap.get(keyList.get(i)));
                if (((i + 1) < keyList.size()) && (keyList.get(i + 1) == keyList.get(i) + 1)) {
                    ++i;
                    callbacksGrouped.setMaxHour(keyList.get(i) + 1);
                    callbacksGrouped.getCallbackScheduleDetailsList().addAll(hourlyMap.get(keyList.get(i)));

                }
                groupedCallbacks.add(callbacksGrouped);
            }
            return groupedCallbacks;

        }

        return null;
    }

    @Override
    public JsonObject resechduleCallback(String username, String agentId, String time, Long callbackId, String rescheduleComment,String callBackTz) {
        JsonObject respJsonObject = new JsonObject();
        respJsonObject.addProperty("status", "Success");
        try {
            CallBack cb = callBackDao.getCallbackById(username, callbackId);
            User user = cb.getUser();
           // cb.setCallbackDate(DateUtil.convertFromOneTimeZoneToOhter(DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", time), user.getUserTimezone(), user.getServerTimezone()));
            log.debug("resechduleCallback time came : "+time);
            cb.setCallbackDate(DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", time));
            cb.setCallbackTz(callBackTz);
            cb.setCalled(false);
            cb.setRescheduleComment(StringUtils.abbreviate(rescheduleComment, 255));
            cb.setDateRescheduled(new Date());
            cb = callBackDao.save(cb);
            respJsonObject.addProperty("message", "Number " + cb.getCallbackNumber() + " has been rescheduled to:" + DateUtil.convertDateToString(cb.getCallbackDate(), "E, dd MMM yyyy HH:mm:ss z", user.getUserTimezone()));
            log.info("Call back rescheduled :" + cb);
        } catch (ParseException pe) {
            respJsonObject.addProperty("status", "Fail");
            respJsonObject.addProperty("message", "Invalid Time");
            log.error(pe.getMessage(), pe);
        }
        return respJsonObject;
    }

    @Override
    public StatusMessage rescheduleCallback(String username, String agentId, Date newDate, Long callbackId, String rescheduleComment,String callBackTz) {
        CallBack cb = callBackDao.getCallbackById(username, callbackId);
        cb.setCallbackDate(newDate);
        cb.setCalled(false);
        cb.setRescheduleComment(StringUtils.abbreviate(rescheduleComment, 255));
        cb.setDateRescheduled(new Date());
        cb.setCallbackTz(callBackTz);
        cb = callBackDao.save(cb);
        log.info("Call back rescheduled :" + cb);
        return new StatusMessage(Status.SUCCESS, messageSource.getMessage("callback.rescheduled", new Object[]{cb.getCallbackNumber(), cb.getCallbackDate()}, Locale.getDefault()));
    }

    @Override
    public String failCallback(String username, String agentId, Long callbackId) {
        CallBack cb = callBackDao.getCallbackById(username, callbackId);
        cb.setCalled(false);
        callBackDao.save(cb);
        return "Success";
    }

    @Override
    public StatusMessage callBackDial(String user, String agentId, String agentPhoneNumber, Long callBackId) {

        CallBack callBack = callBackManager.get(callBackId);
        if (callBack != null) {

            String customerNumber = callBack.getCallbackNumber();
            String did = callBack.getCampaign().getdId();
            String campaignId = callBack.getCampaign().getCampaignId().toString();
            String username = user;

            AppProperty appProperty = (AppProperty) getBean("appProperty");
            String kookooOutBoundUrl = appProperty.getKooKooOutBoundUrl();
            StringBuilder callBackUrl = new StringBuilder();
            callBackUrl.append(appProperty.getKooKooCallBackUrl())
                    .append("OutBoundCallBack?cAgentId=").append(agentId)
                    .append("&cDialNumber=").append(agentPhoneNumber);

            UserManager userManager = (UserManager) getBean("userManager");
            String apiKey = userManager.getUserByUsername(username).getApiKey();
            StringBuilder outBoundAppUrl = new StringBuilder();
            outBoundAppUrl.append(appProperty.getPreviewDialerUrl());
            outBoundAppUrl.append("?cAgentId=").append(agentId)
                    .append("&cAgentPhoneNumber=").append(agentPhoneNumber)
                    .append("&cType=").append("ToolBarManual") //type is Preview or Inbound or Progressive or manual
                    .append("&cDid=").append(did)
                    .append("&cDialNumber=").append(customerNumber)
                    .append("&campaignId=").append(campaignId)
                    .append("&priority=1")
                    .append("&cCampAgentWise=").append(true);

            StringBuilder kUrl = new StringBuilder();

            try {
                URIBuilder kookooOutboundURIBuilder = new URIBuilder(kookooOutBoundUrl);
                kookooOutboundURIBuilder.addParameter("phone_no", agentPhoneNumber);
                kookooOutboundURIBuilder.addParameter("api_key", apiKey);
                kookooOutboundURIBuilder.addParameter("caller_id", did);
                kookooOutboundURIBuilder.addParameter("callback_url", callBackUrl.toString());
                kookooOutboundURIBuilder.addParameter("url", outBoundAppUrl.toString());
                kUrl = new StringBuilder(kookooOutboundURIBuilder.build().toString());
            } catch (URISyntaxException ex) {
                log.error(ex.getMessage(), ex);

                kUrl.append(kookooOutBoundUrl)
                        .append("?phone_no=").append(URLEncoder.encode(agentPhoneNumber))
                        .append("&api_key=").append(apiKey)
                        .append("&caller_id=").append(did)
                        .append("&priority=1")
                        .append("&callback_url=").append(URLEncoder.encode(callBackUrl.toString()))
                        .append("&url=").append(URLEncoder.encode(outBoundAppUrl.toString()));
            }

            HttpResponseDetails httpResponseDetails = null;
            try {
                httpResponseDetails = HttpUtils.doGet(kUrl.toString());
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }

            StatusMessage statusMessage = KookooUtils.parseKookooResponse(httpResponseDetails.getResponseBody());

            if (StringUtils.equalsIgnoreCase(statusMessage.getStatus().toString(), "queued")) {//this means
                UpdateReport r = new UpdateReport();
                r.updateCallStatus(statusMessage.getMessage(), statusMessage.getMessage(), did, agentId, customerNumber, statusMessage.getStatus().toString(), "", "0", "AgentHangup", "Manual Dial", "false", "AgentDial", "ToolBarManual", 0, statusMessage.getMessage(), false, "", "", "", campaignId, new Long(0), new Long(0), new Date(), new Date(), new Date(), new Date(), null);

                callBack.setCalled(true);
                callBackManager.save(callBack);
            }
            return statusMessage;
        } else {
            log.error("Got Callback as " + callBack + " for callback id:" + callBackId);
            return new StatusMessage(Status.ERROR, messageSource.getMessage("error.invalid.callbackId", null, Locale.getDefault()));
        }

    }

    public Object getBean(String name) {

        ApplicationContext ctx = AppContext.getApplicationContext();
        return ctx.getBean(name);
    }
//String ucid, String agentMonitorUcid, String did, String agentId, String callerId, String callStatus, String audioFile, String skillName, String hangUpBy, String uui, String isCompleted, String fallBackRule, String type, int transferType, String dialStatus, boolean callCompleted, String dataId, String customerStatus, String agentStatus, String campaignId

    public boolean sendDataToCustomer(
            final String agentMonitorUcid, final String username) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    AppProperty appProperty = (AppProperty) getBean("appProperty");
                    String callBackurl = appProperty.getAdminPortalUrl();
                    StringBuilder callBackToCust = new StringBuilder();

                    callBackToCust.append(callBackurl).append("/executeCallback.html?").append("ucid=").append(agentMonitorUcid).append("&username=").append(username);
                    HttpClient msuClient = new HttpClient();
                    HttpMethod method = new GetMethod(callBackToCust.toString());
                    try {
                        msuClient.executeMethod(method);
                    } catch (Exception e) {
                        log.error("[" + agentMonitorUcid + "][" + username + "] Unable to Connect Admin Portal to send Data to Customer due to some Errors..");
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
    }

    @Override
    public boolean scheduleCallback(String customerNumber, String comment, Date callbackDate, Campaign campaign, Agent agent) {
        CallBack callBack = getCallBackByPhoneNumberAndAgentId(campaign.getUser().getId(), customerNumber, agent.getAgentId());
        if (callBack == null) {
            callBack = new CallBack();
        } else {
            callBack.setDeleted(false);
            callBack.setDeleteComment(null);
            callBack.setDateDeleted(null);
        }
        callBack.setCallbackNumber(customerNumber);
        callBack.setComments(comment);
        callBack.setUser(campaign.getUser());
        callBack.setCallbackDate(callbackDate);
        callBack.setCampaign(campaign);
        callBack.setCalled(false);
        callBack.setDateCreated(new Date());

        callBack.setAgent(agent);
        callBack = save(callBack);
        log.debug("<-Saved call back:" + callBack.toShortString());
        return true;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    private class CallbackAsString {

        Date date;
        String data;

        public CallbackAsString(Date date, String data) {
            this.date = date;
            this.data = data;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + Objects.hashCode(this.date);
            hash = 17 * hash + Objects.hashCode(this.data);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CallbackAsString other = (CallbackAsString) obj;
            if (!Objects.equals(this.date, other.date)) {
                return false;
            }
            if (!Objects.equals(this.data, other.data)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CallbackAsString{" + "date=" + date + ", data=" + data + '}';
        }
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    private CallBackDao callBackDao;
    private MessageSource messageSource;
    private AppProperty appProperty;
    private UserManager userManager;
}
