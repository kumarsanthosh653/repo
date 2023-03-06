package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.dao.SkillDao;
import com.ozonetel.occ.model.FwpNumber;
import java.util.List;

public class SkillDaoHibernate extends GenericDaoHibernate<Skill, Long> implements SkillDao {

    public SkillDaoHibernate() {
        super(Skill.class);
    }

    public List<Skill> getSkillsByUser(String username) {
        return (List<Skill>)getHibernateTemplate().find("select distinct s from Skill s where s.user.username = ? ", username);
    }

    public List<Skill> getSkillsByUserAndSkillName(String skillName, String username) {
        return (List<Skill>)getHibernateTemplate().find("select distinct s from Skill s where s.skillName='" + skillName + "' and s.user.username = ? ", username);
    }

    public List<FwpNumber> getHuntingFwpNumbersBySkill(Long skillId) {
        String sql = "select * from  fwp_numbers f "
                + "join skill_fwpNumbers rsa on ( f.id = rsa.fwp_id) "
                + "join skill s on (s.id = rsa.skill_id)"
                + "and s.id= " + skillId + " and f.nextFlag = 0 and f.`State` != 3 "
                + "order by f.priority , f.lastSelected";

        List<FwpNumber> fwpNumbers = getSession().createSQLQuery(sql).addEntity(FwpNumber.class).list();
        log.debug("Agents  =" + fwpNumbers);

        return fwpNumbers;
    }

    @Override
    public List<Skill> getTransferSkillList(String username, String campaignType, String did) {
        //FIXME not working check this.
        return (List<Skill>)getHibernateTemplate().find("select new Skill(s.id,s.skillName) from Skill s where s.user.username=? and s.campaign.campaignType like '%'||?||'%' and  s.campaign.dId like '%'||?||'%' and (s.campaign.position like '%'||?||'%' or s.campaign.position like '%'||?||'%' ) and s.campaign.isDelete = False  and s.active=true", username, campaignType, did, "RUNNING", "STARTED");
    }

    @Override
    public List<Skill> getSkillsOfAgent(String user, String agentId) {
        return (List<Skill>)getHibernateTemplate().find("SELECT DISTINCT s FROM Skill s,IN(s.agents) a   WHERE a.agentId=? and s.user.username=?", agentId, user);
    }

}
