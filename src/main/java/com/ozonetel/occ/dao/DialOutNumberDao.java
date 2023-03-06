package com.ozonetel.occ.dao;

import com.ozonetel.occ.dao.GenericDao;

import com.ozonetel.occ.model.DialOutNumber;
import java.util.List;

/**
 * An interface that provides a data management interface to the DialOutNumber table.
 */
public interface DialOutNumberDao extends GenericDao<DialOutNumber, Long> {
    public List<DialOutNumber> getDialOutNumbersByUser(String userName);

     public List getDialOutNumberByUserAndDon(String don ,String username);

}