package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.DialNumberDao;
import com.ozonetel.occ.model.DialNumber;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class DialNumberManagerImplTest extends BaseManagerMockTestCase {
    private DialNumberManagerImpl manager = null;
    private DialNumberDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(DialNumberDao.class);
        manager = new DialNumberManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetDialNumber() {
        log.debug("testing get...");

        final Long id = 7L;
        final DialNumber dialNumber = new DialNumber();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(dialNumber));
        }});

        DialNumber result = manager.get(id);
        assertSame(dialNumber, result);
    }

    @Test
    public void testGetDialNumbers() {
        log.debug("testing getAll...");

        final List dialNumbers = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(dialNumbers));
        }});

        List result = manager.getAll();
        assertSame(dialNumbers, result);
    }

    @Test
    public void testSaveDialNumber() {
        log.debug("testing save...");

        final DialNumber dialNumber = new DialNumber();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(dialNumber)));
        }});

        manager.save(dialNumber);
    }

    @Test
    public void testRemoveDialNumber() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}