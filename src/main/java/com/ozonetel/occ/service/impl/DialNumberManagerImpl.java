package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.DialNumberDao;
import com.ozonetel.occ.model.DialNumber;
import com.ozonetel.occ.service.DialNumberManager;
import com.ozonetel.occ.service.impl.GenericManagerImpl;

import java.util.List;
import javax.jws.WebService;

@WebService(serviceName = "DialNumberService", endpointInterface = "com.ozonetel.occ.service.DialNumberManager")
public class DialNumberManagerImpl extends GenericManagerImpl<DialNumber, Long> implements DialNumberManager {
    DialNumberDao dialNumberDao;

    public DialNumberManagerImpl(DialNumberDao dialNumberDao) {
        super(dialNumberDao);
        this.dialNumberDao = dialNumberDao;
    }
}