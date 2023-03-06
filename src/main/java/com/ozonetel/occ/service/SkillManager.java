package com.ozonetel.occ.service;

import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Skill;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface SkillManager extends GenericManager<Skill, Long> {

    List<Skill> getSkillsByUser(String username);

    Skill getSkillsByUserAndSkillName(String skillName, String username);
    
    public List<Skill> getSkillsOfAgent(String user, String agentId);

    public List<FwpNumber> getHuntingFwpNumbersBySkill(Long skillId);

    
    /**
     * In this list skill object will  have only <code>id</code> and <code>skillName</code>.All other will be null. 
     * @param username
     * @param campaignType
     * @param did
     * @return 
     */
    public List<Skill> getTransferSkillList(String username, String campaignType, String did);
}
