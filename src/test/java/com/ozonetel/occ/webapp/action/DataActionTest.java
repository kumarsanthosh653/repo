package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.DataManager;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class DataActionTest extends BaseActionTestCase {
    private DataAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new DataAction();
        DataManager dataManager = (DataManager) applicationContext.getBean("dataManager");
        action.setDataManager(dataManager);
    
        // add a test data to the database
        Data data = new Data();

        // enter all required fields

        dataManager.save(data);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getDatas().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setData_id(-1L);
        assertNull(action.getData());
        assertEquals("success", action.edit());
        assertNotNull(action.getData());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setData_id(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getData());

        Data data = action.getData();
        // update required fields

        action.setData(data);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        Data data = new Data();
        data.setData_id(-2L);
        action.setData(data);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}