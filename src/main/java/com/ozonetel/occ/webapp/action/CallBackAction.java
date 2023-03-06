package com.ozonetel.occ.webapp.action;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentAlertsManager;
import com.ozonetel.occ.service.impl.Status;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class CallBackAction extends BaseAction implements Preparable {

    private CallBackManager callBackManager;
    private List callBacks;
    private CallBack callBack;
    private Long id;
    private AgentManager agentManager;
    private String callbackDetails;
    private AgentAlertsManager agentAlertsManager;
    private static Logger logger = Logger.getLogger(CallBackAction.class);
    private StatusMessage statusMessage;

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setAgentAlertsManager(AgentAlertsManager agentAlertsManager) {
        this.agentAlertsManager = agentAlertsManager;
    }

    public void setCallbackDetails(String callbackDetails) {
        this.callbackDetails = callbackDetails;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public List<Agent> getAgentList() {
        return agentManager.getAll();
    }

    public void setCallBackManager(CallBackManager callBackManager) {
        this.callBackManager = callBackManager;
    }

    public List getCallBacks() {
        return callBacks;
    }

    /**
     * Grab the entity from the database before populating with request
     * parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String callBackId = getRequest().getParameter("callBack.id");
            if (callBackId != null && !callBackId.equals("")) {
                callBack = callBackManager.get(new Long(callBackId));
            }
        }
    }

    public String list() {
        callBacks = callBackManager.getAll();
        return SUCCESS;
    }

    /**
     * This sends the alerts to the agents who has call backs.
     *
     * @return
     */
    public String sendCallbackAlerts() {
        logger.debug("Call back details:" + callbackDetails);
        statusMessage = new StatusMessage(Status.ERROR, "I feel blank");

        Type collectionType = new TypeToken<List<Map>>() {
        }.getType();
        List<Map> callbackAlertList = new Gson().fromJson(callbackDetails, collectionType);
        String alertMsg;
        for (Map<String, Object> callbackAlert : callbackAlertList) {
            alertMsg = "";
            if (Integer.parseInt(callbackAlert.get("NotDialed").toString()) != 0) {
                alertMsg = getText("msg.pending.callbackAlert", new String[]{callbackAlert.get("NotDialed").toString()});
            }

            if (Integer.parseInt(callbackAlert.get("ToBeDialed").toString()) != 0) {
                alertMsg += getText("msg.tobedial.callbackAlert", new String[]{callbackAlert.get("ToBeDialed").toString()});
            }

            boolean status = agentAlertsManager.alertAgent(callbackAlert.get("UserName").toString(),
                    callbackAlert.get("AgentID").toString(),
                    callbackAlert.get("ClientId").toString(), "Callback Alert", alertMsg);

            if (status) {
                statusMessage = new StatusMessage(Status.SUCCESS, "Sent callback alerts.");
            }

        }
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public String delete() {
        callBackManager.remove(callBack.getId());
        saveMessage(getText("callBack.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            callBack = callBackManager.get(id);
        } else {
            callBack = new CallBack();
        }

        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }

        boolean isNew = (callBack.getId() == null);

        callBackManager.save(callBack);

        String key = (isNew) ? "callBack.added" : "callBack.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}
