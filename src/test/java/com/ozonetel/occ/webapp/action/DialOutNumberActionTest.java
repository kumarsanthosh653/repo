package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.model.DialOutNumber;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class DialOutNumberActionTest extends BaseActionTestCase {
    private DialOutNumberAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new DialOutNumberAction();
        DialOutNumberManager dialOutNumberManager = (DialOutNumberManager) applicationContext.getBean("dialOutNumberManager");
        action.setDialOutNumberManager(dialOutNumberManager);
    
        // add a test dialOutNumber to the database
        DialOutNumber dialOutNumber = new DialOutNumber();

        // enter all required fields

        dialOutNumberManager.save(dialOutNumber);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getDialOutNumbers().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getDialOutNumber());
        assertEquals("success", action.edit());
        assertNotNull(action.getDialOutNumber());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getDialOutNumber());

        DialOutNumber dialOutNumber = action.getDialOutNumber();
        // update required fields

        action.setDialOutNumber(dialOutNumber);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        DialOutNumber dialOutNumber = new DialOutNumber();
        dialOutNumber.setId(-2L);
        action.setDialOutNumber(dialOutNumber);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}