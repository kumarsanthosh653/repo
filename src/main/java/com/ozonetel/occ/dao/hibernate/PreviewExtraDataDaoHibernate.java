package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.PreviewExtraDataDao;
import com.ozonetel.occ.model.OutboundCustomerInfo;
import com.ozonetel.occ.model.PreviewExtraData;
import org.springframework.dao.support.DataAccessUtils;

/**
 *
 * @author pavanj
 */
public class PreviewExtraDataDaoHibernate extends GenericDaoHibernate<PreviewExtraData, Long> implements PreviewExtraDataDao {

    public PreviewExtraDataDaoHibernate() {
        super(PreviewExtraData.class);
    }

    @Override
    public String getCustomerData(Long dataId) {        
         return (String)DataAccessUtils.uniqueResult(getHibernateTemplate().find("select d.data from PreviewExtraData d where d.previewData.id= ? ",dataId));
    }

}
