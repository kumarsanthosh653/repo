package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Skill;
import java.util.List;

/**
 * An interface that provides a data management interface to the Skill table.
 */
public interface SkillDao extends GenericDao<Skill, Long> {

    List<Skill> getSkillsByUser(String username);

    List<Skill> getSkillsByUserAndSkillName(String skillName, String username);

    List<FwpNumber> getHuntingFwpNumbersBySkill(Long skillId);

    /**
     * In this list skill object will only have <code>id</code> and
     * <code>skillName</code>.All other will be null.
     *
     * @param username
     * @param campaignType
     * @param did
     * @return
     */
    public List<Skill> getTransferSkillList(String username, String campaignType, String did);

    List<Skill> getSkillsOfAgent(String user, String agentId);

}
