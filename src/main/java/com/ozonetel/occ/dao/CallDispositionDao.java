package com.ozonetel.occ.dao;

import java.util.List;

import com.ozonetel.occ.dao.GenericDao;

import com.ozonetel.occ.model.CallDisposition;

/**
 * An interface that provides a data management interface to the CallDisposition table.
 */
public interface CallDispositionDao extends GenericDao<CallDisposition, Long> {

	List<CallDisposition> getDispositionsByCampaign(Long campaignId2);

}