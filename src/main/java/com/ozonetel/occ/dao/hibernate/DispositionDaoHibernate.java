package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.dao.DispositionDao;
import java.util.List;

public class DispositionDaoHibernate extends GenericDaoHibernate<Disposition, Long> implements DispositionDao {

    public DispositionDaoHibernate() {
        super(Disposition.class);
    }

    @Override
    public List<Disposition> getDispositionsByCampaign(Long campaignId) {
        return getHibernateTemplate().find("select distinct s from Campaign c,IN(c.dispositions) s where c.campaignId=?", campaignId);
    }

    @Override
    public List<Disposition> getActiveDispositionsWithoutCampaigns(Long campaignId) {
        return getHibernateTemplate().find("select new Disposition(d.id,d.reason,d.callAlternateNumber,d.active) from   Disposition d where d.campaign.campaignId = ? and d.active=true and d.reason is not null and d.reason!=''", campaignId);

    }

}
