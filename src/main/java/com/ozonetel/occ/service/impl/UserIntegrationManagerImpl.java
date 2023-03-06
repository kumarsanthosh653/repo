package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.UserIntegration;
import java.util.List;
import com.ozonetel.occ.dao.UserIntegrationDao;
import com.ozonetel.occ.service.UserIntegrationManager;

/**
 *
 * @author PavanJ
 */
public class UserIntegrationManagerImpl extends GenericManagerImpl<UserIntegration, Long> implements UserIntegrationManager {

    public UserIntegrationManagerImpl(UserIntegrationDao integrationDao) {
        super(integrationDao);
        this.integrationDao = integrationDao;
    }

    @Override
    public List<UserIntegration> getIntegrationsByUser(Long userId) {
        return integrationDao.getIntegrationsByUser(userId);
    }

    @Override
    public UserIntegration getUserIntegrationById(Long userId, Long integrationId) {
        return integrationDao.getUserIntegrationById(userId, integrationId);
    }

    private UserIntegrationDao integrationDao;

}
