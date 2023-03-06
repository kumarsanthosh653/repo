package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.CallDisposition;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class CallDispositionDaoTest extends BaseDaoTestCase {
    private CallDispositionDao callDispositionDao;

    public void setCallDispositionDao(CallDispositionDao callDispositionDao) {
        this.callDispositionDao = callDispositionDao;
    }

    public void testAddAndRemoveCallDisposition() throws Exception {
        CallDisposition callDisposition = new CallDisposition();

        // enter all required fields

        log.debug("adding callDisposition...");
        callDisposition = callDispositionDao.save(callDisposition);

        callDisposition = callDispositionDao.get(callDisposition.getId());

        assertNotNull(callDisposition.getId());

        log.debug("removing callDisposition...");

        callDispositionDao.remove(callDisposition.getId());

        try {
            callDispositionDao.get(callDisposition.getId());
            fail("CallDisposition found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}