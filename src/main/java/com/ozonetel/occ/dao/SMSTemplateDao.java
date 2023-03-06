package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.SMSTemplate;
import java.util.List;

/**
 *
 * @author pavanj
 */
public interface SMSTemplateDao extends GenericDao<SMSTemplate, Long> {

    public List<SMSTemplate> getSMSTemplatesByUser(String user);
}
