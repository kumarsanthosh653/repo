/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.AgentFreeStatusDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AgentFreeStatus;
import com.ozonetel.occ.model.FwpNumber;

public class AgentFreeStatusDaoHibernate extends GenericDaoHibernate<AgentFreeStatus, Long> implements AgentFreeStatusDao {

    public AgentFreeStatusDaoHibernate() {
        super(AgentFreeStatus.class);
    }

    @Override
    public int saveAgentLogin(Long id, boolean reconnect, String phoneNumber, FwpNumber fwp, String clientId) {
        log.debug("Saving login in AgentFreeStatus: " + id + " : " + reconnect + " : " + phoneNumber + " : " + fwp + " : " + clientId + AgentFreeStatus.Mode.INBOUND);
        long currentMillies = System.currentTimeMillis();
        StringBuilder query = new StringBuilder();
        query.append("update AgentFreeStatus a set a.phoneNumber=?, a.fwpNumber=?, a.clientId=?, a.isLocked=1, a.idleTime=?,a.lastSelected=?")
                .append(!reconnect ? " ,a.mode=" + AgentFreeStatus.Mode.INBOUND.ordinal() : "")
                .append(" where a.id=?");
        log.debug("Query : " + query);
        return getHibernateTemplate().bulkUpdate(query.toString(), phoneNumber, fwp, clientId, currentMillies, currentMillies, id);
    }

    @Override
    public int saveAgentLogout(Long id) {
        log.debug("updating logged out in AgentFreeStatus: " + id);
        return getHibernateTemplate().bulkUpdate("update AgentFreeStatus a set a.isLocked=1, a.clientId=null where a.id=?", id);
    }

    @Override
    public int saveReleasedAgent(Long id) {
        log.debug("Releasing agent in AgentFreeStatus : " + id);
        StringBuilder query = new StringBuilder();
        long currentMillies = System.currentTimeMillis();
        query.append("update AgentFreeStatus a set a.isLocked=0, a.idleTime=?,a.lastSelected=?, a.directCallCount=a.directCallCount-1")
                .append(" where a.id=?");
        return getHibernateTemplate().bulkUpdate(query.toString(), currentMillies, currentMillies, id);

    }

    @Override
    public int savePauseAgent(Long id) {
        log.debug("Paused agent in AgentFreeStatus: " + id);
        return getHibernateTemplate().bulkUpdate("update AgentFreeStatus a set a.isLocked=1 where a.id=?", id);

    }

    @Override
    public int saveAgentMode(Long id, Agent.Mode mode, boolean reconnect) {
        log.debug("Setting AgentFreeStatus mode : " + mode + " for " + id + " : " + reconnect);
        StringBuilder query = new StringBuilder();
        long currentMillies = System.currentTimeMillis();
        query.append("update AgentFreeStatus a set a.mode = ?")
                .append(!reconnect ? " ,a.lastSelected=" + currentMillies + ", a.idleTime=" + currentMillies : "")
                .append(" where a.id=?");

        return getHibernateTemplate().bulkUpdate(query.toString(), mode, id);
    }

    @Override
    public int saveAgentReconnect(Long id, String clientId) {
        log.debug("Agent reconnect in AgentFreeStatus: " + id + " clientID : " + clientId);
        return getHibernateTemplate().bulkUpdate("update AgentFreeStatus a set a.clientId=? where a.id=?", clientId, id);
    }

    @Override
    public int releaseAgentLock(Long id, boolean dropcall) {
        log.debug("Unlocking in AgentFreeStatus :" + id + " dropcall: " + dropcall);
        StringBuilder query = new StringBuilder();
        query.append("update AgentFreeStatus a set a.directCallCount=a.directCallCount-1")
                .append(dropcall ? " ,a.isLocked=0" : "")
                .append(" where a.id=?");

        return getHibernateTemplate().bulkUpdate(query.toString(), id);
    }

    @Override
    public int releaseAgentLockFlag(Long id) {
        log.debug("Releasing in AgentFreeStatus is_locked flag :" + id);
        StringBuilder query = new StringBuilder();
        query.append("update AgentFreeStatus a set ")
                .append(" a.isLocked=0 ")
                .append(" where a.id=" + id);

        return getHibernateTemplate().bulkUpdate(query.toString());
    }

    @Override
    public boolean lockAgentFreeStatusIfAvailable(Long id) {
        log.debug("Locking in AgentFreeStatus : " + id);
        return getHibernateTemplate().bulkUpdate("update AgentFreeStatus a set a.isLocked=1 where a.id=? and a.isLocked=0", id) > 0;
    }

    public boolean decrementChatSessionCount(Long id) {
        log.debug("Decrementing chat sess_count in AgentFreeStatus for : " + id);
        return getHibernateTemplate().bulkUpdate("update AgentFreeStatus a set a.sessionCount = a.sessionCount-1 where a.id=?", id) > 0;
    }
}
