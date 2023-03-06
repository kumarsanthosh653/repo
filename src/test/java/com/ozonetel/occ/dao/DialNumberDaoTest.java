package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.DialNumber;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class DialNumberDaoTest extends BaseDaoTestCase {
    private DialNumberDao dialNumberDao;

    public void setDialNumberDao(DialNumberDao dialNumberDao) {
        this.dialNumberDao = dialNumberDao;
    }

    public void testAddAndRemoveDialNumber() throws Exception {
        DialNumber dialNumber = new DialNumber();

        // enter all required fields

        log.debug("adding dialNumber...");
        dialNumber = dialNumberDao.save(dialNumber);

        dialNumber = dialNumberDao.get(dialNumber.getId());

        assertNotNull(dialNumber.getId());

        log.debug("removing dialNumber...");

        dialNumberDao.remove(dialNumber.getId());

        try {
            dialNumberDao.get(dialNumber.getId());
            fail("DialNumber found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}