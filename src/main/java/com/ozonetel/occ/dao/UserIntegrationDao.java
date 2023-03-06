
package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.UserIntegration;
import java.util.List;

/**
 *
 * @author PavanJ
 */
public interface UserIntegrationDao extends  GenericDao<UserIntegration, Long>{
        public List<UserIntegration> getIntegrationsByUser(Long userId);
        
        public UserIntegration getUserIntegrationById(Long userId, Long integrationId);
        
        public UserIntegration getUserIntegrationByIntegrationName(Long userId, String integrationName);

}
