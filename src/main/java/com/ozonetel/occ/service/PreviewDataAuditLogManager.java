/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.service;

import com.ozonetel.occ.model.PreviewDataAuditLog;
import javax.jws.WebService;

/**
 *
 * @author venkatrao
 */
@WebService
public interface PreviewDataAuditLogManager extends GenericManager<PreviewDataAuditLog, Long> {
    
    public PreviewDataAuditLog getByPidAndAgentId(Long pid, Long agentId);
    
}
