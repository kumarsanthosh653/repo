package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.FwpNumber;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class FwpNumberDaoTest extends BaseDaoTestCase {
    private FwpNumberDao fwpNumberDao;

    public void setFwpNumberDao(FwpNumberDao fwpNumberDao) {
        this.fwpNumberDao = fwpNumberDao;
    }

    public void testAddAndRemoveFwpNumber() throws Exception {
        FwpNumber fwpNumber = new FwpNumber();

        // enter all required fields

        log.debug("adding fwpNumber...");
        fwpNumber = fwpNumberDao.save(fwpNumber);

        fwpNumber = fwpNumberDao.get(fwpNumber.getId());

        assertNotNull(fwpNumber.getId());

        log.debug("removing fwpNumber...");

        fwpNumberDao.remove(fwpNumber.getId());

        try {
            fwpNumberDao.get(fwpNumber.getId());
            fail("FwpNumber found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}