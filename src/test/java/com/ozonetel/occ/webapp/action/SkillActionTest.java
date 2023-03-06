package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class SkillActionTest extends BaseActionTestCase {
    private SkillAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new SkillAction();
        SkillManager skillManager = (SkillManager) applicationContext.getBean("skillManager");
        action.setSkillManager(skillManager);
    
        // add a test skill to the database
        Skill skill = new Skill();

        // enter all required fields

        skillManager.save(skill);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getSkills().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getSkill());
        assertEquals("success", action.edit());
        assertNotNull(action.getSkill());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getSkill());

        Skill skill = action.getSkill();
        // update required fields

        action.setSkill(skill);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        Skill skill = new Skill();
        skill.setId(-2L);
        action.setSkill(skill);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}