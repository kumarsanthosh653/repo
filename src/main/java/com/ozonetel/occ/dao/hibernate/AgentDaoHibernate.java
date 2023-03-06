package com.ozonetel.occ.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.dao.AgentDao;
import com.ozonetel.occ.model.Agent.Mode;
import com.ozonetel.occ.model.FwpNumber;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.Query;

public class AgentDaoHibernate extends GenericDaoHibernate<Agent, Long> implements AgentDao {

    public AgentDaoHibernate() {
        super(Agent.class);
    }

    public List<Agent> getAgents() {
        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where a.active is true");
    }

    public List<Agent> getAgentsByUser(String userName) {
        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where a.user.username = '" + userName + "' and a.active is true");
    }

    public List<Agent> getIdleAgentsByUser(String userName) {
        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where  a.state = 2 and a.active is true and a.userId in (select u.id from User u where u.username=?)", userName);
    }

    @Override
    public List<Agent> getAgentsByCampaign(Long campaignId) {
        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where a.active is true and  a.campaignId = " + campaignId + " and a.nextFlag= 0 order by a.priority");
    }

//
//    public List<Agent> getAgentsBySkillandUser(String skillName, String username) {
//        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where a.active is true and a.skill.skillName = '" + skillName + "' and a.user.username = '" + username + "' and a.nextFlag= 0 order by a.priority");
//    }
    public List<Agent> getAgentsBySkill(Long skillId) {
        String sql = "select * from  agent a "
                + "join skill_agents rsa on ( a.id = rsa.agent_id) "
                + "join skill s on (s.id = rsa.skill_id)"
                + "and s.id= " + skillId + " and a.nextFlag = 0 and a.`State` = 2 and a.is_active is true "
                + "order by a.priority , a.lastSelected";

        List<Agent> agents = getSession().createSQLQuery(sql).addEntity(Agent.class).list();
        log.debug("Agents List =" + agents.size());
        return agents;
    }

    public List<Agent> getAgentsBySkill(Long skillId, Agent.Mode mode) {
        String sql = "select * from  agent a "
                + "join skill_agents rsa on ( a.id = rsa.agent_id) "
                + "join skill s on (s.id = rsa.skill_id)"
                + "and s.id= " + skillId + " and a.nextFlag = 0 and a.`State` = 2 and a.mode = 0 and a.is_active is true "
                + "order by a.priority , a.lastSelected";

        List<Agent> agents = getSession().createSQLQuery(sql).addEntity(Agent.class).list();
        log.debug("Agents List =" + agents.size());
        return agents;
    }

    public List<Agent> getHuntingAgentsBySkill(Long skillId) {
        String sql = "select * from  agent a "
                + "join skill_agents rsa on ( a.id = rsa.agent_id) "
                + "join skill s on (s.id = rsa.skill_id)"
                + "and s.id= " + skillId + " and a.nextFlag = 0 and a.`State` != 3 and a.is_hunting is true and a.active is true  "
                + "order by a.priority , a.lastSelected";

        List<Agent> agents = getSession().createSQLQuery(sql).addEntity(Agent.class).list();
//               log.debug("Agents List ="+agents.size());
        log.debug("Agents  =" + agents);

        return agents;
    }

    public List<Agent> getAgentByAgentIdV2(Long userId, String agent) {
        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where a.userId = '" + userId + "' and a.agentId = '" + agent + "' and a.active is true");
    }

    public List<Campaign> getCampaignByAgentId(Long id) {
        return (List<Campaign>) getHibernateTemplate().find("select campaign from Agent a where a.id = " + id);
    }

    public List<Campaign> getCampaignByAgentId(String id) {
        return (List<Campaign>) getHibernateTemplate().find("select campaign from Agent a where a.agentId = '" + id + "'");
    }

    public List<Agent> getIdleAgents() {
        return (List<Agent>) getHibernateTemplate().find("from Agent a where a.state = 2 and a.active is true");
    }

    //Returns the Idle Agents By Campaign
    public List<Agent> getIdleAgentsByCampaign(Long campaignId) {
        return (List<Agent>) getHibernateTemplate().find("select a from Agent a where a.active is true and  a.state = 2 and a.campaignId = " + campaignId);
    }

    public List<Agent> getBusyAgents() {
        return (List<Agent>) getHibernateTemplate().find("from Agent a where a.active is true and a.state = 3");
    }

    public List<Agent> getLoggedInAgents() {
        ArrayList<Agent> al = new ArrayList<Agent>();
        List<Agent> l = getAgents();
        for (Agent agent : l) {
            if (agent.isLoggedIn()) {
                al.add(agent);
            }
        }
        return al;
    }

    @Override
    public List<Agent> getTransferAgentList(Long userId, Long agentUniqueId) {
        return getHibernateTemplate().find("select new Agent(a.id,a.agentId,a.agentName,a.fwpNumber.sip) from Agent a where a.userId=? and  a.id!=? and a.state = 2 and a.active is true and a.nextFlag = 0 and a.mode in (0,4) ", userId, agentUniqueId);
        //log.debug("agent transfer list---"+list);

    }
    
    @Override
    public List<Map<String, Object>> getChatTransferAgentList(String did, Long agentUniqueId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("did", did);
        params.put("agentUniqueId", agentUniqueId);
        return executeProcedure("CALL Get_ChatTransferCampaignAgents(?,?)", params);
    }

    //UpdateCallStatus
    @Override
    public int releaseAgentLockWithDialStatus(Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException) {
        StringBuilder query = new StringBuilder();

        query.append("update Agent a set a.callStatus=?,a.directCallCount = (CASE WHEN a.directCallCount<=0 THEN 0 ELSE (a.directCallCount-1) END) ")
                .append(", a.callExceptions = ").append(exceptionCount)
                .append(!isProgressive ? ",a.nextFlag=0" : "")
                .append(lockAgentWithException ? ", a.state=5,a.stateReason='" + dialStatus + "' " : " ")
                .append(" where a.id=?");
//        log.trace("Executing query:"+query);

        return getHibernateTemplate().bulkUpdate(query.toString(), dialStatus, id);
    }
    
    @Override
    public int releaseAgentLockFromUpdateCallStatus(Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException, Long ucid) {
        StringBuilder query = new StringBuilder();

        query.append("update Agent a set a.callStatus=?,a.directCallCount = (CASE WHEN a.directCallCount<=0 THEN 0 ELSE (a.directCallCount-1) END) ")
                .append(", a.callExceptions = ").append(exceptionCount)
                .append(!isProgressive ? ",a.nextFlag=0" : "")
                .append(lockAgentWithException ? ", a.state=5,a.stateReason='" + dialStatus + "' " : " ")
                .append(" where a.id=? and a.ucid=?");
//        log.trace("Executing query:"+query);

        return getHibernateTemplate().bulkUpdate(query.toString(), dialStatus, id, ucid);
    }

    @Override
    public int decrementChatSessionCount(Long agentUniqId) {
        int isUpdated = getHibernateTemplate().bulkUpdate("update Agent a set a.sessionCount = a.sessionCount-1 where a.id=? and a.sessionCount > 0", agentUniqId);
        log.debug("Decrementing agent sessions in DB for -> " + agentUniqId + " isUpdated -> "+isUpdated);
        return isUpdated;
    }
    
    @Override
    public int pauseChatSessions(Long agentUniqId) { //setting session count to a very high value so that he will not be selected for next chats
        return getHibernateTemplate().bulkUpdate("update Agent a set a.sessionCount = 999 where  a.id=?", agentUniqId);
    }

    @Override
    public List getAgentPerformance(Long user, Long agent) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user_id", user);
        params.put("agent_id", agent);
        return executeProcedure("CALL Rep_AgentPerformace(?,?)", params);
    }

    @Override
    public List<Map<String, Object>> getAgentSummary(Long agentId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("agentId", agentId);
        return this.executeProcedure("call Get_AgentSummary(?)", params);
    }

    @Override
    public int updatePassword(String user, String agentId, String oldPwd, String newPwd) {
        return getHibernateTemplate().bulkUpdate("update Agent a set a.password = ? where a.agentId=? and a.password=? and a.active is true and  a.userId  in (select u.id from User u where u.username=?)", newPwd, agentId, oldPwd, user);

    }

    //tbAgentRelease
    @Override
    public int saveReleasedAgent(Long id, Mode mode) {
        log.debug("Updating release agent in DB :" + id + " : " + mode);
        StringBuilder query = new StringBuilder();
        query.append("update Agent a set a.state=" + Agent.State.IDLE.ordinal() + ",a.idleSince=0,a.type='',a.skillName=null,a.idleTime=" + System.currentTimeMillis() + ",a.nextFlag=0,a.contact='',a.campignName=null,a.ucid=null,a.callExceptions=0,a.directCallCount = (CASE WHEN a.directCallCount<=0 THEN 0 ELSE (a.directCallCount-1) END),a.mode=" + mode.ordinal() + ",a.stateReason=null,a.sessionCount=0 ")
                .append(" where a.id=" + id);
        log.trace("Executing query:" + query);
        return getHibernateTemplate().bulkUpdate(query.toString());
    }

    @Override
    public int savePauseAgent(Long id, String reason) {
        log.debug("Updating pause agent in DB : " + id + " reason : " + reason);
        return getHibernateTemplate().bulkUpdate("update Agent a set a.state = ?,a.idleTime=?, a.stateReason=? where a.id=? and a.nextFlag=0", Agent.State.AUX, System.currentTimeMillis(), reason, id);
    }

    @Override
    public int saveAgentMode(Long id, Mode mode, boolean reconnect) {
        log.debug("Updating agent mode in DB : " + id + " mode : " + mode + " reconnect ? " + reconnect);
        StringBuilder query = new StringBuilder();
        long currentMillies = System.currentTimeMillis();
        query.append("update Agent a set a.mode = ?")
                .append(!reconnect ? " ,a.lastSelected=" + currentMillies + ", a.idleTime=" + currentMillies : "")
                .append(" where a.id=?");

        return getHibernateTemplate().bulkUpdate(query.toString(), mode, id);
    }

    @Override
    public int saveAgentReconnect(Long id, String clientId) {
        log.debug("Updating agent reconnect in DB : " + id + " client Id : " + clientId);
        return getHibernateTemplate().bulkUpdate("update Agent a set a.clientId=? where a.id=?", clientId, id);
    }

    @Override
    public int lockAgent(Long id, String customerNumber) {
        return getHibernateTemplate().bulkUpdate("update Agent a set a.nextFlag=1, a.contact=?, a.idleSince=0, a.idleTime=? where a.id=?", customerNumber, System.currentTimeMillis(), id);
    }

    public int lockIfAgentAvailable(Long id, String customerNumber) {
        return getHibernateTemplate().bulkUpdate("update Agent a set a.nextFlag=1, a.contact=?, a.idleSince=0, a.idleTime=? where a.id=? and a.nextFlag=0 and a.state=?", customerNumber, System.currentTimeMillis(), id,Agent.State.IDLE);
    }

    @Override
    public int saveAgentLogin(Long id, FwpNumber fwpNumber, String clientId, String phoneNumber, boolean reconnect) {
        StringBuilder query = new StringBuilder();
        long currentMillies = System.currentTimeMillis();
        query.append("update Agent a set a.idleTime = ?, a.stateReason= null, a.phoneNumber=?")
                .append(!reconnect ? " , a.mode = " + Agent.Mode.INBOUND.ordinal() : "")
                .append((clientId != null && !clientId.isEmpty()) ? " , a.clientId='" + clientId + "'" : "")
                .append(" , a.fwpNumber = ?, a.state = ?, a.lastSelected = ?, a.sessionCount = 0, a.nextFlag=0")
                .append(" where a.id=?");
        return getHibernateTemplate().bulkUpdate(query.toString(), currentMillies, phoneNumber, fwpNumber, Agent.State.AUX, currentMillies, id);

    }

    @Override
    public int saveAgentLogout(Long id) {
        StringBuilder query = new StringBuilder();
        query.append("update Agent a set a.state = " + Agent.State.AUX.ordinal() + ", a.stateReason= null, a.idleTime = " + System.currentTimeMillis() + ", a.clientId=null ,a.phoneNumber=null, a.ucid=null")
                .append(" , a.fwpNumber = null, a.callExceptions=0 , a.sessionCount=0")
                .append(" where a.id=" + id);
        return getHibernateTemplate().bulkUpdate(query.toString());
    }

    public boolean makeAgentBusy(Long agentUniqId, String contact, String channelName, String callType, Long monitorUcid) {
        log.debug("Making agent busy in DB, AgentID : " + agentUniqId + " UCID : " + monitorUcid);
        return getHibernateTemplate().bulkUpdate("update Agent a set a.state = 3,a.callExceptions=0,a.idleTime = ?, a.contact=?,a.channelName = ?,a.type=?,a.ucid = ? where a.id = ?", System.currentTimeMillis(), contact, channelName, callType, monitorUcid, agentUniqId) > 0;
    }

    //ReleaseAgent
    public boolean releaseAgentFromCall(Long agentUniqId, Agent.State state) {
        log.debug("Releasing agent from call in DB : " + agentUniqId + " State : " + state);
        return getHibernateTemplate().bulkUpdate("update Agent a set a.state = ?,a.idleTime = ?, a.contact='',a.nextFlag=0 where a.id = ?", state, System.currentTimeMillis(), agentUniqId) > 0;
    }

    public boolean releaseAgentNextFlag(Long agentUniqId) {
        log.debug("Releasing agent nextflag : " + agentUniqId);
        return getHibernateTemplate().bulkUpdate("update Agent a set a.nextFlag=0 where a.id = " + agentUniqId) > 0;
    }

    @Override
    public boolean agentCallStarted(Long agentUniqId, String contact, String type, String callStatus, String skill, String campName, Long agentMonitorUcid, boolean updateTimeStamp) {
        StringBuilder query = new StringBuilder();
        long currentMillies = System.currentTimeMillis();
        query.append("update Agent a set a.contact='" + contact + "', a.type='" + type + "', a.callStatus='" + callStatus + "', a.skillName='" + skill + "', a.campignName='" + campName + "', a.ucid=" + agentMonitorUcid)
                .append(updateTimeStamp ? ", a.lastSelected=" + currentMillies + ", a.idleTime=" + currentMillies + ", a.idleSince=0" : "")
                .append(", a.nextFlag=1 where a.id=" + agentUniqId);
        return getHibernateTemplate().bulkUpdate(query.toString()) > 0;
    }

    public boolean updateHoldStartTime(Long agentId, boolean start) {
        return getHibernateTemplate().bulkUpdate("update Agent a set a.holdStartTime = ? where a.id= ?", start ? new Date() : null, agentId) > 0;
    }

    @Override
    public boolean isAgentLoggedin(Long agentId) {
        return !getHibernateTemplate().find("from Agent a where a.id= " + agentId + " and a.clientId is not null and a.active is true").isEmpty();
    }

    @Override
    public Object getLoggedInAgentsCountByUser(Long UserId) {
//        Map<String, Object> params = new LinkedHashMap<String, Object>();
//        params.put("UserId", UserId);
//        return uniqueQueryResult("from Agent a where  a.active is true and a.userId =:UserId and a.loggedIn = true and a.clientId is not NULL ", params);
//        
        List<Agent> agents = getHibernateTemplate().find("from Agent a where a.userId= " + UserId + " and a.clientId is not null and a.active is true");
        log.debug("Size of Logged in Agents : " + agents.size());
        return (Integer) agents.size();
    }

    public List<Map<String, Object>> getLoggedInAgentsForChat(Long UserId) {
        return getHibernateTemplate().find("from Agent a where a.userId= " + UserId + " and a.clientId is not null and a.active is true");
    }

    public Object uniqueQueryResult(String queryString, Map<String, Object> params) {
        try {
            Query query = getSession().createQuery(queryString);
            setParameters(query, params);
            return query.uniqueResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void setParameters(final Query query, final Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                if (key != null && !key.equals("")) {
                    if (params.get(key) instanceof Collection) {
                        query.setParameterList(key, (Collection) params.get(key));
                    } else if (params.get(key) instanceof Object[]) {
                        query.setParameterList(key, (Object[]) params.get(key));
                    } else {
                        query.setParameter(key, params.get(key));
                    }
                }
            }
        }
    }

    public boolean isValidAgent(Long agentId, String PhoneNo) {
        List<Agent> agents = getHibernateTemplate().find("from Agent a where a.id= " + agentId + " and a.clientId is not null and a.active is true");
        log.debug("agentId=" + agentId + " PhoneNo=" + PhoneNo);
        if (agents.size() > 0) {
//           return true;
            Agent a = agents.get(0);
            log.debug("The Agent Details are : " + a);
            if (a.getFwpNumber().getPhoneNumber().equals(PhoneNo)) {
                log.debug("Agent logged in with this number");
            } else {
                log.debug("Agent not logged in with that Number");
            }
        } else {
            log.debug("OK");
        }
//           return false;

        return true;
    }
}
