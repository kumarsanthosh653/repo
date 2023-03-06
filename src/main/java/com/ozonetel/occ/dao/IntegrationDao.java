package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.Integration;
import java.util.List;

/**
 *
 * @author PavanJ
 */
public interface IntegrationDao extends GenericDao<Integration, Long> {

    public List<Integration> getAvailableIntegrations();
    public Integration getIntegrationByName(String name);

}
