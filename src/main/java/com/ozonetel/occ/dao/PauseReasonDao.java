package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.GenericDao;

import com.ozonetel.occ.model.PauseReason;
import java.util.List;

/**
 * An interface that provides a data management interface to the PauseReason table.
 */
public interface PauseReasonDao extends GenericDao<PauseReason, Long> {
    List<PauseReason> getPauseReasonByUser(String userName);

}