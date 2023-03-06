package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class FwpNumberActionTest extends BaseActionTestCase {
    private FwpNumberAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new FwpNumberAction();
        FwpNumberManager fwpNumberManager = (FwpNumberManager) applicationContext.getBean("fwpNumberManager");
        action.setFwpNumberManager(fwpNumberManager);
    
        // add a test fwpNumber to the database
        FwpNumber fwpNumber = new FwpNumber();

        // enter all required fields

        fwpNumberManager.save(fwpNumber);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getFwpNumbers().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getFwpNumber());
        assertEquals("success", action.edit());
        assertNotNull(action.getFwpNumber());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getFwpNumber());

        FwpNumber fwpNumber = action.getFwpNumber();
        // update required fields

        action.setFwpNumber(fwpNumber);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        FwpNumber fwpNumber = new FwpNumber();
        fwpNumber.setId(-2L);
        action.setFwpNumber(fwpNumber);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}