package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.SkillDao;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.SkillManager;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "SkillService", endpointInterface = "com.ozonetel.occ.service.SkillManager")
public class SkillManagerImpl extends GenericManagerImpl<Skill, Long> implements SkillManager {

    SkillDao skillDao;

    public SkillManagerImpl(SkillDao skillDao) {
        super(skillDao);
        this.skillDao = skillDao;
    }

    public List<Skill> getSkillsByUser(String username) {
        return skillDao.getSkillsByUser(username);
    }

    public Skill getSkillsByUserAndSkillName(String skillName, String username) {
        List<Skill> skillList = skillDao.getSkillsByUserAndSkillName(skillName, username);
        if (!skillList.isEmpty()) {
            return skillList.get(0);
        } else {
            return null;
        }
    }

    public List<FwpNumber> getHuntingFwpNumbersBySkill(Long skillId) {
        return skillDao.getHuntingFwpNumbersBySkill(skillId);
    }

    @Override
    public List<Skill> getTransferSkillList(String username, String campaignType, String did) {
        return skillDao.getTransferSkillList(username, campaignType, did);
    }

    @Override
    public List<Skill> getSkillsOfAgent(String user, String agentId) {
        return skillDao.getSkillsOfAgent(user, agentId);
    }

}
