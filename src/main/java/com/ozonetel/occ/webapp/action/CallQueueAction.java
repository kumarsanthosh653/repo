package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;

public class CallQueueAction extends BaseAction implements Preparable {
    private CallQueueManager callQueueManager;
    private List callQueues;
    private CallQueue callQueue;
    private Long  id;

    public void setCallQueueManager(CallQueueManager callQueueManager) {
        this.callQueueManager = callQueueManager;
    }

    public List getCallQueues() {
        return callQueues;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String callQueueId = getRequest().getParameter("callQueue.id");
            if (callQueueId != null && !callQueueId.equals("")) {
                callQueue = callQueueManager.get(new Long(callQueueId));
            }
        }
    }

    public String list() {
        callQueues = callQueueManager.getAll();
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public CallQueue getCallQueue() {
        return callQueue;
    }

    public void setCallQueue(CallQueue callQueue) {
        this.callQueue = callQueue;
    }

    public String delete() {
        callQueueManager.remove(callQueue.getCallQueuePK().getUcid());

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            callQueue = callQueueManager.get(id);
        } else {
            callQueue = new CallQueue();
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

        boolean isNew = (callQueue.getCallQueuePK().getUcid() == null);

        callQueueManager.save(callQueue);

        String key = (isNew) ? "callQueue.added" : "callQueue.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}