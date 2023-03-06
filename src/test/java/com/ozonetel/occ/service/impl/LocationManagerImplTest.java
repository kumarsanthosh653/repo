package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.LocationDao;
import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocationManagerImplTest extends BaseManagerMockTestCase {
    private LocationManagerImpl manager = null;
    private LocationDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(LocationDao.class);
        manager = new LocationManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetLocation() {
        log.debug("testing get...");

        final Long id = 7L;
        final Location location = new Location();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(location));
        }});

        Location result = manager.get(id);
        assertSame(location, result);
    }

    @Test
    public void testGetLocations() {
        log.debug("testing getAll...");

        final List locations = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(locations));
        }});

        List result = manager.getAll();
        assertSame(locations, result);
    }

    @Test
    public void testSaveLocation() {
        log.debug("testing save...");

        final Location location = new Location();
        // enter all required fields
        location.setName("GlSmUuKkZnHxCxEiCiIdPwMaNkScKrKrNxFqWcUoTsMxZhFsCyXbLjOzNjYbZlPdOsHeAiMxFnRxKeVaCkTjIiVpQkYnFdXyAeCdQdPqSfYpPdHiPzCkMgQmPqGmHlUoIbZjBkXvDqDxAfRmExTfUhDaBoEwGbBxMeHoHpDySbTpToFcRaHpQnLfUyXgRrJoOfXwKeHrLrRrJpPvQuMzNsFlZlHgRdShWoStXkDzRbQlZmTeReDhIgNuXfLtXwF");
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(location)));
        }});

        manager.save(location);
    }

    @Test
    public void testRemoveLocation() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}