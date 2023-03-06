package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.DataDao;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.service.DataManager;
import com.ozonetel.occ.service.impl.GenericManagerImpl;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "DataService", endpointInterface = "com.ozonetel.occ.service.DataManager")
public class DataManagerImpl extends GenericManagerImpl<Data, Long> implements DataManager {
    DataDao dataDao;

    public DataManagerImpl(DataDao dataDao) {
        super(dataDao);
        this.dataDao = dataDao;
    }

	/*public List<Data> getDataByCallGroupId(Long callGroupId) {
		return dataDao.getDataByCallGroupId(callGroupId);
	}

	
	public boolean deleteDataByGroupId(Long callGroupId) {
		//return dataDao.deleteDataByGroupId(callGroupId);
		try{
		List<Data> dataList = getDataByCallGroupId(callGroupId);
		for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
			Data data = (Data) iterator.next();
			dataDao.remove(data.getData_id());
		}
		}catch (Exception e) {
			return false;
		}
		return true;
	}*/

/*	@Override
	public List<Campaign> getCampaignsByUserId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
*/
	public List<Data> getDataByCampaignId(Long callGroupId) {
		return dataDao.getDataByCampaignId(callGroupId);
	}

	public boolean deleteDataByCampaignId(Long campaignId2) {
		return dataDao.deleteDataByCampaignId(campaignId2);
	}

        
	public List<Data> getDataByPhoneNumber(String destination) {
		return dataDao.getDataByPhoneNumber(destination);
	}

	public List<Data> getDataByCampaignIdAndCalledInfo(Long campaignId) {
		List<Data> datas = dataDao.getDataByCampaignIdAndCalledInfo(campaignId );
//                for(Data data:datas){

                List<Data> datasList = new ArrayList<Data>();
                for(Data data : datas){
                   datasList.add(data);

                }
                for(int i=0 ;i< datas.size();i++){
                    Data data = datas.get(i);
                    if(data.getTriesRemaining()<=0){
                        datasList.remove(data);
                        data.setIsDone(Boolean.TRUE);// if the Number of Trails for this Data is Completed
                        log.debug("Seting the dataId="+data.getNextNumber()+"="+data.getIsDone());
                        dataDao.save(data);
                       
                    }
                }
                log.debug(">>dataList remiaing=="+datas.size());
                if(datasList.isEmpty())
                    return null;
                else
                    return datasList;
	}

        public Data getDataByCampaignIdAndCalledInfo(Long campaignId , Long agentId) {
		List<Data> datas = dataDao.getDataByCampaignIdAndCalledInfo(campaignId ,agentId);
//                for(Data data:datas){
                log.debug("DataList Size for Agent="+datas.size());
                List<Data> datasList = new ArrayList<Data>();
                for(Data data : datas){
                   datasList.add(data);
                   
                }
                for(int i=0 ;i< datas.size();i++){
                    Data data = datas.get(i);
                    log.debug("Numberof Tries Remaining=="+data.getTriesRemaining());
                    if(data.getTriesRemaining()<=0){
                        datasList.remove(data);
                        data.setIsDone(Boolean.TRUE);// if the Number of Trails for this Data is Completed set to 1
                        log.debug("Seting the dataId="+data.getNextNumber()+"="+data.getIsDone());
                        dataDao.save(data);
                    }
                     }
                if(datasList.isEmpty())
                    return null;
                else
                    return datasList.get(0);
	}

        public boolean isCampaignComplete(Long campaignId) {
		return dataDao.isCampaignComplete(campaignId);
	}

    /**
     * {@inheritDoc}
     */
    public int bulkUpdate(String query, Object[] values) {
        return dataDao.bulkUpdate(query, values);
    }

}