package com.ozonetel.occ.dao;

import java.util.List;

import com.ozonetel.occ.model.PreviewData;

/**
 * An interface that provides a data management interface to the PreviewData
 * table.
 */
public interface PreviewDataDao extends GenericDao<PreviewData, Long> {

    List<PreviewData> getDataByAgentId(String agentId);

    List<PreviewData> getDataByPhoneNumberAgentId(String agentId, String number);

    List<PreviewData> getPreviewDatByCampaign(Long campaignId);

    boolean isPreviewCampaignCompleted(Long campaignId);

    List<PreviewData> getPreviewDatByCampaign(String agentId, Long campaignId);

    List<PreviewData> getNextPreviewData(String agentId, Long campaignId, boolean agentWise);

    /**
     * Give a single number for non agent wise campaign.This is used when an
     * agent is freed ane we ask for one number.
     *
     * @param campaignId campaign ID
     * @return a record if found otherwise null.
     */
    public PreviewData getNumberToDialNonAgentWise(Long campaignId);

    /**
     * Return the number to be dialed which is assigned to the agent with
     * agentID
     *
     * @param agentID agent ID
     * @param campaignID campaign ID
     * @return data to dial
     */
    public PreviewData getNumberToDialForAgent(String agentID, Long campaignID);

    /**
     * Return the data with status "SENT" of campaign with id
     * <code>campaignId</code>
     *
     * @return
     */
    public List<PreviewData> getUnUpdatedData(Long campaignId);

    public List<PreviewData> getNumbersRemainingToDial(Long campaignId);

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
     * @param agentId
     * @return count
     */
    public Integer getCountOfNumbersRemainingToDialForAgent(Long campaignId, String agentId);

    public Long getCampaignIdofData(Long dataId);

    public int lockData(Long dataId);

}
