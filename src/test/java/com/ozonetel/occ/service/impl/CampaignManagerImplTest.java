package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.CampaignDao;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CampaignManagerImplTest extends BaseManagerMockTestCase {
    private CampaignManagerImpl manager = null;
    private CampaignDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(CampaignDao.class);
        manager = new CampaignManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetCampaign() {
        log.debug("testing get...");

        final Long campaign_id = 7L;
        final Campaign campaign = new Campaign();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(campaign_id)));
            will(returnValue(campaign));
        }});

        Campaign result = manager.get(campaign_id);
        assertSame(campaign, result);
    }

    @Test
    public void testGetCampaigns() {
        log.debug("testing getAll...");

        final List campaigns = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(campaigns));
        }});

        List result = manager.getAll();
        assertSame(campaigns, result);
    }

    @Test
    public void testSaveCampaign() {
        log.debug("testing save...");

        final Campaign campaign = new Campaign();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(campaign)));
        }});

        manager.save(campaign);
    }

    @Test
    public void testRemoveCampaign() {
        log.debug("testing remove...");

        final Long campaign_id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(campaign_id)));
        }});

        manager.remove(campaign_id);
    }
}