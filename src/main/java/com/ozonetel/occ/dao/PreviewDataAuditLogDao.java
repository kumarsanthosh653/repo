/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.PreviewDataAuditLog;

/**
 *
 * @author venkatrao
 */
public interface PreviewDataAuditLogDao extends GenericDao<PreviewDataAuditLog, Long> {
    
    public PreviewDataAuditLog getByPidAndAgentId(Long pid, Long agentId);
    
}
