package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.model.DialOutNumber;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;

public class DialOutNumberAction extends BaseAction implements Preparable {
    private DialOutNumberManager dialOutNumberManager;
    private List dialOutNumbers;
    private DialOutNumber dialOutNumber;
    private Long  id;

    public void setDialOutNumberManager(DialOutNumberManager dialOutNumberManager) {
        this.dialOutNumberManager = dialOutNumberManager;
    }

    public List getDialOutNumbers() {
        return dialOutNumbers;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String dialOutNumberId = getRequest().getParameter("dialOutNumber.id");
            if (dialOutNumberId != null && !dialOutNumberId.equals("")) {
                dialOutNumber = dialOutNumberManager.get(new Long(dialOutNumberId));
            }
        }
    }

    public String list() {
//        dialOutNumbers = dialOutNumberManager.getAll();
        dialOutNumbers = dialOutNumberManager.getDialOutNumbersByUser(getRequest().getRemoteUser());
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public DialOutNumber getDialOutNumber() {
        return dialOutNumber;
    }

    public void setDialOutNumber(DialOutNumber dialOutNumber) {
        this.dialOutNumber = dialOutNumber;
    }

    public String delete() {
        dialOutNumberManager.remove(dialOutNumber.getId());
        saveMessage(getText("dialOutNumber.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            dialOutNumber = dialOutNumberManager.get(id);
        } else {
            dialOutNumber = new DialOutNumber();
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


        boolean isNew = (dialOutNumber.getId() == null);

        if(isNew){
            dialOutNumber.setUser(userManager.getUserByUsername(getRequest().getRemoteUser()));
            
        }

        dialOutNumberManager.save(dialOutNumber);

        String key = (isNew) ? "dialOutNumber.added" : "dialOutNumber.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}