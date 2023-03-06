package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.CallBackDao;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CallBackManagerImplTest extends BaseManagerMockTestCase {
    private CallBackManagerImpl manager = null;
    private CallBackDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(CallBackDao.class);
        manager = new CallBackManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetCallBack() {
        log.debug("testing get...");

        final Long id = 7L;
        final CallBack callBack = new CallBack();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(callBack));
        }});

        CallBack result = manager.get(id);
        assertSame(callBack, result);
    }

    @Test
    public void testGetCallBacks() {
        log.debug("testing getAll...");

        final List callBacks = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(callBacks));
        }});

        List result = manager.getAll();
        assertSame(callBacks, result);
    }

    @Test
    public void testSaveCallBack() {
        log.debug("testing save...");

        final CallBack callBack = new CallBack();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(callBack)));
        }});

        manager.save(callBack);
    }

    @Test
    public void testRemoveCallBack() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}