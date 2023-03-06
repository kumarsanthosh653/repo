package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.LocationDao;
import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.service.LocationManager;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "LocationService", endpointInterface = "com.ozonetel.occ.service.LocationManager")
public class LocationManagerImpl extends GenericManagerImpl<Location, Long> implements LocationManager {
    LocationDao locationDao;

    public LocationManagerImpl(LocationDao locationDao) {
        super(locationDao);
        this.locationDao = locationDao;
    }

    public List<Location> getLocationsByUser(String username) {
        return locationDao.getLocationsByUser(username);
    }
}