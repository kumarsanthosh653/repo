package com.ozonetel.occ.service;

import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.PreviewExtraData;
import java.util.Map;

/**
 *
 * @author pavanj
 */
public interface PreviewExtraDataManager extends GenericManager<PreviewExtraData, Long> {

    public Map<String, String> getCustomerData(Long dataId, Long campaignId);
}
