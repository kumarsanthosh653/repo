package com.ozonetel.occ.service;

import com.ozonetel.occ.model.PreviewData;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PreviewDataManager extends GenericManager<PreviewData, Long> {

    List<PreviewData> getDataByAgentId(String agentId);

    PreviewData getDataByPhoneNumberAgentId(String agentId, String number);

    List<PreviewData> getPreviewDataByCampaign(Long campaignId);

    boolean isPreviewCampaignCompleted(Long campaignId);

    List<PreviewData> getDataByAgentIdAndCamapign(String agentId, Long c);

    PreviewData getNextPreviewData(String agentId, Long campaignId, boolean agentWise);

    int getPreviewDataSize(String agentId, Long campaignId, boolean agentWise);

    /**
     * Gives a record to dial which is associated to the <code>agentID</code>
     *
     * @param campaignID
     * @param agentID
     * @return
     */
    public PreviewData getNumberToDialForAgent(Long campaignID, String agentID);

    /**
     * Give a single number for non agent wise campaign.This is used when an
     * agent is freed ane we ask for one number.
     *
     * @param campaignId campaign ID
     * @return a record if found otherwise null.
     */
    public PreviewData getNumberToDialNonAgentWise(Long campaignId);

    public PreviewData getNumberToDialNonAgentWiseFromDialer(Long campaignId);

    public PreviewData getNumberToDialForAgentFromDialer(Long campaignID, String agentID);

    /**
     * Return the numbers with status 'sent'(sent to Kookoo but not updated till
     * now)
     *
     * @param campaignId campaign id
     * @return unupdated preview data list
     */
    public List<PreviewData> getUnUpdatedData(Long campaignId);

    public boolean checkWhetherCampaignCompleted(Long campaignId);

    /**
     * Returns no.of records remaining to dial for a campaign with id
     * <code>campaignId</code>
     *
     * @param campaignId Campaign id
     * @return count
     */
    public Integer getCountOfNumbersRemainingToDial(Long campaignId);

    /**
     * Returns no.of records remaining to dial for a campaign with id
     * <code>campaignId</code> for that particular agent with
     * <code>agentId</code>
     *
     * @param campaignId Campaign id
     * @return count
     */
    public Integer getCountOfNumbersRemainingToDialForAgent(Long campaignId, String agentId);

    /**
     * if agent wants to dial the number later we reset the number to previous
     * state.
     *
     * @param pid primary key
     */
    public void resetPreviewNumber(Long pid);

    /**
     * As of now if agent skips a number we are treating it as a trail and
     * making it as failed call.
     *
     * @param pid primary key
     */
    public void skipPreviewNumber(Long pid, String message);

    /**
     * returns the id of the campaign the data belongs to
     *
     * @param dataId
     * @return campaign id
     */
    public Long getCampaignIdofData(Long dataId);

    /**
     *
     * @param dataId
     * @return
     */
    public int lockData(Long dataId);

    public void closePreviewNumber(Long pid, String disp, String comments, String message);
}
