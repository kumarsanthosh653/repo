package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.DialNumberManager;
import com.ozonetel.occ.model.DialNumber;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class DialNumberActionTest extends BaseActionTestCase {
    private DialNumberAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new DialNumberAction();
        DialNumberManager dialNumberManager = (DialNumberManager) applicationContext.getBean("dialNumberManager");
        action.setDialNumberManager(dialNumberManager);
    
        // add a test dialNumber to the database
        DialNumber dialNumber = new DialNumber();

        // enter all required fields

        dialNumberManager.save(dialNumber);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getDialNumbers().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getDialNumber());
        assertEquals("success", action.edit());
        assertNotNull(action.getDialNumber());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getDialNumber());

        DialNumber dialNumber = action.getDialNumber();
        // update required fields

        action.setDialNumber(dialNumber);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        DialNumber dialNumber = new DialNumber();
        dialNumber.setId(-2L);
        action.setDialNumber(dialNumber);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}