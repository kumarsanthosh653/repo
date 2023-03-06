package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Integration;
import java.util.List;

/**
 *
 * @author PavanJ
 */
public interface IntegrationManager extends GenericManager<Integration, Long> {

        public List<Integration> getAllAvailableIntegrations();
        
        public Integration getIntegrationByName(String name);
    
}
