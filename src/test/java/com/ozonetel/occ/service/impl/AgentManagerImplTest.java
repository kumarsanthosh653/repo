package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.AgentDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class AgentManagerImplTest extends BaseManagerMockTestCase {
    private AgentManagerImpl manager = null;
    private AgentDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(AgentDao.class);
        manager = new AgentManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetAgent() {
        log.debug("testing get...");

        final Long id = 7L;
        final Agent agent = new Agent();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(agent));
        }});

        Agent result = manager.get(id);
        assertSame(agent, result);
    }

    @Test
    public void testGetAgents() {
        log.debug("testing getAll...");

        final List agents = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(agents));
        }});

        List result = manager.getAll();
        assertSame(agents, result);
    }

    @Test
    public void testSaveAgent() {
        log.debug("testing save...");

        final Agent agent = new Agent();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(agent)));
        }});

        manager.save(agent);
    }

    @Test
    public void testRemoveAgent() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}