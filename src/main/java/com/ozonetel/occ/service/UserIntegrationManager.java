package com.ozonetel.occ.service;

import com.ozonetel.occ.model.UserIntegration;
import java.util.List;

/**
 *
 * @author PavanJ
 */
public interface UserIntegrationManager extends GenericManager<UserIntegration, Long> {

    public List<UserIntegration> getIntegrationsByUser(Long userId);
    
    public UserIntegration getUserIntegrationById(Long userId, Long integrationId);
    
}
