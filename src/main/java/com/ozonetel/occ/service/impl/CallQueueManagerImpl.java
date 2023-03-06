package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.CallQueueDao;
import com.ozonetel.occ.model.AgentCallQueue;
import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.service.UserManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;

@WebService(serviceName = "CallQueueService", endpointInterface = "com.ozonetel.occ.service.CallQueueManager")
public class CallQueueManagerImpl extends GenericManagerImpl<CallQueue, Long> implements CallQueueManager {

    CallQueueDao callQueueDao;

    public CallQueueManagerImpl(CallQueueDao callQueueDao) {
        super(callQueueDao);
        this.callQueueDao = callQueueDao;
    }

    public boolean isCallInQueue(String callerId, String skillName, String did, Long ucid) {
        List<CallQueue> callQueues = callQueueDao.isCallInQueue(callerId, skillName, did, ucid);
        //Increment the Requested Count
        if (callQueues.size() > 0) {
            CallQueue callQueue = callQueues.get(0);
            callQueue.setReqCount(callQueue.getReqCount() + 1);
            callQueue.setIsActive(true);
            callQueueDao.save(callQueue);
        }
        return (!callQueues.isEmpty() ? true : false);
    }

    public int getPostionInQueue(String callerId, String skillName, String did, Long ucid) {
        List<CallQueue> callQueues = callQueueDao.getPostionInQueue(skillName, did);
        int i = 0;
        //Manupulate the position of the CallerId;
        Iterator<CallQueue> it = callQueues.iterator();
        while (it.hasNext()) {
            CallQueue callQueue = it.next();
            i = i + 1;
            if (callerId.equalsIgnoreCase(callQueue.getCallQueuePK().getCallerId()) && (ucid.longValue() == callQueue.getCallQueuePK().getUcid().longValue())) {
                log.debug("Equals success returning " + i);
                return i;
            }
        }

        //code not completed need to completet it
        return 0;
    }

    public boolean isQueueEmpty(String skillName, String did) {
        List<CallQueue> callQueues = callQueueDao.getPostionInQueue(skillName, did);
        return (callQueues.isEmpty() ? true : false);
    }

    public CallQueue getCallQueue(String callerId, String skillName, String did, Long ucid) {
        List<CallQueue> callQueues = callQueueDao.isCallInQueue(callerId, skillName, did, ucid);
        if (!callQueues.isEmpty()) {
            CallQueue callqueue = callQueues.get(0);
            return callqueue;
        }
        return null;
    }

    public void deleteCallQueue(String callerId, String skillName, String did, Long ucid) {
//        CallQueue callQueue = getCallQueue(callerId, skillName, did, ucid);
//        if (callQueue != null) {
//            callQueue.setIsActive(false);
//            callQueue.setEndTime(new Date());
//            callQueueDao.save(callQueue);
//        }
        callQueueDao.deleteCallQueue(ucid, skillName, did);
    }

    public void deleteAgentCallQueue(String callerId, String skillName, String did, Long ucid) {
        List<AgentCallQueue> callQueues = callQueueDao.isAgentCallInQueue(callerId, skillName, did, ucid);
        for (AgentCallQueue agentCallQueue : callQueues) {
            agentCallQueue.setActive(false);
            agentCallQueue.setEndTime(new Date());
            callQueueDao.saveAgentCallQueue(agentCallQueue);
        }
    }

    public List getCallQueuesByUser(Long userId) {
        return callQueueDao.getCallQueuesByUser(userId);
    }

    @Override
    public List getCallQueuesBySubUser(Long userId, String campaigns) {
        log.debug("Getting call queues for sub user :" + userId + " camapigns : " + " -> " + campaigns);
        return callQueueDao.getCallQueuesBySubUser(userId, campaigns);
    }

    public List<Map<String, Object>> getCallQueuesByUserAndDate(Long userId, String fromDate, String toDate) {
        return callQueueDao.getCallQueuesByUserAndDate(userId, fromDate, toDate);
    }

    public List<Map<String, Object>> getCallQueuesBySubUserAndDate(Long userId, String campaigns, String fromDate, String toDate) {
        log.debug("Getting call queues for sub user :" + userId + " camapigns : " + " -> " + campaigns);
        return callQueueDao.getCallQueuesBySubUserAndDate(userId, campaigns, fromDate, toDate);
    }

    public int getCallQueueCountForAgent(String user, String agentId) {
        List<Skill> agentSkills = skillManager.getSkillsOfAgent(user, agentId);
        List<String> skills = new ArrayList<>(agentSkills.size());
        if (agentSkills.isEmpty()) {
            return 0;
        } else {
            for (Skill skill : agentSkills) {
                skills.add(skill.getSkillName());
            }
        }
        return callQueueDao.getCallQueueCountBySkills(userManager.getUserByUsername(user).getId(), skills);
    }

    public List<Map<String, Object>> getCallQueueCountSkillWise(Long userId) {
        return callQueueDao.getCallQueueCountSkillWise(userId);
    }

    public List<Map<String, Object>> getAgentCallQueueList(Long userId) {
        return callQueueDao.getAgentCallQueueList(userId);
    }

    public List<Map<String, Object>> getAgentCallQueueListByDate(Long userId, String fromDate, String toDate) {
        return callQueueDao.getAgentCallQueueListByDate(userId, fromDate, toDate);
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    private SkillManager skillManager;
    private UserManager userManager;
}
