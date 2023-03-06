package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.PreviewExtraDataDao;
import com.ozonetel.occ.model.OutboundCustomerInfo;
import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.PreviewExtraData;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.PreviewExtraDataManager;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author pavanj
 */
public class PreviewExtraDataManagerImpl extends GenericManagerImpl<PreviewExtraData, Long> implements PreviewExtraDataManager {

    public PreviewExtraDataManagerImpl(PreviewExtraDataDao previewExtraDataDao) {
        super(previewExtraDataDao);
        this.previewExtraDataDao = previewExtraDataDao;
    }

    @Override
    public Map<String, String> getCustomerData(Long dataId, Long campaignId) {
//        OutboundCustomerInfo outboundCustomerInfo = previewExtraDataDao.getCustomerData(dataId);
        Map<String, String> customerInfo = new LinkedHashMap<>();

        try {
            PreviewDataMap previewDataMap = campaignManager.getPreviewDataMap(campaignId);
            String[] mapColumns = StringUtils.splitPreserveAllTokens(previewDataMap.getValue(), ",");
            String[] splitRawData = StringUtils.splitPreserveAllTokens(previewExtraDataDao.getCustomerData(dataId), "~");

            int i;
            for (i = 0; i < Math.min(mapColumns.length, splitRawData.length); i++) {
                customerInfo.put(mapColumns[i], splitRawData[i]);
            }

            if (mapColumns.length != splitRawData.length) {
                if (mapColumns.length > splitRawData.length) {
                    for (int j = i; j < mapColumns.length; j++) {
                        customerInfo.put(mapColumns[j], "");
                    }
                } else {
                    for (int j = i; j < splitRawData.length; j++) {
                        customerInfo.put("Col " + j, splitRawData[j]);
                    }
                }

            }
        } catch (Exception e) {
            log.error("Error getting info for data id:" + dataId + e.getMessage(), e);
        }
        log.debug("Returning customerinfo for dataId :" + dataId + " as  : " + customerInfo);

        return customerInfo;

    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    private PreviewExtraDataDao previewExtraDataDao;
    private CampaignManager campaignManager;

}
