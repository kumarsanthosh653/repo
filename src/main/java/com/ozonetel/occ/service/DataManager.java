package com.ozonetel.occ.service;

import com.ozonetel.occ.service.GenericManager;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DataManager extends GenericManager<Data, Long> {

	//List<Data> getDataByCallGroupId(Long callGroupId);

	
	List<Data> getDataByCampaignId(Long callGroupId);
	
	
	//boolean deleteDataByGroupId(Long callGroupId);


	boolean deleteDataByCampaignId(Long campaignId2);

       
        
	List<Data> getDataByPhoneNumber(String destination);


	List<Data> getDataByCampaignIdAndCalledInfo(Long campaignId);

	
	//List<CallGroup> getGroupsByUserId(Long id);
	
	//List<Campaign> getCampaignsByUserId(Long id);

        public boolean isCampaignComplete(Long campaignId);

        //gets the
        Data getDataByCampaignIdAndCalledInfo(Long campaignId , Long agentId);

    /**
     * Generic method used to update bulk of objects of a particular query.
     * @param query is query of the bulk update
     * @param values is array of values and condition values
     * @return int no.of records updated
     */
    int bulkUpdate(String query, Object[] values);
}