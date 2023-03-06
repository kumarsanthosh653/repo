package com.ozonetel.occ.service;

import com.ozonetel.occ.model.TransferNumber;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface TransferNumberManager extends GenericManager<TransferNumber, Long> {
    
    List<TransferNumber> getTransferNumbersByUser(String username);
    
}