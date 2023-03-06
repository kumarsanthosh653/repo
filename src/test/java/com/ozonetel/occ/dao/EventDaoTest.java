package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Event;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class EventDaoTest extends BaseDaoTestCase {
    private EventDao eventDao;

    public void setEventDao(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public void testAddAndRemoveEvent() throws Exception {
        Event event = new Event();

        // enter all required fields

        log.debug("adding event...");
        event = eventDao.save(event);

        event = eventDao.get(event.getEventId());

        assertNotNull(event.getEventId());

        log.debug("removing event...");

        eventDao.remove(event.getEventId());

        try {
            eventDao.get(event.getEventId());
            fail("Event found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}