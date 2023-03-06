/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.PreviewDataAuditLogDao;
import com.ozonetel.occ.model.PreviewDataAuditLog;
import java.util.List;

/**
 *
 * @author venkatrao
 */
public class PreviewDataAuditLogDaoHibernate extends GenericDaoHibernate<PreviewDataAuditLog, Long> implements PreviewDataAuditLogDao {
    
    public PreviewDataAuditLogDaoHibernate() {
        super(PreviewDataAuditLog.class);
    }
    
    @Override
    public PreviewDataAuditLog getByPidAndAgentId(Long pid, Long agentId) {
        
        List<PreviewDataAuditLog> logsList = (List<PreviewDataAuditLog>) getHibernateTemplate().find("select pdal from PreviewDataAuditLog pdal where pdal.pid = ? and pdal.agentId = ? order by pdal.id desc", pid, agentId);
        
        if (logsList != null && !logsList.isEmpty())
            return logsList.get(0);
        
        return null;
    }
}
