package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.SMSTemplate;
import java.util.List;

/**
 *
 * @author pavanj
 */
public class SMSTemplateDaoHibernate extends GenericDaoHibernate<SMSTemplate, Long> implements com.ozonetel.occ.dao.SMSTemplateDao {

    public SMSTemplateDaoHibernate() {
        super(SMSTemplate.class);
    }

    @Override
    public List<SMSTemplate> getSMSTemplatesByUser(String user) {
        return getHibernateTemplate().find("SELECT t FROM SMSTemplate t where t.user.username=?", user);
    }
}
