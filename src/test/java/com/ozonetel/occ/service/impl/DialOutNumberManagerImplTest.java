package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.DialOutNumberDao;
import com.ozonetel.occ.model.DialOutNumber;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class DialOutNumberManagerImplTest extends BaseManagerMockTestCase {
    private DialOutNumberManagerImpl manager = null;
    private DialOutNumberDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(DialOutNumberDao.class);
        manager = new DialOutNumberManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetDialOutNumber() {
        log.debug("testing get...");

        final Long id = 7L;
        final DialOutNumber dialOutNumber = new DialOutNumber();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(dialOutNumber));
        }});

        DialOutNumber result = manager.get(id);
        assertSame(dialOutNumber, result);
    }

    @Test
    public void testGetDialOutNumbers() {
        log.debug("testing getAll...");

        final List dialOutNumbers = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(dialOutNumbers));
        }});

        List result = manager.getAll();
        assertSame(dialOutNumbers, result);
    }

    @Test
    public void testSaveDialOutNumber() {
        log.debug("testing save...");

        final DialOutNumber dialOutNumber = new DialOutNumber();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(dialOutNumber)));
        }});

        manager.save(dialOutNumber);
    }

    @Test
    public void testRemoveDialOutNumber() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}