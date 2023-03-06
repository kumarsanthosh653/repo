package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.CallDispositionManager;
import com.ozonetel.occ.model.CallDisposition;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class CallDispositionActionTest extends BaseActionTestCase {
    private CallDispositionAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new CallDispositionAction();
        CallDispositionManager callDispositionManager = (CallDispositionManager) applicationContext.getBean("callDispositionManager");
        action.setCallDispositionManager(callDispositionManager);
    
        // add a test callDisposition to the database
        CallDisposition callDisposition = new CallDisposition();

        // enter all required fields

        callDispositionManager.save(callDisposition);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getCallDispositions().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
       /* action.setId(-1L);
        assertNull(action.getCallDisposition());
        assertEquals("success", action.edit());
        assertNotNull(action.getCallDisposition());
        assertFalse(action.hasActionErrors());*/
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
/*        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getCallDisposition());

        CallDisposition callDisposition = action.getCallDisposition();
        // update required fields

        action.setCallDisposition(callDisposition);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
 * 
 */
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        CallDisposition callDisposition = new CallDisposition();
        callDisposition.setId(-2L);
/*        action.setCallDisposition(callDisposition);
        assertEquals("success", action.delete());
 * 
 */
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}