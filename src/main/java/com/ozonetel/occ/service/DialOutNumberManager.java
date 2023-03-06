package com.ozonetel.occ.service;

import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.model.DialOutNumber;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DialOutNumberManager extends GenericManager<DialOutNumber, Long> {

    public List<DialOutNumber> getDialOutNumbersByUser(String userName);
     public DialOutNumber getDialOutNumberByUserAndDon(String don ,String username);
    
}