package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.CallEvent;
import com.ozonetel.occ.service.CallbacksExecutorService;
import com.ozonetel.occ.service.ImmediateCallbacksExecutorService;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.campaignManager;
import com.ozonetel.occ.util.CallListenerAdapter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class ImmediateCallbacksExecutorServiceImpl extends CallListenerAdapter implements ImmediateCallbacksExecutorService {
    
    @Override
    public void callCompleted(CallEvent callEvent) {
        if (callEvent.getUser() != null && StringUtils.isNotBlank(callEvent.getUser().getCallBackUrl())) {
            try {// --> Retrying allowed no.of tries for the customer callbacks.
                boolean sendCallbackImmediately = false;
                Map<String, Object> params = new LinkedHashMap();
                params.put("user_id", callEvent.getUser().getId());
                params.put("param_name", "IMMEDIATE_CALLBACK");
                List settings = campaignManager.executeProcedure("call Get_UserParamter(?,?)", params);
                if (settings != null && !settings.isEmpty()) {
                    Map item = (Map) settings.get(0);
                    sendCallbackImmediately = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                            ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                }
                
                if (sendCallbackImmediately) {
                    logger.debug("Sending callback on call complete event :" + callEvent);
                    callbacksExecutorService.sendCallbackDetails(callEvent.getUser().getCallBackUrl(), callEvent.getMonitorUcid(), false, null, null);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
    public void setCallbacksExecutorService(CallbacksExecutorService callbacksExecutorService) {
        this.callbacksExecutorService = callbacksExecutorService;
    }
    
    private Logger logger = Logger.getLogger(ImmediateCallbacksExecutorServiceImpl.class);
    private CallbacksExecutorService callbacksExecutorService;
}
