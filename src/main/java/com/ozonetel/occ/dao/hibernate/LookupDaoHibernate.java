package com.ozonetel.occ.dao.hibernate;

import java.util.List;

import com.ozonetel.occ.dao.LookupDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Role;

/**
 * Hibernate implementation of LookupDao.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class LookupDaoHibernate extends UniversalDaoHibernate implements LookupDao {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Role> getRoles() {
        log.debug("Retrieving all role names...");

        return getHibernateTemplate().find("from Role order by name");
    }
    @SuppressWarnings("unchecked")
    public void updateToolBarSettings() {
        log.debug("Updating all the Agents to Factory Settings..");

        List<Agent> agents = getHibernateTemplate().find("from Agent");
        for(Agent agent: agents){
            agent.setState(Agent.State.AUX);
            agent.setNextFlag(new Long(0));
            agent.setClientId(null);
            agent.setContact(null);
            agent.setLastSelected(new Long(0));
            agent.setPhoneNumber(null);
            agent.setFwpNumber(null);
            getHibernateTemplate().update(agent);
        }
        
        List<FwpNumber> fwps = getHibernateTemplate().find("from FwpNumber");
        for(FwpNumber fwpNumber: fwps){
          fwpNumber.setAgent(null);
          fwpNumber.setContact(null);
          fwpNumber.setNextFlag(new Long(0));
          fwpNumber.setState(Agent.State.IDLE);
          getHibernateTemplate().update(fwpNumber);
        }
          
        

    }
}
