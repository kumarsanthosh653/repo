/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.PreviewDataAuditLogDao;
import com.ozonetel.occ.model.PreviewDataAuditLog;
import com.ozonetel.occ.service.PreviewDataAuditLogManager;
import javax.jws.WebService;

/**
 *
 * @author venkatrao
 */
@WebService(serviceName = "PreviewDataAuditLogService", endpointInterface = "com.ozonetel.occ.service.PreviewDataAuditLogManager")
public class PreviewDataAuditLogManagerImpl extends GenericManagerImpl<PreviewDataAuditLog, Long> implements PreviewDataAuditLogManager {
    
    PreviewDataAuditLogDao previewDataAuditLogDao;
    
    public PreviewDataAuditLogManagerImpl(PreviewDataAuditLogDao previewDataAuditLogDao) {
        super(previewDataAuditLogDao);
        this.previewDataAuditLogDao = previewDataAuditLogDao;
    }
    
    @Override
    public PreviewDataAuditLog getByPidAndAgentId(Long pid, Long agentId) {
        return previewDataAuditLogDao.getByPidAndAgentId(pid, agentId);
    }
    
}
