package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.SMSTemplateDao;
import com.ozonetel.occ.model.SMSTemplate;
import com.ozonetel.occ.service.SMSTemplateManager;
import java.util.List;

/**
 *
 * @author pavanj
 */
public class SMSTemplateManagerImpl extends GenericManagerImpl<SMSTemplate, Long> implements SMSTemplateManager {

    public SMSTemplateManagerImpl(SMSTemplateDao genericDao) {
        super(genericDao);
        this.sMSTemplateDao=genericDao;
    }

    @Override
    public List<SMSTemplate> getSMSTemplatesByUser(String user) {
        return sMSTemplateDao.getSMSTemplatesByUser(user);
    }
    private SMSTemplateDao sMSTemplateDao;
}
