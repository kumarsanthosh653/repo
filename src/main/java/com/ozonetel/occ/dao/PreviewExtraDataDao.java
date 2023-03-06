package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.OutboundCustomerInfo;
import com.ozonetel.occ.model.PreviewExtraData;

/**
 *
 * @author pavanj
 */
public interface PreviewExtraDataDao extends GenericDao<PreviewExtraData, Long> {

    public String getCustomerData(Long dataId);

}
