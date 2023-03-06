package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.Constants;
import java.util.ArrayList;
import java.util.List;

import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.dao.PreviewDataDao;
import java.sql.SQLException;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;

public class PreviewDataDaoHibernate extends GenericDaoHibernate<PreviewData, Long> implements PreviewDataDao {

    public PreviewDataDaoHibernate() {
        super(PreviewData.class);
    }

    public List<PreviewData> getDataByAgentId(String agentId) {

        return (List<PreviewData>) getHibernateTemplate().find("select distinct pd from PreviewData pd where pd.agentId = ? and pd.deleted=false", agentId);

    }

    public List<PreviewData> getDataByPhoneNumberAgentId(String agentId, String number) {

        return (List<PreviewData>) getHibernateTemplate().find("from PreviewData pd where pd.dest like '%" + number + "%' and pd.agentId = ? and pd.deleted=false", agentId);

    }

    public boolean isPreviewCampaignCompleted(Long campaignId) {
//        List<PreviewData> previewData = (List<PreviewData>) getHibernateTemplate().find("select pd from PreviewData pd where pd.isDone is not true and pd.nextFlag is not true and pd.status is null and pd.campaign.campaignId = ? and pd.deleted=false", campaignId);
//        List<PreviewData> previewData = (List<PreviewData>) getHibernateTemplate().getSessionFactory().getCurrentSession()
//                .createQuery("select pd from PreviewData pd where pd.isDone is not true and pd.nextFlag is not true and pd.status is null and pd.campaign.campaignId =:campaignId and pd.deleted=false")
//                .setParameter("campaignId", campaignId)
//                .setMaxResults(1)
//                .list();
//        if (previewData.size() > 0) {
//            return false;
//        } else {
//            return true;
//        }
        
        return CollectionUtils.isNotEmpty(this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createQuery("select p from PreviewData p where p.campaign.campaignId=:campaignId and ifnull(p.status,'xxxxxx') in ('fail','xxxxxx') and p.nextFlag=false and p.deleted=false and p.currentTrail < p.campaign.ruleNot")
                .setParameter("campaignId", campaignId)
                .setMaxResults(1).list());
    }

    public List<PreviewData> getPreviewDatByCampaign(Long campaignId) {
        return (List<PreviewData>) getHibernateTemplate().find("from PreviewData pd where pd.campaign.campaignId = ? and pd.deleted=false", campaignId);
    }

    public List<PreviewData> getPreviewDatByCampaign(String agentId,
            Long campaignId) {
        return (List<PreviewData>) getHibernateTemplate().find("select distinct pd from PreviewData pd where pd.isDelete is not true and pd.agentId = '" + agentId + "' and pd.campaign.campaignId = " + campaignId + " and pd.deleted=false");

    }

    public List getNextPreviewData(String agentId, Long campaignId, boolean agentWise) {

        StringBuilder query = new StringBuilder();

        query.append("select distinct pd from PreviewData pd ");
        query.append(" where pd.campaign.campaignId = ").append(campaignId);
        query.append(" and pd.isDone is not true and pd.deleted=false and pd.nextFlag is not true and pd.status is null ");

        if (agentWise) {
            query.append(" and pd.agent.agentId = '").append(agentId).append("'");
        }
        query.append(" order by lastSelected");
        return (List<PreviewData>) getHibernateTemplate().find(query.toString());

    }

    @Override
    public PreviewData getNumberToDialNonAgentWise(final Long campaignId) {
//--------------------------------------------------------        
        List<PreviewData> data = (List<PreviewData>) getHibernateTemplate().executeFind(new HibernateCallback() {
            @Override
            public List<PreviewData> doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("SELECT NEW PreviewData(p.id,p.name,p.phoneNumber,p.createDate) from PreviewData p where p.campaign.campaignId= :campaignId and p.campaign.position like '%'||:position||'%' and  (p.status like '%'||:status||'%' or p.status is NULL)  and p.nextFlag=false and p.currentTrail < p.campaign.ruleNot and p.deleted=false order by p.priority desc,p.currentTrail asc,p.dateUpdated asc,p.id");
                q.setLong("campaignId", campaignId);
                q.setString("status", "fail");
                q.setString("position", "RUNNING");
                q.setMaxResults(1);
                return q.list();
            }
        });

        if (CollectionUtils.isNotEmpty(data)) {
            return data.get(0);
        }

        return null;
    }

    @Override
    public PreviewData getNumberToDialForAgent(final String agentID, final Long campaignID) {
//--------------------------------------------------------        
//        List<PreviewData> data = getHibernateTemplate().find("from PreviewData p where p.campaign.campaignId=? and p.campaign.position like '%'||?||'%' and p.agent.id=? and  (p.status like '%'||?||'%' or p.status is NULL)  and p.nextFlag=false and p.currentTrail < p.campaign.ruleNot order by p.currentTrail asc,p.dateUpdated asc,p.id", new Object[]{campaignID, "RUNNING", agentID, "fail"});

        List<PreviewData> data = (List<PreviewData>) getHibernateTemplate().executeFind(new HibernateCallback() {
            @Override
            public List<PreviewData> doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("SELECT NEW PreviewData(p.id,p.name,p.phoneNumber,p.createDate) from PreviewData p where p.campaign.campaignId=:campaignId and p.campaign.position like '%'||:position||'%' and p.agent.agentId=:agentId and  (p.status like '%'||:status||'%' or p.status is NULL)  and p.nextFlag=false and p.currentTrail < p.campaign.ruleNot and p.deleted=false order by p.priority desc, p.currentTrail asc,p.dateUpdated asc,p.id");
                q.setLong("campaignId", campaignID);
                q.setString("status", "fail");
                q.setString("position", "RUNNING");
                q.setString("agentId", agentID);
                q.setMaxResults(1);
                return q.list();
            }
        });

        if (CollectionUtils.isNotEmpty(data)) {
            return data.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PreviewData> getUnUpdatedData(Long campaignId) {
//--------------------------------------------------------        
        return (List<PreviewData>) getHibernateTemplate().find("from PreviewData p where p.campaign.campaignId=? and  p.status like '%'||?||'%' and p.nextFlag=true and p.deleted=false", campaignId, Constants.SENT);
    }

    //Non Agent wise
    @Override
    public List<PreviewData> getNumbersRemainingToDial(Long campaignId) {
//--------------------------------------------------------         
        return (List<PreviewData>) getHibernateTemplate().find("from PreviewData p where (p.status like '%'||?||'%' or p.status is NULL) and p.currentTrail < p.campaign.ruleNot and p.campaign.campaignId=? and p.deleted=false", new Object[]{"fail", campaignId});
    }

    @Override
    public Integer getCountOfNumbersRemainingToDial(Long campaignId) {
//-------------------------------------------------------- 
//        List<PreviewData> list = getHibernateTemplate().find("from PreviewData p where (p.status like '%'||?||'%' or p.status is NULL) and p.currentTrail < p.campaign.ruleNot and p.campaign.campaignId=? and p.deleted=false", new Object[]{"fail", campaignId});

        return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from PreviewData p where (p.status like '%'||?||'%' or p.status is NULL) and p.currentTrail < p.campaign.ruleNot and p.campaign.campaignId=? and p.deleted=false", new Object[]{"fail", campaignId}));

    }

    @Override
    public Integer getCountOfNumbersRemainingToDialForAgent(Long campaignId, String agentId) {
//--------------------------------------------------------         
        return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from PreviewData p where (p.status like '%'||?||'%' or p.status is NULL) and p.currentTrail < p.campaign.ruleNot and p.campaign.campaignId=? and p.agent.agentId=? and p.deleted=false ", new Object[]{"fail", campaignId, agentId}));
    }

    @Override
    public Long getCampaignIdofData(Long dataId) {
//--------------------------------------------------------         
        return DataAccessUtils.longResult(getHibernateTemplate().find("select p.campaign.campaignId from PreviewData p where p.id=", dataId));
    }

    public int lockData(Long dataId) {
        Query q = getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("UPDATE PreviewData p SET p.nextFlag=:flag,p.status=:status WHERE p.id=:id");
        q.setBoolean("flag", true);
        q.setString("status", Constants.SENT);
        q.setLong("id", dataId);
        return q.executeUpdate();
    }

}
