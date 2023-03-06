package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.model.Agent.State;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;
import org.hibernate.exception.ConstraintViolationException;

public class FwpNumberAction extends BaseAction implements Preparable {
    private FwpNumberManager fwpNumberManager;
    private List fwpNumbers;
    private FwpNumber fwpNumber;
    private Long  id;

    public void setFwpNumberManager(FwpNumberManager fwpNumberManager) {
        this.fwpNumberManager = fwpNumberManager;
    }

    public List getFwpNumbers() {
        return fwpNumbers;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String fwpNumberId = getRequest().getParameter("fwpNumber.id");
            if (fwpNumberId != null && !fwpNumberId.equals("")) {
                fwpNumber = fwpNumberManager.get(new Long(fwpNumberId));
            }
        }
    }

    public String list() {
        fwpNumbers = fwpNumberManager.getFwpNumbersByUser(getRequest().getRemoteUser());
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public FwpNumber getFwpNumber() {
        return fwpNumber;
    }

    public void setFwpNumber(FwpNumber fwpNumber) {
        this.fwpNumber = fwpNumber;
    }

    public String delete() {
        fwpNumberManager.remove(fwpNumber.getId());
        saveMessage(getText("fwpNumber.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            fwpNumber = fwpNumberManager.get(id);
        } else {
            fwpNumber = new FwpNumber();
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

        boolean isNew = (fwpNumber.getId() == null);
        fwpNumber.setUserId(userManager.getUserByUsername(getRequest().getRemoteUser()).getId());
        fwpNumber.setNextFlag(new Long(0));
        fwpNumber.setState(State.IDLE);
        String key = (isNew) ? "fwpNumber.added" : "fwpNumber.updated";
        try{
        fwpNumberManager.save(fwpNumber);
        saveMessage(getText(key));
        }catch(ConstraintViolationException e ){
            key = "fwpNumber.uniqueConstraint";
            saveErrors(getText(key));
        }catch(Exception e){
            key = "fwpNumber.uniqueConstraint";
            saveErrors(getText(key));
        }
        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}