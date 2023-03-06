/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author root
 */
@Entity
@Table(name = "Transfer_Numbers")
public class TransferNumber extends BaseObject {

    private Long id;
    private String transferName;
    private String transferNumber;
    private boolean sip;
    private User user;

    public TransferNumber() {
    }

    public TransferNumber(Long id, String transferName, String transferNumber, boolean sip) {
        this.id = id;
        this.transferName = transferName;
        this.transferNumber = transferNumber;
        this.sip = sip;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    @Column(name = "is_SIP")
    public boolean isSip() {
        return sip;
    }

    public void setSip(boolean sip) {
        this.sip = sip;
    }

    /**
     * @return the transferName
     */
    @Column(name = "transfer_name", insertable = true)
    public String getTransferName() {
        return transferName;
    }

    /**
     * @return the transferNumber
     */
    @Column(name = "transfer_number", insertable = true, unique = true)
    public String getTransferNumber() {
        return transferNumber;
    }

    @Override
    public String toString() {
        return this.transferName + "~" + this.transferNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferNumber)) {
            return false;
        }
        final TransferNumber transferNumber = (TransferNumber) o;

        return !(transferNumber != null ? !transferNumber.equals(transferNumber.getTransferNumber()) : transferNumber.transferNumber != null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param transferName the transferName to set
     */
    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    /**
     * @param transferNumber the transferNumber to set
     */
    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    /**
     * @return the user
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

}
