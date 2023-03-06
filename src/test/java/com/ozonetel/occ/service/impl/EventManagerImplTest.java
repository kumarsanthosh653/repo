package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.EventDao;
import com.ozonetel.occ.model.Event;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventManagerImplTest extends BaseManagerMockTestCase {
    private EventManagerImpl manager = null;
    private EventDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(EventDao.class);
        manager = new EventManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetEvent() {
        log.debug("testing get...");

        final Long eventId = 7L;
        final Event event = new Event();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(eventId)));
            will(returnValue(event));
        }});

        Event result = manager.get(eventId);
        assertSame(event, result);
    }

    @Test
    public void testGetEvents() {
        log.debug("testing getAll...");

        final List events = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(events));
        }});

        List result = manager.getAll();
        assertSame(events, result);
    }

    @Test
    public void testSaveEvent() {
        log.debug("testing save...");

        final Event event = new Event();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(event)));
        }});

        manager.save(event);
    }

    @Test
    public void testRemoveEvent() {
        log.debug("testing remove...");

        final Long eventId = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(eventId)));
        }});

        manager.remove(eventId);
    }
}