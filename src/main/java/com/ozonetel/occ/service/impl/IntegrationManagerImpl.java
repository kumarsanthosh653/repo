package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.IntegrationDao;
import com.ozonetel.occ.model.Integration;
import com.ozonetel.occ.service.IntegrationManager;
import java.util.List;

/**
 *
 * @author PavanJ
 */
public class IntegrationManagerImpl extends GenericManagerImpl<Integration, Long> implements IntegrationManager {

    public IntegrationManagerImpl(IntegrationDao integrationDao) {
        super(integrationDao);
        this.integrationDao = integrationDao;
    }

    @Override
    public List<Integration> getAllAvailableIntegrations() {
        return integrationDao.getAvailableIntegrations();
    }

    @Override
    public Integration getIntegrationByName(String name) {
        return integrationDao.getIntegrationByName(name);
    }

    private IntegrationDao integrationDao;
}
