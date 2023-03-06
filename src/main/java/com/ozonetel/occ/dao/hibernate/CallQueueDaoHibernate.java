package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.dao.CallQueueDao;
import com.ozonetel.occ.model.AgentCallQueue;
import com.ozonetel.occ.util.DateUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.support.DataAccessUtils;

public class CallQueueDaoHibernate extends GenericDaoHibernate<CallQueue, Long> implements CallQueueDao {

    public CallQueueDaoHibernate() {
        super(CallQueue.class);
    }

    public List<CallQueue> isCallInQueue(String callerId, String skillName, String did, Long ucid) {
        return getHibernateTemplate().find("from CallQueue c where  c.callQueuePK.skillName like '%" + skillName + "%' and c.callQueuePK.did like '%" + did + "%' and c.callQueuePK.callerId like '%" + callerId + "%' and c.callQueuePK.ucid=" + ucid);

    }

    public List<AgentCallQueue> isAgentCallInQueue(String callerId, String skillName, String did, Long ucid) {
        return getHibernateTemplate().find("from AgentCallQueue c where c.skillName like '%" + skillName + "%' and c.did like '%" + did + "%' and c.callerID like '%" + callerId + "%' and c.callID=" + ucid);
    }

    public List getPostionInQueue(String skillName, String did) {
        return getHibernateTemplate().find("from CallQueue c where c.isActive is true and c.callQueuePK.skillName like '%" + skillName + "%' and c.callQueuePK.did like '%" + did + "%'  order by c.reqCount desc , c.callQueuePK.ucid");
    }

    public List getCallQueuesBySubUser(Long userId, String campaigns) {
        Map<String, Object> params = new LinkedHashMap<>();
//        params.put("campaigns", campaigns);
//        log.debug(campaigns);
        log.debug("Getting call queues for sub user : "+userId+" for campaigns : "+campaigns);
        List result = executeSQLQuery("SELECT CallerID AS callerId,SkillName AS skillName,TIMESTAMPDIFF(SECOND,StartTime,NOW()) AS duration FROM `GeneralCallQueue` WHERE `ISActive` = true AND `CampaignID` IN ("+campaigns+") AND `StartTime` < SUBDATE(NOW(),INTERVAL 2 SECOND)", params);
        return result;
    }

    public List getCallQueuesByUser(Long userId) {
//        return getHibernateTemplate().find("select c.callQueuePK.callerId as callerId,c.callQueuePK.skillName as skillName,TIMESTAMPDIFF(SECOND,c.startTime,NOW()) AS duration from CallQueue c where c.isActive is true and c.userId ='" + userId + "' order by c.reqCount desc , c.callQueuePK.ucid");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user_id", userId);
        List result = executeSQLQuery("SELECT CallerID AS callerId,SkillName AS skillName,TIMESTAMPDIFF(SECOND,StartTime,NOW()) AS duration FROM `GeneralCallQueue` WHERE `ISActive` = true AND `UserID` = ? AND `StartTime` < SUBDATE(NOW(),INTERVAL 2 SECOND)", params);
        return result;
    }

    public List<Map<String, Object>> getCallQueuesByUserAndDate(Long userId, String fromDate, String toDate) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user_id", userId);
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        return executeSQLQuery("SELECT A.CallerID as callerId,A.SkillName as skillName,TIMESTAMPDIFF(SECOND,StartTime,NOW()) AS duration FROM `GeneralCallQueue` A WHERE A.`ISActive` = TRUE AND A.UserID = ? AND A.`StartTime` between ? and ?", params);
    }

    public List<Map<String, Object>> getCallQueuesBySubUserAndDate(Long userId, String campaigns, String fromDate, String toDate) {
        Map<String, Object> params = new LinkedHashMap<>();
//        params.put("campaigns", campaigns);
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        return executeSQLQuery("SELECT SkillName as skillName,CallerID as callerId ,TIMESTAMPDIFF(SECOND,StartTime,NOW()) AS duration FROM `GeneralCallQueue` WHERE `CampaignID` IN ("+campaigns+") AND `ISActive` = TRUE AND `StartTime` between ? and ?", params);

    }

    public void saveAgentCallQueue(AgentCallQueue agentCallQueue) {
        getHibernateTemplate().save(agentCallQueue);
    }

    public int getCallQueueCountBySkills(Long userId, List<String> skills) {
        return DataAccessUtils.intResult(getHibernateTemplate().findByNamedParam("select count(*) from  CallQueue c where c.isActive is true and c.userId = :userId and c.callQueuePK.skillName in (:skills) ", new String[]{"userId", "skills"}, new Object[]{userId, skills}));
    }

//    public int getCallQueueCountBySkills(Long user, List<String> skills) {
//        return DataAccessUtils.intResult(getHibernateTemplate().findByNamedParam("select count(*) from  CallQueue c where c.isActive is true and c.userId = :user and c.callQueuePK.skillName in (:skills) ", new String[]{"user", "skills"}, new Object[]{user, skills}));
//    }
    public int deleteCallQueue(Long ucid, String skillname, String did) {
        return getHibernateTemplate().bulkUpdate("update CallQueue c set c.isActive=false, c.endTime='" + DateUtil.convertDateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + "' where c.callQueuePK.skillName like '%" + skillname + "%' and c.callQueuePK.did like '%" + did + "%' and c.callQueuePK.ucid=" + ucid);
    }

    public List<Map<String, Object>> getCallQueueCountSkillWise(Long userId) {
        return getHibernateTemplate().find("select new map(c.callQueuePK.skillName as Skill,count(c.callQueuePK.skillName) as QueueCount) from CallQueue c where c.isActive is true and c.userId = ? group by c.callQueuePK.skillName", userId);
    }

    public List<Map<String, Object>> getAgentCallQueueList(Long userId) {
        return getHibernateTemplate().find("select new map(a.callerID as callerID,a.did as did,a.skillName as skillName,a.agent as agent,a.startTime as startTime,a.phoneName as phoneName) from AgentCallQueue a where  a.userId=? and a.active=true", userId);
    }

    public List<Map<String, Object>> getAgentCallQueueListByDate(Long userId, String fromDate, String toDate) {
        return getHibernateTemplate().find("select new map(a.callerID as callerID,a.did as did,a.skillName as skillName,a.agent as agent,a.startTime as startTime,a.phoneName as phoneName) from AgentCallQueue a where  a.userId=? and a.startTime between '" + fromDate + "' and '" + toDate + "' and a.active=true", userId);
    }
}
