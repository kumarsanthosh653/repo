package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.FwpNumberDao;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class FwpNumberManagerImplTest extends BaseManagerMockTestCase {
    private FwpNumberManagerImpl manager = null;
    private FwpNumberDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(FwpNumberDao.class);
        manager = new FwpNumberManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetFwpNumber() {
        log.debug("testing get...");

        final Long id = 7L;
        final FwpNumber fwpNumber = new FwpNumber();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(fwpNumber));
        }});

        FwpNumber result = manager.get(id);
        assertSame(fwpNumber, result);
    }

    @Test
    public void testGetFwpNumbers() {
        log.debug("testing getAll...");

        final List fwpNumbers = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(fwpNumbers));
        }});

        List result = manager.getAll();
        assertSame(fwpNumbers, result);
    }

    @Test
    public void testSaveFwpNumber() {
        log.debug("testing save...");

        final FwpNumber fwpNumber = new FwpNumber();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(fwpNumber)));
        }});

        manager.save(fwpNumber);
    }

    @Test
    public void testRemoveFwpNumber() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}