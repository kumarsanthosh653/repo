package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.dao.TransferNumberDao;
import java.util.List;

public class TransferNumberDaoHibernate extends GenericDaoHibernate<TransferNumber, Long> implements TransferNumberDao {

    public TransferNumberDaoHibernate() {
        super(TransferNumber.class);
    }
    
    
    @Override
    public List<TransferNumber> getTransferNumbersByUser(String username){
        return getHibernateTemplate().find("select new TransferNumber(a.id,a.transferName,a.transferNumber,a.sip) from TransferNumber a where a.user.username='"+username+"' order by a.transferName");
    }
}
