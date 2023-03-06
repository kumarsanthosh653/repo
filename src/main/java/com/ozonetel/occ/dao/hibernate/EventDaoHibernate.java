package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.dao.EventDao;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;

public class EventDaoHibernate extends GenericDaoHibernate<Event, Long> implements EventDao {
    
    public EventDaoHibernate() {
        super(Event.class);
    }
    
    @Override
    public List<Event> getEventsByUserAndDate(Long userId, Date dt) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDate = df.format(dt);
        return getHibernateTemplate().find("select e from Event e where e.userId='" + userId + "' and  e.startTime like '" + formatedDate + "%'");
    }
    
    @Override
    public Event getLastLoginEventForAgent(final Long userId, final Long agentId) {
        List<Event> eventList = getHibernateTemplate().executeFind(new HibernateCallback<List<Event>>() {
            
            @Override
            public List<Event> doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from Event e where e.agentId=:agentId and e.userId=:userId and e.event=:eventName order by e.eventId desc");
                q.setLong("agentId", agentId);
                q.setLong("userId", userId);
                q.setString("eventName", "login");
                q.setMaxResults(1);
                return q.list();
            }
        });
        
        if (CollectionUtils.isNotEmpty(eventList)) {
            return eventList.get(0);
        }
        
        return null;
    }
    

    public Event getLastEventForAgentNew(final Long userId, final Long agentId) {
        Long uniqueId = (Long) DataAccessUtils.uniqueResult(getHibernateTemplate().find("select MAX(e.eventId) from Event e where e.agentId=? and e.userId=? ", agentId, userId));
        log.debug("Got last evnt id:" + uniqueId + " for  agentid:" + agentId + "|UserId: " + userId);
        
        if (uniqueId != null) {
            return get(uniqueId);
        }
        
        log.debug("I have no last event. User ID:" + userId + " | agentId:" + agentId);
        return null;
    }
    
   

   
    public Event getLastEventForAgent(final Long userId, final Long agentId) {
        List<Event> eventList = getHibernateTemplate().executeFind(new HibernateCallback<List<Event>>() {
            
            @Override
            public List<Event> doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from Event e where e.agentId=:agentId and e.userId=:userId order by e.eventId desc");
                q.setLong("agentId", agentId);
                q.setLong("userId", userId);
                
                q.setMaxResults(1);
                return q.list();
            }
        });
        
        if (CollectionUtils.isNotEmpty(eventList)) {
            return eventList.get(0);
        }
        
        return null;
    }
    
}
