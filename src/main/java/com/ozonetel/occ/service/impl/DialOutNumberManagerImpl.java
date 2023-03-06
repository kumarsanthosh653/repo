package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.DialOutNumberDao;
import com.ozonetel.occ.model.DialOutNumber;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.service.impl.GenericManagerImpl;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "DialOutNumberService", endpointInterface = "com.ozonetel.occ.service.DialOutNumberManager")
public class DialOutNumberManagerImpl extends GenericManagerImpl<DialOutNumber, Long> implements DialOutNumberManager {
    DialOutNumberDao dialOutNumberDao;

    public DialOutNumberManagerImpl(DialOutNumberDao dialOutNumberDao) {
        super(dialOutNumberDao);
        this.dialOutNumberDao = dialOutNumberDao;
    }

    public List<DialOutNumber> getDialOutNumbersByUser(String userName){
        return dialOutNumberDao.getDialOutNumbersByUser(userName);
    }

    public DialOutNumber getDialOutNumberByUserAndDon(String don ,String username){
       List<DialOutNumber> donList = dialOutNumberDao.getDialOutNumberByUserAndDon(don, username);
    if(!donList.isEmpty()){
        return donList.get(0);
    }else{
           return null;
    }
   }
}