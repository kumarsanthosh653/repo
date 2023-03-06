package com.ozonetel.occ.dao.hibernate;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;

import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.dao.DataDao;
import com.ozonetel.occ.dao.hibernate.GenericDaoHibernate;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class DataDaoHibernate extends GenericDaoHibernate<Data, Long> implements DataDao {

    public DataDaoHibernate() {
        super(Data.class);
    }


	//This returns the list of customers who still have to be called always.
	public List<Data> getDataByCampaignId(Long callGroupId) {
		List<Data> dataList = new ArrayList<Data>();
    	dataList = getHibernateTemplate().find("from Data da where da.state='Fail' and da.campaign.campaignId=?", callGroupId);
    	return dataList;
	}

	public boolean deleteDataByCampaignId(Long campaignId2) {
		try{
			List<Data> dataList = getDataByCampaignId(campaignId2);
			for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
				Data data = (Data) iterator.next();
				getHibernateTemplate().delete(data);
			}
		}catch (Exception e) {
//			e.printStackTrace(); // commented
                    log.error(e.getMessage());
			return false;
		}
		return true;
	}

        public List<Data> getDataByPhoneNumber(String destination) {
		List<Data> dataList = new ArrayList<Data>();
    	dataList = getHibernateTemplate().find("from Data da where da.dest like '%"+destination+"%'");
    	return dataList;
	}


	public List<Data> getDataByCampaignIdAndCalledInfo(Long campaignId) {
        List<Data> dataList = new ArrayList<Data>();
    	dataList = getHibernateTemplate().find("from Data da where da.state='Fail' and da.playback_info is NULL and da.campaign.campaignId=?", campaignId);
    	return dataList;
	}

        public List<Data> getDataByCampaignIdAndCalledInfo(Long campaignId ,Long agentId) {
            List<Data> dataList = new ArrayList<Data>();
            dataList = getHibernateTemplate().find("from Data da where da.state='Fail' and da.playback_info is NULL and da.agent.id="+agentId+"and da.campaign.campaignId=? ", campaignId);
            return dataList;
	}

        public boolean isCampaignComplete(Long campaignId) {

          List<Data> datas =  getHibernateTemplate().find("from Data da where da.isDone is not true and da.campaign.campaignId=?", campaignId);

          if(datas.size() >0){
          return false;
          }else{
              return true;
          }
        }
}
