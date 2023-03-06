package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Location;
import java.util.List;
import javax.jws.WebService;

@WebService
public interface LocationManager extends GenericManager<Location, Long> {

    List<Location> getLocationsByUser(String username);
}