package com.ozonetel.occ.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.model.CallDisposition;
import com.ozonetel.occ.dao.CallDispositionDao;


public class CallDispositionDaoHibernate extends GenericDaoHibernate<CallDisposition, Long> implements CallDispositionDao {

    public CallDispositionDaoHibernate() {
        super(CallDisposition.class);
    }

	public List getDispositionsByCampaign(Long campaignId2) {
		List<CallDisposition> list = new ArrayList<CallDisposition>();
		log.debug("Campaign Id : ................"+campaignId2);
		list = getHibernateTemplate().find("from CallDisposition c where c.campaign.campaignId = ?",campaignId2);
		log.debug("List : "+list);
		return list;
	}
    
    
}
