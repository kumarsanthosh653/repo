package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Agent;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class AgentDaoTest extends BaseDaoTestCase {
    private AgentDao agentDao;

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public void testAddAndRemoveAgent() throws Exception {
        Agent agent = new Agent();

        // enter all required fields

        log.debug("adding agent...");
        agent = agentDao.save(agent);

        agent = agentDao.get(agent.getId());

        assertNotNull(agent.getId());

        log.debug("removing agent...");

        agentDao.remove(agent.getId());

        try {
            agentDao.get(agent.getId());
            fail("Agent found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}