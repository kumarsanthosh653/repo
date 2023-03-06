package com.ozonetel.occ.service;

import com.ozonetel.occ.model.CallQueue;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.springframework.transaction.annotation.Transactional;

@WebService
@Transactional("transactionManager2")
public interface CallQueueManager extends GenericManager<CallQueue, Long> {

    @Override
    public CallQueue get(Long id);

    @Override
    public CallQueue save(CallQueue object);

    CallQueue getCallQueue(String callerId, String skillName, String did, Long ucid);

    boolean isCallInQueue(String callerId, String skillName, String did, Long ucid);

    public int getPostionInQueue(String callerId, String skillName, String did, Long ucid);

    public boolean isQueueEmpty(String skillName, String did);

    public void deleteCallQueue(String callerId, String skillName, String did, Long ucid);

    public void deleteAgentCallQueue(String callerId, String skillName, String did, Long ucid);

    public List<CallQueue> getCallQueuesByUser(Long userId);

    public int getCallQueueCountForAgent(String user, String agentId);

    public List<CallQueue> getCallQueuesBySubUser(Long userId, String campaigns);

    public List<Map<String, Object>> getCallQueuesBySubUserAndDate(Long userId, String campaigns, String fromDate, String toDate);

    public List<Map<String, Object>> getCallQueuesByUserAndDate(Long userId, String fromDate, String toDate);

    public List<Map<String, Object>> getCallQueueCountSkillWise(Long userId);

    public List<Map<String, Object>> getAgentCallQueueList(Long userId);

    public List<Map<String, Object>> getAgentCallQueueListByDate(Long userId, String fromDate, String toDate);
}
