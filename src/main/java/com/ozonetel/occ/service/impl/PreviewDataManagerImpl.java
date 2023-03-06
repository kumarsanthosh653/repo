package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.PreviewDataDao;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.util.HttpUtils;
import java.util.Date;

import java.util.List;
import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.utils.URIBuilder;

@WebService(serviceName = "PreviewDataService", endpointInterface = "com.ozonetel.occ.service.PreviewDataManager")
public class PreviewDataManagerImpl extends GenericManagerImpl<PreviewData, Long> implements PreviewDataManager {

    PreviewDataDao previewDataDao;

    public PreviewDataManagerImpl(PreviewDataDao previewDataDao) {
        super(previewDataDao);
        this.previewDataDao = previewDataDao;
    }

    public List getDataByAgentId(String agentId) {
        return previewDataDao.getDataByAgentId(agentId);
    }

    public PreviewData getDataByPhoneNumberAgentId(String agentId, String number) {
        // TODO Auto-generated method stub
        return previewDataDao.getDataByPhoneNumberAgentId(agentId, number).get(0);
    }

    public List<PreviewData> getPreviewDataByCampaign(Long campaignId) {
        return previewDataDao.getPreviewDatByCampaign(campaignId);
    }

    public boolean isPreviewCampaignCompleted(Long campaignId) {
        return previewDataDao.isPreviewCampaignCompleted(campaignId);
    }

    public List<PreviewData> getDataByAgentIdAndCamapign(String agentId, Long campaignId) {
        return previewDataDao.getPreviewDatByCampaign(agentId, campaignId);
    }

    public PreviewData getNextPreviewData(String agentId, Long campaignId, boolean agentWise) {
        List<PreviewData> previewDataList = previewDataDao.getNextPreviewData(agentId, campaignId, agentWise);
        if (previewDataList.size() > 0) {
            return previewDataList.get(0);
        } else {
            return null;
        }
    }

    public int getPreviewDataSize(String agentId, Long campaignId, boolean agentWise) {
        List<PreviewData> previewDataList = previewDataDao.getNextPreviewData(agentId, campaignId, agentWise);
        return previewDataList.size();
    }

    private PreviewData getNumberFromDialer(Long campaignId, String agentId) {
        try {
            URIBuilder urib = new URIBuilder(dialerUrl);
            urib.addParameter("campaignId", "" + campaignId)
                    .addParameter("action", "getPreviewNumber");
            if (StringUtils.isNotBlank(agentId)) {
                urib.addParameter("agentId", "" + agentId);
            }
            HttpResponseDetails response = HttpUtils.doGet(urib.build().toString());
            log.debug("Got next preview number response:" + response + " | Body:" + response.getResponseBody());
            response.setResponseBody(StringUtils.trim(response.getResponseBody()));
            if (NumberUtils.isNumber(response.getResponseBody())) {
                log.debug("Got preview id:'" + response.getResponseBody() + "'");
                return previewDataDao.get(Long.valueOf(response.getResponseBody()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public PreviewData getNumberToDialNonAgentWiseFromDialer(Long campaignId) {
        return getNumberFromDialer(campaignId, null);
    }

    @Override
    public PreviewData getNumberToDialForAgentFromDialer(Long campaignID, String agentID) {
        return getNumberFromDialer(campaignID, agentID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized PreviewData getNumberToDialNonAgentWise(Long campaignId) {
//----------------------------------------------------------
        PreviewData tmpData = previewDataDao.getNumberToDialNonAgentWise(campaignId);
        if (tmpData == null) {
            checkWhetherCampaignCompleted(campaignId);
            return null;
        }

        log.debug("Data@BeforeLock:" + tmpData);
        lockData(tmpData.getId());
        return tmpData;
    }

    @Override
    public PreviewData getNumberToDialForAgent(Long campaignID, String agentID) {
//----------------------------------------------------------        
        log.info("Trying to get number for agent with ID:" + agentID);
        PreviewData tmpData = previewDataDao.getNumberToDialForAgent(agentID, campaignID);
        if (tmpData == null) {
            checkWhetherCampaignCompleted(campaignID);
            return null;
        }

        log.debug("Data@BeforeLock:" + tmpData);
        lockData(tmpData.getId());
        return tmpData;
    }

    @Override
    public boolean checkWhetherCampaignCompleted(Long campaignId) {
//        List<PreviewData> dataLeft = getNumbersRemainingToDial(campaignId);
        if (getCountOfNumbersRemainingToDial(campaignId) != 0) {
            log.info("There are some numbers to dial for the campaign :" + campaignId + " | Cmapign state -->  " + campaignManager.get(campaignId).getPosition());
            return false;
        } else {
            List dataLeft = getUnUpdatedData(campaignId);
            if (CollectionUtils.isNotEmpty(dataLeft)) {
                log.info("Some data is on call :" + dataLeft);
                return false;
            }
        }
        log.info("Campaign can be completed ----> " + campaignId);
        campaignManager.setCampaignCompleted(campaignId);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getCountOfNumbersRemainingToDial(Long campaignId) {
        return previewDataDao.getCountOfNumbersRemainingToDial(campaignId);
//        if (count.equals(0)) {
//            checkWhetherCampaignCompleted(campaignId);
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOfNumbersRemainingToDialForAgent(Long campaignId, String agentId) {
        Integer count = previewDataDao.getCountOfNumbersRemainingToDialForAgent(campaignId, agentId);
        if (count.equals(0)) {
            checkWhetherCampaignCompleted(campaignId);
        }
        return count;
    }

    @Override
    public List<PreviewData> getUnUpdatedData(Long campaignId) {
//----------------------------------------------------------
        return previewDataDao.getUnUpdatedData(campaignId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPreviewNumber(Long pid) {
//----------------------------------------------------------        
        PreviewData previewData = previewDataDao.get(pid);
        if (previewData != null) {
            log.debug("Got previewData as : " + previewData);
            if (previewData.getStatus().equalsIgnoreCase("sent")) {
                previewData.setNextFlag(false);
                previewData.setStatus(null);
                previewData.setDateUpdated(new Date());
                save(previewData);
            }else{
                log.debug("Not resetting preview data as status is not sent | "+pid);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void skipPreviewNumber(Long pid, String message) {
//----------------------------------------------------------        
        PreviewData data = previewDataDao.get(pid);
        data.setDialMessage(message);
        data.setStatus("Fail");
        data.setNextFlag(false);
        //
        // ----- > Treat it as a trail if agent skips the number.
        data.setCurrentTrail(data.getCurrentTrail() + 1);
        data.setDateUpdated(new Date());
        data = previewDataDao.save(data);
        log.debug("Skipped data :" + data.toLongString() + " | " + message);
    }

    public void closePreviewNumber(Long pid, String disp, String comments, String message) {
        PreviewData data = previewDataDao.get(pid);
        data.setDialMessage(message);
        data.setStatus("Fail");
        data.setNextFlag(false);
        data.setCurrentTrail(0L + data.getCampaign().getRuleNot());
        data.setDisposition(disp);
        data.setComments(comments);
        data.setDateUpdated(new Date());
        data = previewDataDao.save(data);
        log.debug(data.toLongString() + " | " + message);
    }

    @Override
    public Long getCampaignIdofData(Long dataId) {
        return previewDataDao.getCampaignIdofData(dataId);
    }

    @Override
    public int lockData(Long dataId) {
        return previewDataDao.lockData(dataId);
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setDialerUrl(String dialerUrl) {
        this.dialerUrl = dialerUrl;
    }

    private CampaignManager campaignManager;
    private String dialerUrl;
}
