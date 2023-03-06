package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.CallDispositionDao;
import com.ozonetel.occ.model.CallDisposition;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CallDispositionManagerImplTest extends BaseManagerMockTestCase {
    private CallDispositionManagerImpl manager = null;
    private CallDispositionDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(CallDispositionDao.class);
        manager = new CallDispositionManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetCallDisposition() {
        log.debug("testing get...");

        final Long id = 7L;
        final CallDisposition callDisposition = new CallDisposition();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(callDisposition));
        }});

        CallDisposition result = manager.get(id);
        assertSame(callDisposition, result);
    }

    @Test
    public void testGetCallDispositions() {
        log.debug("testing getAll...");

        final List callDispositions = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(callDispositions));
        }});

        List result = manager.getAll();
        assertSame(callDispositions, result);
    }

    @Test
    public void testSaveCallDisposition() {
        log.debug("testing save...");

        final CallDisposition callDisposition = new CallDisposition();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(callDisposition)));
        }});

        manager.save(callDisposition);
    }

    @Test
    public void testRemoveCallDisposition() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}