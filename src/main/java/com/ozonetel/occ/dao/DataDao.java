package com.ozonetel.occ.dao;

import java.util.List;

import com.ozonetel.occ.dao.GenericDao;

import com.ozonetel.occ.model.Data;

/**
 * An interface that provides a data management interface to the Data table.
 */
public interface DataDao extends GenericDao<Data, Long> {

	//List<Data> getDataByCallGroupId(Long callGroupId);

	List<Data> getDataByCampaignId(Long callGroupId);

	boolean deleteDataByCampaignId(Long campaignId2);

	List<Data> getDataByPhoneNumber(String destination);

	List<Data> getDataByCampaignIdAndCalledInfo(Long campaignId);

        List<Data> getDataByCampaignIdAndCalledInfo(Long campaignId , Long agentId);

        public boolean isCampaignComplete(Long campaignId);

//	/boolean deleteDataByGroupId(Long callGroupId);

}