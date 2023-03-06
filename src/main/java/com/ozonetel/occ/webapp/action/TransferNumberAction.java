package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;

public class TransferNumberAction extends BaseAction implements Preparable {
    private TransferNumberManager transferNumberManager;
    private List transferNumbers;
    private TransferNumber transferNumber;
    private Long  id;

    public void setTransferNumberManager(TransferNumberManager transferNumberManager) {
        this.transferNumberManager = transferNumberManager;
    }

    public List getTransferNumbers() {
        return transferNumbers;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String transferNumberId = getRequest().getParameter("transferNumber.id");
            if (transferNumberId != null && !transferNumberId.equals("")) {
                transferNumber = transferNumberManager.get(new Long(transferNumberId));
            }
        }
    }

    public String list() {
//        transferNumbers = transferNumberManager.getAll();
        transferNumbers = transferNumberManager.getTransferNumbersByUser(getRequest().getRemoteUser());
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public TransferNumber getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(TransferNumber transferNumber) {
        this.transferNumber = transferNumber;
    }

    public String delete() {
        transferNumberManager.remove(transferNumber.getId());
        saveMessage(getText("transferNumber.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            transferNumber = transferNumberManager.get(id);
        } else {
            transferNumber = new TransferNumber();
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

        boolean isNew = (transferNumber.getId() == null);
        
        if(isNew){
            transferNumber.setUser(userManager.getUserByUsername(getRequest().getRemoteUser()));
        }
        String key = (isNew) ? "transferNumber.added" : "transferNumber.updated";
        try{
        transferNumberManager.save(transferNumber);
        saveMessage(getText(key));
        }catch(Exception e){
            log.debug("TransferNumber Already Exists");
            key = "TransferNumber Already Exists !!!";
            saveErrors(key);
        }

        
        

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}