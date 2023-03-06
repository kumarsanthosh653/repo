package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.DataDao;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataManagerImplTest extends BaseManagerMockTestCase {
    private DataManagerImpl manager = null;
    private DataDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(DataDao.class);
        manager = new DataManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetData() {
        log.debug("testing get...");

        final Long data_id = 7L;
        final Data data = new Data();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(data_id)));
            will(returnValue(data));
        }});

        Data result = manager.get(data_id);
        assertSame(data, result);
    }

    @Test
    public void testGetDatas() {
        log.debug("testing getAll...");

        final List datas = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(datas));
        }});

        List result = manager.getAll();
        assertSame(datas, result);
    }

    @Test
    public void testSaveData() {
        log.debug("testing save...");

        final Data data = new Data();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(data)));
        }});

        manager.save(data);
    }

    @Test
    public void testRemoveData() {
        log.debug("testing remove...");

        final Long data_id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(data_id)));
        }});

        manager.remove(data_id);
    }
}