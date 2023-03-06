package com.ozonetel.occ.service;

import com.ozonetel.occ.model.SMSTemplate;
import java.util.List;

/**
 *
 * @author pavanj
 */
public interface SMSTemplateManager extends GenericManager<SMSTemplate, Long> {

    public List<SMSTemplate> getSMSTemplatesByUser(String user);
}
