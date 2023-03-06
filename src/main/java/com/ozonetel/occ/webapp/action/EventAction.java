package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;

public class EventAction extends BaseAction implements Preparable {
    private EventManager eventManager;
    private List events;
    private Event event;
    private Long  eventId;

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public List getEvents() {
        return events;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String eventId = getRequest().getParameter("event.eventId");
            if (eventId != null && !eventId.equals("")) {
                event = eventManager.get(new Long(eventId));
            }
        }
    }

    public String list() {
        events = eventManager.getAll();
        return SUCCESS;
    }

    public void setEventId(Long  eventId) {
        this. eventId =  eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String delete() {
        eventManager.remove(event.getEventId());
        saveMessage(getText("event.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (eventId != null) {
            event = eventManager.get(eventId);
        } else {
            event = new Event();
        }

        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }

        boolean isNew = (event.getEventId() == null);

        eventManager.save(event);

        String key = (isNew) ? "event.added" : "event.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}