package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.DialOutNumber;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class DialOutNumberDaoTest extends BaseDaoTestCase {
    private DialOutNumberDao dialOutNumberDao;

    public void setDialOutNumberDao(DialOutNumberDao dialOutNumberDao) {
        this.dialOutNumberDao = dialOutNumberDao;
    }

    public void testAddAndRemoveDialOutNumber() throws Exception {
        DialOutNumber dialOutNumber = new DialOutNumber();

        // enter all required fields

        log.debug("adding dialOutNumber...");
        dialOutNumber = dialOutNumberDao.save(dialOutNumber);

        dialOutNumber = dialOutNumberDao.get(dialOutNumber.getId());

        assertNotNull(dialOutNumber.getId());

        log.debug("removing dialOutNumber...");

        dialOutNumberDao.remove(dialOutNumber.getId());

        try {
            dialOutNumberDao.get(dialOutNumber.getId());
            fail("DialOutNumber found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}