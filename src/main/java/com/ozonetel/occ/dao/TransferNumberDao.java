package com.ozonetel.occ.dao;


import com.ozonetel.occ.model.TransferNumber;
import java.util.List;

/**
 * An interface that provides a data management interface to the TransferNumber table.
 */
public interface TransferNumberDao extends GenericDao<TransferNumber, Long> {
    
    List<TransferNumber> getTransferNumbersByUser(String username);
}