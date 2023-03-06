package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Data;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class DataDaoTest extends BaseDaoTestCase {
    private DataDao dataDao;

    public void setDataDao(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public void testAddAndRemoveData() throws Exception {
        Data data = new Data();

        // enter all required fields

        log.debug("adding data...");
        data = dataDao.save(data);

        data = dataDao.get(data.getData_id());

        assertNotNull(data.getData_id());

        log.debug("removing data...");

        dataDao.remove(data.getData_id());

        try {
            dataDao.get(data.getData_id());
            fail("Data found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}