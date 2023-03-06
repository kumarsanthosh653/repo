package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.model.Campaign;
import org.springframework.mock.web.MockHttpServletRequest;

public class CampaignActionTest extends BaseActionTestCase {
    private CampaignAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new CampaignAction();
        CampaignManager campaignManager = (CampaignManager) applicationContext.getBean("campaignManager");
//        action.setCampaignManager(campaignManager);
    
        // add a test campaign to the database
        Campaign campaign = new Campaign();

        // enter all required fields

        campaignManager.save(campaign);
    }

    public void testSearch() throws Exception {
//        assertEquals(action.list(), ActionSupport.SUCCESS);
//        assertTrue(action.getCampaigns().size() >= 1);
    }

    public void testEdit() throws Exception {
//        log.debug("testing edit...");
//        action.setCampaignId(-1L);
//        assertNull(action.getCampaign());
//        assertEquals("success", action.edit());
//        assertNotNull(action.getCampaign());
//        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        ServletActionContext.setRequest(request);
//        action.setCampaignId(-1L);
//        assertEquals("success", action.edit());
//        assertNotNull(action.getCampaign());
//
//        Campaign campaign = action.getCampaign();
//        // update required fields
//
//        action.setCampaign(campaign);
//
//        assertEquals("input", action.save());
//        assertFalse(action.hasActionErrors());
//        assertFalse(action.hasFieldErrors());
//        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        ServletActionContext.setRequest(request);
//        action.setDelete("");
//        Campaign campaign = new Campaign();
//        campaign.setCampaignId(-2L);
//        action.setCampaign(campaign);
//        assertEquals("success", action.delete());
//        assertNotNull(request.getSession().getAttribute("messages"));
    }
}