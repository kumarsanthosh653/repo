package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.ReportDao;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReportManagerImplTest extends BaseManagerMockTestCase {
    private ReportManagerImpl manager = null;
    private ReportDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(ReportDao.class);
        manager = new ReportManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetReport() {
        log.debug("testing get...");

        final Long report_id = 7L;
        final Report report = new Report();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(report_id)));
            will(returnValue(report));
        }});

        Report result = manager.get(report_id);
        assertSame(report, result);
    }

    @Test
    public void testGetReports() {
        log.debug("testing getAll...");

        final List reports = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(reports));
        }});

        List result = manager.getAll();
        assertSame(reports, result);
    }

    @Test
    public void testSaveReport() {
        log.debug("testing save...");

        final Report report = new Report();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(report)));
        }});

        manager.save(report);
    }

    @Test
    public void testRemoveReport() {
        log.debug("testing remove...");

        final Long report_id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(report_id)));
        }});

        manager.remove(report_id);
    }
}