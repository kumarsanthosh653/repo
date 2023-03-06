package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Event;
import java.util.Date;

import javax.jws.WebService;

@WebService
public interface EventManager extends GenericManager<Event, Long> {

    /**
     * Log the agent event in agent_data table.
     *
     * @param eventName the event just happened
     * @param userId User unique ID
     * @param userName user login name
     * @param agentUniqId agent unique ID
     * @param agentLoginId agent login name
     * @param currentAgentMode latest mode after the event has happened.
     * @param startTime event start time
     * @param ucid <code>ucid</code> if this is related to call event
     * @param eventMessage event info like what caused the event.
     * @param miscDetails extra info
     */
    public void logEvent(String eventName, Long userId, String userName, Long agentUniqId, String agentLoginId, Agent.Mode currentAgentMode, Date startTime, Long ucid, String eventMessage, String miscDetails);

    public void updatePreviousEvent(Long userId, String user, Long agentUniqId, String agentLoginId, Date endDate);

    public void updateLastLoginEvent(Long userId, String user, Long agentUniqId, String agentLoginId, Date time);

    public Event getLastEventForAgent(Long userId, String user, Long agentUniqId, String agentLoginId);

    public void updateChatSessionsCountInBusyEvent(String user, Long agentUniqId, String agentLoginId, int sessionCount);

}
