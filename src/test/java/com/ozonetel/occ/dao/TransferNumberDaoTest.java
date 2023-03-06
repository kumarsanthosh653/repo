package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.TransferNumber;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class TransferNumberDaoTest extends BaseDaoTestCase {
    private TransferNumberDao transferNumberDao;

    public void setTransferNumberDao(TransferNumberDao transferNumberDao) {
        this.transferNumberDao = transferNumberDao;
    }

    public void testAddAndRemoveTransferNumber() throws Exception {
        TransferNumber transferNumber = new TransferNumber();

        // enter all required fields

        log.debug("adding transferNumber...");
        transferNumber = transferNumberDao.save(transferNumber);

        transferNumber = transferNumberDao.get(transferNumber.getId());

        assertNotNull(transferNumber.getId());

        log.debug("removing transferNumber...");

        transferNumberDao.remove(transferNumber.getId());

        try {
            transferNumberDao.get(transferNumber.getId());
            fail("TransferNumber found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}