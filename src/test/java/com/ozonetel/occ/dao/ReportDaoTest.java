package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Report;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class ReportDaoTest extends BaseDaoTestCase {
    private ReportDao reportDao;

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public void testAddAndRemoveReport() throws Exception {
        Report report = new Report();

        // enter all required fields

        log.debug("adding report...");
        report = reportDao.save(report);

        report = reportDao.get(report.getReport_id());

        assertNotNull(report.getReport_id());

        log.debug("removing report...");

        reportDao.remove(report.getReport_id());

        try {
            reportDao.get(report.getReport_id());
            fail("Report found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}