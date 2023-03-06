package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.AgentCallQueue;

import com.ozonetel.occ.model.CallQueue;
import java.util.List;
import java.util.Map;

/**
 * An interface that provides a data management interface to the CallQueue
 * table.
 */
public interface CallQueueDao extends GenericDao<CallQueue, Long> {

    List<CallQueue> isCallInQueue(String callerId, String skillName, String did, Long ucid);

    List<AgentCallQueue> isAgentCallInQueue(String callerId, String skillName, String did, Long ucid);

    List<CallQueue> getPostionInQueue(String skillName, String did);

    List<CallQueue> getCallQueuesByUser(Long userId);

    void saveAgentCallQueue(AgentCallQueue agentCallQueue);

    public int getCallQueueCountBySkills(Long userId, List<String> skills);
//    public int getCallQueueCountBySkills(Long userUniqId, List<String> skills);

    public int deleteCallQueue(Long ucid, String skillname, String did);

    public List<CallQueue> getCallQueuesBySubUser(Long userId, String campaigns);

    public List<Map<String, Object>> getCallQueuesByUserAndDate(Long userId, String fromDate, String toDate);

    public List<Map<String, Object>> getCallQueuesBySubUserAndDate(Long userId, String campaigns, String fromDate, String toDate);

    public List<Map<String, Object>> getCallQueueCountSkillWise(Long userId);

    public List<Map<String, Object>> getAgentCallQueueList(Long userId);

    public List<Map<String, Object>> getAgentCallQueueListByDate(Long userId, String fromDate, String toDate);
}
