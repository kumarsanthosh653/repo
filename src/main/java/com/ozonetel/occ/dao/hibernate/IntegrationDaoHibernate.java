package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.IntegrationDao;
import com.ozonetel.occ.model.Integration;
import java.util.List;

/**
 *
 * @author PavanJ
 */
public class IntegrationDaoHibernate extends GenericDaoHibernate<Integration, Long> implements IntegrationDao {

    public IntegrationDaoHibernate() {
        super(Integration.class);
    }

    @Override
    public List<Integration> getAvailableIntegrations() {
        return getAll();
    }

    @Override
    public Integration getIntegrationByName(String name) {
        List<Integration> integrations = getHibernateTemplate().find("from Integration i where i.name=?", name);
        if (integrations == null || integrations.isEmpty()) {
            return null;
        }

        return integrations.get(0);
    }

}
