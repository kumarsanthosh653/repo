package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.DialNumber;
import com.ozonetel.occ.dao.DialNumberDao;
import com.ozonetel.occ.dao.hibernate.GenericDaoHibernate;

public class DialNumberDaoHibernate extends GenericDaoHibernate<DialNumber, Long> implements DialNumberDao {

    public DialNumberDaoHibernate() {
        super(DialNumber.class);
    }
}
