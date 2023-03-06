package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.Event;
import java.util.Date;
import java.util.List;

/**
 * An interface that provides a data management interface to the Event table.
 */
public interface EventDao extends GenericDao<Event, Long> {

    List<Event> getEventsByUserAndDate(Long userId, Date dt);

    public Event getLastLoginEventForAgent(Long userId, Long agentId);

    public Event getLastEventForAgent(Long userId, Long agentId);

    public Event getLastEventForAgentNew(final Long userId, final Long agentId);
}
