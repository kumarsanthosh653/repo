package com.ozonetel.occ.service;

import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.StatusMessage;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.springframework.transaction.annotation.Transactional;

@WebService
@Transactional("transactionManager2")
public interface FwpNumberManager extends GenericManager<FwpNumber, Long> {

    @Override
    public FwpNumber get(Long id);

    @Override
    public FwpNumber save(FwpNumber object);

    public List<FwpNumber> getFwpNumbersByUser(String username);

    public List<FwpNumber> getFwpNumbersNotAssigend(String username);

    public FwpNumber getFwpNumberByPhone(String phoneNumber, Long userId);

    public FwpNumber getFwpNumberByName(String phoneName, String username);

    /**
     * Resets FwpNumber. Sets agent null,contact empty, state to IDLE,nextFlag
     * to 0
     *
     *
     * @param fwpNumber
     */
    public void resetFwpNumber(FwpNumber fwpNumber);

    public void setUcidForFwp(Long montiorUcid, Long id);

    public boolean makeFwpBusy(Long id, String contact, boolean isInCalling);

    public String syncFwpToLdb(Long fwpId, Long userId);

    /**
     * Synch multiple FWPs
     * @param fwpIdsCommaSeperated
     * @param userId
     * @return 
     */
    public String syncFwpsToLdb(String fwpIdsCommaSeperated, Long userId);

    public String syncFwpsForUser(Long userId);

    public String releasePhoneNumberFromSystemMonitor(Long fwpId);

    public StatusMessage delPhoneNumber(Long FwpId, Long userId);

    public List<Map<String, Object>> getFwpNumberStatesforMonitor(Long userId);
}
