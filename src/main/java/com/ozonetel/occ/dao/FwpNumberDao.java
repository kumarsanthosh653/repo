package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.FwpNumber;
import java.util.List;
import java.util.Map;

/**
 * An interface that provides a data management interface to the FwpNumber
 * table.
 */
public interface FwpNumberDao extends GenericDao<FwpNumber, Long> {

    List<FwpNumber> getFwpNumbersByUser(String username);

    List<FwpNumber> getFwpNumbersNotAssigend(String username);

    public FwpNumber getFwpNumberByPhone(String phoneNumber, Long userId);

    public FwpNumber getFwpNumberByName(String phoneName, String username);

    public void setUcidForFwp(Long montiorUcid, Long id);

    public boolean makeFwpBusy(Long id, String contact, boolean isInCalling);

    public List<Map<String, Object>> getFwpNumberStatesforMonitor(Long userId);
}
