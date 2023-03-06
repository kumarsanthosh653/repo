package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.dao.FwpNumberDao;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FwpNumberDaoHibernate extends GenericDaoHibernate<FwpNumber, Long> implements FwpNumberDao {

    public FwpNumberDaoHibernate() {
        super(FwpNumber.class);
    }

    public List<FwpNumber> getFwpNumbersByUser(String username) {
        return (List<FwpNumber>) getHibernateTemplate().find("from FwpNumber f where f.user.username = '" + username + "'");

    }

    public List<FwpNumber> getFwpNumbersNotAssigend(String username) {
        return (List<FwpNumber>) getHibernateTemplate().find("from FwpNumber f where f.user.username = '" + username + "' and f.agent = null");

    }

    public FwpNumber getFwpNumberByPhone(String phoneNumber, Long userId) {
        List<FwpNumber> l = (List<FwpNumber>) getHibernateTemplate().find("from FwpNumber f where f.userId = '" + userId + "' and f.phoneNumber = '" + phoneNumber + "'");
        if (!l.isEmpty()) {
            return l.get(0);
        }
        return null;
    }

    public FwpNumber getFwpNumberByName(String phoneName, String username) {
        List<FwpNumber> l = (List<FwpNumber>) getHibernateTemplate().find("from FwpNumber f where f.user.username = ? and f.phoneName = ?", new Object[]{username, phoneName});
        if (!l.isEmpty()) {
            return l.get(0);
        }
        return null;
    }

    @Override
    public void setUcidForFwp(Long montiorUcid, Long id) {
        getHibernateTemplate().bulkUpdate("update FwpNumber f set f.ucid = ? where f.id = ?", montiorUcid, id);
    }

    @Override
    public boolean makeFwpBusy(Long id, String contact, boolean isInCalling) {
        return getHibernateTemplate().bulkUpdate(
                "update FwpNumber f set f.contact='" + contact + "', f.state=3, f.callExceptions=0, f.nextFlag=0 "
                + (isInCalling ? ",f.lastSelected=" + (System.currentTimeMillis() / 1000) * 1000 : "")
                + "  where f.id = " + id) > 0;
    }

    public List<Map<String, Object>> getFwpNumberStatesforMonitor(Long userId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user_id", userId);
        return executeSQLQuery("SELECT id as id, phone_name as name,user_id as userId,phone_number as phoneNumber,state as state,nextFlag as nextFlag,lastSelected as lastSelected,contact as contact,call_status as callStatus, ucid as ucid from fwp_numbers f where user_id=? order by state desc,nextFlag desc", params);
//            return getHibernateTemplate().find("select f.id as id, f.phoneName as name,f.userId as username,f.phoneNumber as phoneNumber,f.state as state,f.nextFlag as nextFlag,f.lastSelected as lastSelected,f.contact as contact,f.callStatus as callStatus, f.ucid as ucid from FwpNumber f where f.userId=? order by f.state desc,f.nextFlag desc", userId);
    }
    
//    public boolean releasePhoneNumberFromSystemMonitor(Long fwpId){
//        return true;
//    }

}
