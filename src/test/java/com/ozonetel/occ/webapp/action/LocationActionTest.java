package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import com.ozonetel.occ.service.LocationManager;
import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.webapp.action.BaseActionTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

public class LocationActionTest extends BaseActionTestCase {
    private LocationAction action;

    @Override @SuppressWarnings("unchecked")
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        action = new LocationAction();
        LocationManager locationManager = (LocationManager) applicationContext.getBean("locationManager");
        action.setLocationManager(locationManager);
    
        // add a test location to the database
        Location location = new Location();

        // enter all required fields
        location.setName("TxTrVsOkItZcDnLuGmIaArEqIaXuWgUfLbVsGsVpJbMpXkYdUwXaOmSlXuVoSiBpBcDbFpIpPkFbSeMcSdDvCvVxZjJaTkLlWmGcSsLcNjYkSrDkQnXiEfFvWcRnArRsKaMyNkDuVfNpXiBgPgDxDtLwCjRjBsHkSbBsNmEeAxRrWqJnLmJnGwHuCzCgVyNvEjZuDzPbGiOhXcFdZoRdOdMzEiEqVvMfJwFmEdMjGvHhIdDwQxUpRmOfQdZsOyU");

        locationManager.save(location);
    }

    public void testSearch() throws Exception {
        assertEquals(action.list(), ActionSupport.SUCCESS);
        assertTrue(action.getLocations().size() >= 1);
    }

    public void testEdit() throws Exception {
        log.debug("testing edit...");
        action.setId(-1L);
        assertNull(action.getLocation());
        assertEquals("success", action.edit());
        assertNotNull(action.getLocation());
        assertFalse(action.hasActionErrors());
    }

    public void testSave() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setId(-1L);
        assertEquals("success", action.edit());
        assertNotNull(action.getLocation());

        Location location = action.getLocation();
        // update required fields
        location.setName("OqIpWwOdSuZoPcNpDjMmMxLbPfJtHhIdQzCsVzWfUyPaOmUbJrEeJtVxXuHtEaHhHpOiNoIhRhDhClGsIcAtIkQcCeAxTpIiDyMnXnXrHqZsOrUcWrSwQtBnBmClOfDeHxUzOrKmQjMcRgLrWyOdUkAeMkIsQeIeJlMmWdEuPeTuNrSmJzOvQhPrIzZqPtWeAaVoHqOgOqCzCpNmDrRtZzZtHkGxElDtHzVoDnEwVkMhXkFrGpJlDzReJiVrEjZ");

        action.setLocation(location);

        assertEquals("input", action.save());
        assertFalse(action.hasActionErrors());
        assertFalse(action.hasFieldErrors());
        assertNotNull(request.getSession().getAttribute("messages"));
    }

    public void testRemove() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletActionContext.setRequest(request);
        action.setDelete("");
        Location location = new Location();
        location.setId(-2L);
        action.setLocation(location);
        assertEquals("success", action.delete());
        assertNotNull(request.getSession().getAttribute("messages"));
    }
}