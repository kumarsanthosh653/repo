package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class PreviewDataActionTest extends BaseActionTestCase {
    private PreviewDataAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new PreviewDataAction();
        PreviewDataManager previewDataManager = (PreviewDataManager) applicationContext.getBean("previewDataManager");
        action.setPreviewDataManager(previewDataManager);
    
        // add a test previewData to the database
        PreviewData previewData = new PreviewData();

        // enter all required fields

        previewDataManager.save(previewData);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getPreviewDatas().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setPreview_data_id(-1L);
        assertNull(action.getPreviewData());
        assertEquals("success", action.edit());
        assertNotNull(action.getPreviewData());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setPreview_data_id(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getPreviewData());

        PreviewData previewData = action.getPreviewData();
        // update required fields

        action.setPreviewData(previewData);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        PreviewData previewData = new PreviewData();
        //previewData.setPreview_data_id(-2L);
        action.setPreviewData(previewData);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}