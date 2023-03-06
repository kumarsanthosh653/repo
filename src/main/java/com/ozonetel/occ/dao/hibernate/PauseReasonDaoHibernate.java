package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.dao.PauseReasonDao;
import com.ozonetel.occ.dao.hibernate.GenericDaoHibernate;
import java.util.List;

public class PauseReasonDaoHibernate extends GenericDaoHibernate<PauseReason, Long> implements PauseReasonDao {

    public PauseReasonDaoHibernate() {
        super(PauseReason.class);
    }
    public List<PauseReason> getPauseReasonByUser(String userName) {
		return getHibernateTemplate().find("select a from PauseReason a where a.user.username = '"+userName+"'");
	}
}
