package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class DispositionActionTest extends BaseActionTestCase {
    private DispositionAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new DispositionAction();
        DispositionManager dispositionManager = (DispositionManager) applicationContext.getBean("dispositionManager");
        action.setDispositionManager(dispositionManager);
    
        // add a test disposition to the database
        Disposition disposition = new Disposition();

        // enter all required fields

        dispositionManager.save(disposition);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getDispositions().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getDisposition());
        assertEquals("success", action.edit());
        assertNotNull(action.getDisposition());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getDisposition());

        Disposition disposition = action.getDisposition();
        // update required fields

        action.setDisposition(disposition);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        Disposition disposition = new Disposition();
        disposition.setId(-2L);
        action.setDisposition(disposition);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}