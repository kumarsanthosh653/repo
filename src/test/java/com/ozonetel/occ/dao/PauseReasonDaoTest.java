package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.PauseReason;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class PauseReasonDaoTest extends BaseDaoTestCase {
    private PauseReasonDao pauseReasonDao;

    public void setPauseReasonDao(PauseReasonDao pauseReasonDao) {
        this.pauseReasonDao = pauseReasonDao;
    }

    public void testAddAndRemovePauseReason() throws Exception {
        PauseReason pauseReason = new PauseReason();

        // enter all required fields

        log.debug("adding pauseReason...");
        pauseReason = pauseReasonDao.save(pauseReason);

        pauseReason = pauseReasonDao.get(pauseReason.getId());

        assertNotNull(pauseReason.getId());

        log.debug("removing pauseReason...");

        pauseReasonDao.remove(pauseReason.getId());

        try {
            pauseReasonDao.get(pauseReason.getId());
            fail("PauseReason found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}