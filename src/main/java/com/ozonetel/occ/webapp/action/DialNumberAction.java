package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.DialNumberManager;
import com.ozonetel.occ.model.DialNumber;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;

public class DialNumberAction extends BaseAction implements Preparable {
    private DialNumberManager dialNumberManager;
    private List dialNumbers;
    private DialNumber dialNumber;
    private Long  id;

    public void setDialNumberManager(DialNumberManager dialNumberManager) {
        this.dialNumberManager = dialNumberManager;
    }

    public List getDialNumbers() {
        return dialNumbers;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String dialNumberId = getRequest().getParameter("dialNumber.id");
            if (dialNumberId != null && !dialNumberId.equals("")) {
                dialNumber = dialNumberManager.get(new Long(dialNumberId));
            }
        }
    }

    public String list() {
        dialNumbers = dialNumberManager.getAll();
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public DialNumber getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(DialNumber dialNumber) {
        this.dialNumber = dialNumber;
    }

    public String delete() {
        dialNumberManager.remove(dialNumber.getId());
        saveMessage(getText("dialNumber.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            dialNumber = dialNumberManager.get(id);
        } else {
            dialNumber = new DialNumber();
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

        boolean isNew = (dialNumber.getId() == null);

        dialNumberManager.save(dialNumber);

        String key = (isNew) ? "dialNumber.added" : "dialNumber.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}