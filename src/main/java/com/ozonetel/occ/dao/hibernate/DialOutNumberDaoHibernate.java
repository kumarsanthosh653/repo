package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.DialOutNumber;
import com.ozonetel.occ.dao.DialOutNumberDao;
import com.ozonetel.occ.dao.hibernate.GenericDaoHibernate;
import java.util.List;

public class DialOutNumberDaoHibernate extends GenericDaoHibernate<DialOutNumber, Long> implements DialOutNumberDao {

    public DialOutNumberDaoHibernate() {
        super(DialOutNumber.class);
    }

    public List<DialOutNumber> getDialOutNumbersByUser(String userName){
        return getHibernateTemplate().find("select a from DialOutNumber a where a.user.username = '"+userName+"'");
    };

    public List getDialOutNumberByUserAndDon(String don ,String username){
        return getHibernateTemplate().find("select distinct s from DialOutNumber s where s.dialOutNumber='"+don+"' and s.user.username = '"+username+"'");
    }
}
