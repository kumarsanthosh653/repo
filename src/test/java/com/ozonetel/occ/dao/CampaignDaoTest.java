package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Campaign;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class CampaignDaoTest extends BaseDaoTestCase {
    private CampaignDao campaignDao;

    public void setCampaignDao(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    public void testAddAndRemoveCampaign() throws Exception {
        Campaign campaign = new Campaign();

        // enter all required fields

        log.debug("adding campaign...");
        campaign = campaignDao.save(campaign);

        campaign = campaignDao.get(campaign.getCampaignId());

        assertNotNull(campaign.getCampaignId());

        log.debug("removing campaign...");

        campaignDao.remove(campaign.getCampaignId());

        try {
            campaignDao.get(campaign.getCampaignId());
            fail("Campaign found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}