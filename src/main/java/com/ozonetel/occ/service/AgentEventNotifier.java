package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AgentEvent;

/**
 *
 * @author pavanj
 */
public interface AgentEventNotifier {

    public boolean notifyEvent(Agent agent,String event, AgentEvent agentEvent);
}
