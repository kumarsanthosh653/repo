package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.TransferNumberDao;
import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.service.TransferNumberManager;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "TransferNumberService", endpointInterface = "com.ozonetel.occ.service.TransferNumberManager")
public class TransferNumberManagerImpl extends GenericManagerImpl<TransferNumber, Long> implements TransferNumberManager {
    TransferNumberDao transferNumberDao;

    public TransferNumberManagerImpl(TransferNumberDao transferNumberDao) {
        super(transferNumberDao);
        this.transferNumberDao = transferNumberDao;
    }
    
    public List<TransferNumber> getTransferNumbersByUser(String username){
        return transferNumberDao.getTransferNumbersByUser(username);
    }
}