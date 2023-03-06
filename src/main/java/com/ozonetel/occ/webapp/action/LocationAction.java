package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.LocationManager;
import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.model.User;

import java.util.List;

public class LocationAction extends BaseAction implements Preparable {
    private LocationManager locationManager;
    private List locations;
    private Location location;
    private Long  id;

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public List getLocations() {
        return locations;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String locationId = getRequest().getParameter("location.id");
            if (locationId != null && !locationId.equals("")) {
                location = locationManager.get(new Long(locationId));
            }
        }
    }

    public String list() {
        locations = locationManager.getLocationsByUser(getRequest().getRemoteUser());
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String delete() {
        locationManager.remove(location.getId());
        saveMessage(getText("location.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            location = locationManager.get(id);
        } else {
            location = new Location();
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

        boolean isNew = (location.getId() == null);

        User user = userManager.getUserByUsername(getRequest().getRemoteUser());
        location.setUser(user);
        location = locationManager.save(location);

        String key = (isNew) ? "location.added" : "location.updated";
        saveMessage(getText(key));
        return SUCCESS;
    }
}