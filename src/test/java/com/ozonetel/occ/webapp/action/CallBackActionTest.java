package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class CallBackActionTest extends BaseActionTestCase {
    private CallBackAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new CallBackAction();
        CallBackManager callBackManager = (CallBackManager) applicationContext.getBean("callBackManager");
        action.setCallBackManager(callBackManager);
    
        // add a test callBack to the database
        CallBack callBack = new CallBack();

        // enter all required fields

        callBackManager.save(callBack);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getCallBacks().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getCallBack());
        assertEquals("success", action.edit());
        assertNotNull(action.getCallBack());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getCallBack());

        CallBack callBack = action.getCallBack();
        // update required fields

        action.setCallBack(callBack);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        CallBack callBack = new CallBack();
        callBack.setId(-2L);
        action.setCallBack(callBack);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}