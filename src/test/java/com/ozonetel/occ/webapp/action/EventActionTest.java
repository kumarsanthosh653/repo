package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class EventActionTest extends BaseActionTestCase {
    private EventAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new EventAction();
        EventManager eventManager = (EventManager) applicationContext.getBean("eventManager");
        action.setEventManager(eventManager);
    
        // add a test event to the database
        Event event = new Event();

        // enter all required fields

        eventManager.save(event);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getEvents().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setEventId(-1L);
        assertNull(action.getEvent());
        assertEquals("success", action.edit());
        assertNotNull(action.getEvent());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setEventId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getEvent());

        Event event = action.getEvent();
        // update required fields

        action.setEvent(event);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        Event event = new Event();
        event.setEventId(-2L);
        action.setEvent(event);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}