package com.ozonetel.occ.service;

import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.model.DialNumber;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DialNumberManager extends GenericManager<DialNumber, Long> {
    
}