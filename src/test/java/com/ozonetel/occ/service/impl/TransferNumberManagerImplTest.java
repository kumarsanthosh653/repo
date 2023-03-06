package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.TransferNumberDao;
import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransferNumberManagerImplTest extends BaseManagerMockTestCase {
    private TransferNumberManagerImpl manager = null;
    private TransferNumberDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(TransferNumberDao.class);
        manager = new TransferNumberManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetTransferNumber() {
        log.debug("testing get...");

        final long id = 7L;
        final TransferNumber transferNumber = new TransferNumber();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(transferNumber));
        }});

        TransferNumber result = manager.get(id);
        assertSame(transferNumber, result);
    }

    @Test
    public void testGetTransferNumbers() {
        log.debug("testing getAll...");

        final List transferNumbers = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(transferNumbers));
        }});

        List result = manager.getAll();
        assertSame(transferNumbers, result);
    }

    @Test
    public void testSaveTransferNumber() {
        log.debug("testing save...");

        final TransferNumber transferNumber = new TransferNumber();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(transferNumber)));
        }});

        manager.save(transferNumber);
    }

    @Test
    public void testRemoveTransferNumber() {
        log.debug("testing remove...");

        final long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}