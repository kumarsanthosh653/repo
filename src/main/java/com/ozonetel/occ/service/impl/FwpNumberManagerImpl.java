package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.FwpNumberDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.FwpNumberManager;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;

@WebService(serviceName = "FwpNumberService", endpointInterface = "com.ozonetel.occ.service.FwpNumberManager")
public class FwpNumberManagerImpl extends GenericManagerImpl<FwpNumber, Long> implements FwpNumberManager {

    FwpNumberDao fwpNumberDao;

    public FwpNumberManagerImpl(FwpNumberDao fwpNumberDao) {
        super(fwpNumberDao);
        this.fwpNumberDao = fwpNumberDao;
    }

    public List<FwpNumber> getFwpNumbersByUser(String username) {
        return fwpNumberDao.getFwpNumbersByUser(username);
    }

    public List<FwpNumber> getFwpNumbersNotAssigend(String username) {
        return fwpNumberDao.getFwpNumbersNotAssigend(username);
    }

    public FwpNumber getFwpNumberByPhone(String phoneNumber, Long userId) {
        return fwpNumberDao.getFwpNumberByPhone(phoneNumber, userId);
    }

    public FwpNumber getFwpNumberByName(String phoneName, String username) {
        return fwpNumberDao.getFwpNumberByName(phoneName, username);
    }

    @Override
    public void resetFwpNumber(FwpNumber fwpNumber) {
        fwpNumber.setAgent(null);
        fwpNumber.setContact("");
        fwpNumber.setState(Agent.State.IDLE);
        fwpNumber.setNextFlag(new Long(0));
        save(fwpNumber);
    }

    @Override
    public void setUcidForFwp(Long montiorUcid, Long id) {
        fwpNumberDao.setUcidForFwp(montiorUcid, id);
    }

    public boolean makeFwpBusy(Long id, String contact, boolean isInCalling) {
        log.debug("Making Fwp busy fwpId " + id + " contact : " + contact + " isInCalling : " + isInCalling);
        return fwpNumberDao.makeFwpBusy(id, contact, isInCalling);
    }

    public String syncFwpToLdb(Long fwpId, Long userId) {
        log.debug("Syncing FWP to local DB | ID : " + fwpId);
        Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
        queryParams.put("userId", userId);
        queryParams.put("fwpId", fwpId);
        this.executeProcedure("{Call Sync_UsersFWPNumber(?,?)}", queryParams);
        return "SUCCESS";
    }

    @Override
    public String syncFwpsToLdb(String fwpIdsCommaSeperated, Long userId) {
        log.debug("Syncing FWPs to local DB | ID : " + fwpIdsCommaSeperated);
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("fwpIds", fwpIdsCommaSeperated);
        this.executeProcedure("{Call Sync_UsersFWPNumber_V1(?,?)}", queryParams);
        return "SUCCESS";
    }

    public String releasePhoneNumberFromSystemMonitor(Long fwpId) {
        try {
            FwpNumber fwp = this.get(fwpId);
            fwp.setContact(null);
            fwp.setNextFlag(new Long(0));
            fwp.setState(Agent.State.IDLE);
            fwp.setCallExceptions(new Long(0));
            this.save(fwp);
            log.debug("Successfully Released the Phone Number from System Monitor " + fwp);
            return "SUCCESS";
        } catch (Exception e) {
            log.error("Exception Occured while Releasing Phone Number from System Monitor : " + fwpId, e);
//            log.debug("Exception Occured while releasing the Phone Number from System Monitor For FWP Id "+fwpId);
            return "ERROR";
        }
    }

    public StatusMessage delPhoneNumber(Long fwpId, Long userId) {
        this.log.debug((Object) ("Trying to delete the FWP Number from local DB with the ID " + fwpId));
        boolean delStatus = false;
        for (int i = 1; i < 4; ++i) {
            try {
                this.remove(fwpId);
                delStatus = true;
                log.debug("Successfully deleted the PhoneNumber with " + i + " try and the id is " + fwpId);
                break;
            } catch (IllegalArgumentException e) {
                log.debug("Supplied Phone number with id " + fwpId + " not synced to Local DB, hence not deleting");
                return new StatusMessage(Status.SUCCESS, "Phone number not synced to Local DB");
            } catch (Exception e) {
                log.error("Exception Occured While Deleting the PhoneNumber : " + fwpId, e);
            }
        }
        if (delStatus) {
            return new StatusMessage(Status.SUCCESS, "Phone number Deleted Successfully");
        }
        return new StatusMessage(Status.ERROR, "Not deleted the PhoneNo");
    }

    public String syncFwpsForUser(Long userId) {
        log.debug("Syncing all fwps for user : " + userId);
        Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
        queryParams.put("userId", userId);
        this.executeProcedure("{Call Refsh_UsersFWPNumber(?)}", queryParams);
        return "SUCCESS";

    }

    public List<Map<String, Object>> getFwpNumberStatesforMonitor(Long userId) {
        return fwpNumberDao.getFwpNumberStatesforMonitor(userId);
    }

}
