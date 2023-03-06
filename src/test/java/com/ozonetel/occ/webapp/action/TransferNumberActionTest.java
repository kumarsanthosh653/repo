package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class TransferNumberActionTest extends BaseActionTestCase {
    private TransferNumberAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new TransferNumberAction();
        TransferNumberManager transferNumberManager = (TransferNumberManager) applicationContext.getBean("transferNumberManager");
        action.setTransferNumberManager(transferNumberManager);
    
        // add a test transferNumber to the database
        TransferNumber transferNumber = new TransferNumber();

        // enter all required fields

        transferNumberManager.save(transferNumber);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getTransferNumbers().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getTransferNumber());
        assertEquals("success", action.edit());
        assertNotNull(action.getTransferNumber());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getTransferNumber());

        TransferNumber transferNumber = action.getTransferNumber();
        // update required fields

        action.setTransferNumber(transferNumber);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        TransferNumber transferNumber = new TransferNumber();
        transferNumber.setId(-2L);
        action.setTransferNumber(transferNumber);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}