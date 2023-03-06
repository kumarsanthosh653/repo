package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.DispositionDao;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class DispositionManagerImplTest extends BaseManagerMockTestCase {
    private DispositionManagerImpl manager = null;
    private DispositionDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(DispositionDao.class);
        manager = new DispositionManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetDisposition() {
        log.debug("testing get...");

        final Long id = 7L;
        final Disposition disposition = new Disposition();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(disposition));
        }});

        Disposition result = manager.get(id);
        assertSame(disposition, result);
    }

    @Test
    public void testGetDispositions() {
        log.debug("testing getAll...");

        final List dispositions = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(dispositions));
        }});

        List result = manager.getAll();
        assertSame(dispositions, result);
    }

    @Test
    public void testSaveDisposition() {
        log.debug("testing save...");

        final Disposition disposition = new Disposition();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(disposition)));
        }});

        manager.save(disposition);
    }

    @Test
    public void testRemoveDisposition() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}