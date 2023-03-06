package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Location;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class LocationDaoTest extends BaseDaoTestCase {
    private LocationDao locationDao;

    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public void testAddAndRemoveLocation() throws Exception {
        Location location = new Location();

        // enter all required fields
        location.setName("ZcPmHuIuIgBpIwYqJzImKyXkUvXhZyGoChLfDqIkQwPzDdCpTbPbSxWiKoEqCtMhShSgTjZmHrCyXrKtDfJxLaQpZpTmZhNhQvQmYwIrFpUsXtRfFpWoZpEwThRnKaSaKuBzSuTyJuPhBxJqPyWjCuWnUjGxIgBaLmQgGpVlQwRxPhZcOaVdHrExYiVvXlGxCtIhLwTyEmPuJxEtXaPvLwUoLtCqTvKbJsFfXcVfFtTgBlNcZhMxSdOzOhOuIiN");

        log.debug("adding location...");
        location = locationDao.save(location);

        location = locationDao.get(location.getId());

        assertNotNull(location.getId());

        log.debug("removing location...");

        locationDao.remove(location.getId());

        try {
            locationDao.get(location.getId());
            fail("Location found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}