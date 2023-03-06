package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class CallQueueActionTest extends BaseActionTestCase {
    private CallQueueAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new CallQueueAction();
        CallQueueManager callQueueManager = (CallQueueManager) applicationContext.getBean("callQueueManager");
        action.setCallQueueManager(callQueueManager);
    
        // add a test callQueue to the database
        CallQueue callQueue = new CallQueue();

        // enter all required fields

        callQueueManager.save(callQueue);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getCallQueues().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getCallQueue());
        assertEquals("success", action.edit());
        assertNotNull(action.getCallQueue());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getCallQueue());

        CallQueue callQueue = action.getCallQueue();
        // update required fields

        action.setCallQueue(callQueue);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        CallQueue callQueue = new CallQueue();
//        callQueue.setId();
        action.setCallQueue(callQueue);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}