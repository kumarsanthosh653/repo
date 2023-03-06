package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.PauseReasonDao;
import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class PauseReasonManagerImplTest extends BaseManagerMockTestCase {
    private PauseReasonManagerImpl manager = null;
    private PauseReasonDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(PauseReasonDao.class);
        manager = new PauseReasonManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetPauseReason() {
        log.debug("testing get...");

        final Long id = 7L;
        final PauseReason pauseReason = new PauseReason();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(pauseReason));
        }});

        PauseReason result = manager.get(id);
        assertSame(pauseReason, result);
    }

    @Test
    public void testGetPauseReasons() {
        log.debug("testing getAll...");

        final List pauseReasons = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(pauseReasons));
        }});

        List result = manager.getAll();
        assertSame(pauseReasons, result);
    }

    @Test
    public void testSavePauseReason() {
        log.debug("testing save...");

        final PauseReason pauseReason = new PauseReason();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(pauseReason)));
        }});

        manager.save(pauseReason);
    }

    @Test
    public void testRemovePauseReason() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}