package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.UserIntegration;
import java.util.List;
import com.ozonetel.occ.dao.UserIntegrationDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author PavanJ
 */
public class UserIntegrationDaoHibernate extends GenericDaoHibernate<UserIntegration, Long> implements UserIntegrationDao {

    public UserIntegrationDaoHibernate() {
        super(UserIntegration.class);
    }

    @Override
    public List<UserIntegration> getIntegrationsByUser(Long userId) {
        return getHibernateTemplate().find("from UserIntegration i where i.userId=?", userId);

    }

    @Override
    public UserIntegration getUserIntegrationById(Long userId, Long integrationId) {
        List<UserIntegration> userIntegrations = getHibernateTemplate().find("from UserIntegration u where u.userId = ? and u.integration.id=? ", userId, integrationId);
        if (userIntegrations == null || userIntegrations.isEmpty()) {
            return null;
        }
        return userIntegrations.get(0);
    }

    public UserIntegration getUserIntegrationByIntegrationName(Long userId, String integrationName) {
        List<UserIntegration> userIntegrations = getHibernateTemplate().find("from UserIntegration u where u.userId = ? and u.integration.name=?", userId, integrationName);
        if (userIntegrations == null || userIntegrations.isEmpty()) {
            return null;
        }
        return userIntegrations.get(0);
    }

}
