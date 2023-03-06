package com.ozonetel.occ.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.dao.SkillDao;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.impl.BaseManagerMockTestCase;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class SkillManagerImplTest extends BaseManagerMockTestCase {
    private SkillManagerImpl manager = null;
    private SkillDao dao = null;

    @Before
    public void setUp() {
        dao = context.mock(SkillDao.class);
        manager = new SkillManagerImpl(dao);
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testGetSkill() {
        log.debug("testing get...");

        final Long id = 7L;
        final Skill skill = new Skill();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).get(with(equal(id)));
            will(returnValue(skill));
        }});

        Skill result = manager.get(id);
        assertSame(skill, result);
    }

    @Test
    public void testGetSkills() {
        log.debug("testing getAll...");

        final List skills = new ArrayList();

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).getAll();
            will(returnValue(skills));
        }});

        List result = manager.getAll();
        assertSame(skills, result);
    }

    @Test
    public void testSaveSkill() {
        log.debug("testing save...");

        final Skill skill = new Skill();
        // enter all required fields
        
        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).save(with(same(skill)));
        }});

        manager.save(skill);
    }

    @Test
    public void testRemoveSkill() {
        log.debug("testing remove...");

        final Long id = -11L;

        // set expected behavior on dao
        context.checking(new Expectations() {{
            one(dao).remove(with(equal(id)));
        }});

        manager.remove(id);
    }
}