package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.Location;
import java.util.List;

/**
 * An interface that provides a data management interface to the Location table.
 */
public interface LocationDao extends GenericDao<Location, Long> {

    List<Location> getLocationsByUser(String username);
}