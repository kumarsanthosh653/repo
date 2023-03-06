package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.CallBack;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class CallBackDaoTest extends BaseDaoTestCase {
    private CallBackDao callBackDao;

    public void setCallBackDao(CallBackDao callBackDao) {
        this.callBackDao = callBackDao;
    }

    public void testAddAndRemoveCallBack() throws Exception {
        CallBack callBack = new CallBack();

        // enter all required fields

        log.debug("adding callBack...");
        callBack = callBackDao.save(callBack);

        callBack = callBackDao.get(callBack.getId());

        assertNotNull(callBack.getId());

        log.debug("removing callBack...");

        callBackDao.remove(callBack.getId());

        try {
            callBackDao.get(callBack.getId());
            fail("CallBack found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}