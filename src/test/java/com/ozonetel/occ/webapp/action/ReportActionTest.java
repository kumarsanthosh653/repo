package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class ReportActionTest extends BaseActionTestCase {
    private ReportAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new ReportAction();
        ReportManager reportManager = (ReportManager) applicationContext.getBean("reportManager");
        action.setReportManager(reportManager);
    
        // add a test report to the database
        Report report = new Report();

        // enter all required fields

        reportManager.save(report);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getReports().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setReport_id(-1L);
        assertNull(action.getReport());
        assertEquals("success", action.edit());
        assertNotNull(action.getReport());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setReport_id(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getReport());

        Report report = action.getReport();
        // update required fields

        action.setReport(report);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        Report report = new Report();
        report.setReport_id(-2L);
        action.setReport(report);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}