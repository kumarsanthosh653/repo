package com.ozonetel.occ.service.impl;

/**
 *
 * @author pavanj
 */
public enum Status {

    SUCCESS,
    ERROR,
    FAIL,
    EXCEPTION,
    QUEUED;

    /**
     * SUCCESS -> Success
     * @return 
     */
    public String toReadableString() {
        StringBuilder sb = new StringBuilder(this.toString().toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(this.toString().charAt(0)));
        return sb.toString();
    }
}
