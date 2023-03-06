/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.IvrFlowDao;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.IvrFlow;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 *
 * @author rajeshdas
 */
public class IvrFlowDaoHibernate extends GenericDaoHibernate<IvrFlow, Long>
        implements IvrFlowDao {
    
    public IvrFlowDaoHibernate() {
        super(IvrFlow.class);
    }
    
    @Override
    public List<IvrFlow> getFeedbackIVRList(Long userId) {
        log.debug("Inside the getFeedbackIVRList");
        List<IvrFlow> ivrFeedbackList = new ArrayList<>();
//        return (List<IvrFlow>) getHibernateTemplate().find("from IvrFlow i where i.type = 3 and  i.user.id=?", userId);
        
//        SQLQuery query = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery("select IVRFlowID, IVRFlowName from IVR_FlowDetails where User_ID=2060 and is_transfer is true");
        Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(
                      "select IVRFlowID, IVRFlowName from IVR_FlowDetails where User_ID=:userId and is_transfer is true")
                      .setParameter("userId", userId);
        List<Object[]> rows = query.list();
//        log.debug("rows : "+rows);
        rows.forEach(System.out::println);
        for(Object[] row : rows){
            IvrFlow ivr = new IvrFlow();
            ivr.setFlowId(Long.parseLong(row[0].toString()));
            ivr.setFlowName(row[1].toString());
//            log.debug("IVR Flowd Details : "+ivr);
            ivrFeedbackList.add(ivr);
        }
        log.debug("Finally got the ivrFeedbackList : "+ivrFeedbackList);
        return ivrFeedbackList;
    }
}
