package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Disposition;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class DispositionDaoTest extends BaseDaoTestCase {
    private DispositionDao dispositionDao;

    public void setDispositionDao(DispositionDao dispositionDao) {
        this.dispositionDao = dispositionDao;
    }

    public void testAddAndRemoveDisposition() throws Exception {
        Disposition disposition = new Disposition();

        // enter all required fields

        log.debug("adding disposition...");
        disposition = dispositionDao.save(disposition);

        disposition = dispositionDao.get(disposition.getId());

        assertNotNull(disposition.getId());

        log.debug("removing disposition...");

        dispositionDao.remove(disposition.getId());

        try {
            dispositionDao.get(disposition.getId());
            fail("Disposition found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}