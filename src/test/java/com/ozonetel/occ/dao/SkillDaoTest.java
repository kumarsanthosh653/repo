package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.BaseDaoTestCase;
import com.ozonetel.occ.model.Skill;
import org.springframework.dao.DataAccessException;

import java.util.List;

public class SkillDaoTest extends BaseDaoTestCase {
    private SkillDao skillDao;

    public void setSkillDao(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    public void testAddAndRemoveSkill() throws Exception {
        Skill skill = new Skill();

        // enter all required fields

        log.debug("adding skill...");
        skill = skillDao.save(skill);

        skill = skillDao.get(skill.getId());

        assertNotNull(skill.getId());

        log.debug("removing skill...");

        skillDao.remove(skill.getId());

        try {
            skillDao.get(skill.getId());
            fail("Skill found in database");
        } catch (DataAccessException e) {
            log.debug("Expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}