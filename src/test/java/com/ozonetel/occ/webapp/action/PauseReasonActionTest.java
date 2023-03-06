package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.PauseReasonManager;
import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class PauseReasonActionTest extends BaseActionTestCase {
    private PauseReasonAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new PauseReasonAction();
        PauseReasonManager pauseReasonManager = (PauseReasonManager) applicationContext.getBean("pauseReasonManager");
        action.setPauseReasonManager(pauseReasonManager);
    
        // add a test pauseReason to the database
        PauseReason pauseReason = new PauseReason();

        // enter all required fields

        pauseReasonManager.save(pauseReason);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getPauseReasons().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getPauseReason());
        assertEquals("success", action.edit());
        assertNotNull(action.getPauseReason());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getPauseReason());

        PauseReason pauseReason = action.getPauseReason();
        // update required fields

        action.setPauseReason(pauseReason);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        PauseReason pauseReason = new PauseReason();
        pauseReason.setId(-2L);
        action.setPauseReason(pauseReason);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}