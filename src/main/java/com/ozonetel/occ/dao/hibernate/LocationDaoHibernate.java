package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.dao.LocationDao;
import java.util.List;

public class LocationDaoHibernate extends GenericDaoHibernate<Location, Long> implements LocationDao {

    public LocationDaoHibernate() {
        super(Location.class);
    }

    public List<Location> getLocationsByUser(String username) {
        return getHibernateTemplate().find("select distinct l from Location l where l.user.username = ? ",username);
    }
}
