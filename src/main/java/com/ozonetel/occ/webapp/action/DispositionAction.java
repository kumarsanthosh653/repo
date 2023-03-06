package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.webapp.action.BaseAction;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class DispositionAction extends BaseAction implements Preparable {
    private DispositionManager dispositionManager;
    private List dispositions;
    private Disposition disposition;
    private Long  id;

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public List getDispositions() {
        return dispositions;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String dispositionId = getRequest().getParameter("disposition.id");
            if (dispositionId != null && !dispositionId.equals("")) {
                disposition = dispositionManager.get(new Long(dispositionId));
            }
        }
    }

    public String list() {
//        dispositions = dispositionManager.getAll();
        Map params =  new HashMap();
        User u  = userManager.getUserByUsername(getRequest().getRemoteUser());
        params.put("userId", u.getId());
        //Geting only Dispositions Which are Global
        dispositions = dispositionManager.findByNamedQuery("getGlobalDispositions", params);


        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    public String delete() {
        dispositionManager.remove(disposition.getId());
        HashMap params = new HashMap();
        params.put("username",getRequest().getRemoteUser());
        params.put("reason",disposition.getReason());
        List<Disposition> dispList = dispositionManager.findByNamedQuery("dispositionsByUserAndReason",params);
        for(Disposition disp: dispList){
           dispositionManager.remove(disp.getId());
        }
        saveMessage(getText("disposition.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            disposition = dispositionManager.get(id);
        } else {
            disposition = new Disposition();
        }

        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }

        boolean isNew = (disposition.getId() == null);
        if(isNew){
            disposition.setUser(userManager.getUserByUsername(getRequest().getRemoteUser()));
        }

        dispositionManager.save(disposition);

        String key = (isNew) ? "disposition.added" : "disposition.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}