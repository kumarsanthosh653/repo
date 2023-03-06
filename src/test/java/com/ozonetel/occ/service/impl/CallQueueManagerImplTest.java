package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.CallQueueDao;
import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CallQueueManagerImplTest extends BaseManagerMockTestCase {
    private CallQueueManagerImpl manager = null;
    private CallQueueDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(CallQueueDao.class);
        manager = new CallQueueManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetCallQueue() {
        log.debug("testing get...");

        final Long id = 7L;
        final CallQueue callQueue = new CallQueue();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(callQueue));
        }});

        CallQueue result = manager.get(id);
        assertSame(callQueue, result);
    }

    @Test
    public void testGetCallQueues() {
        log.debug("testing getAll...");

        final List callQueues = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(callQueues));
        }});

        List result = manager.getAll();
        assertSame(callQueues, result);
    }

    @Test
    public void testSaveCallQueue() {
        log.debug("testing save...");

        final CallQueue callQueue = new CallQueue();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(callQueue)));
        }});

        manager.save(callQueue);
    }

    @Test
    public void testRemoveCallQueue() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}