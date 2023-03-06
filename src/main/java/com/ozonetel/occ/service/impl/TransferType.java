package com.ozonetel.occ.service.impl;

/**
 *
 * @author pavanj
 */
public enum TransferType {
    //
    // ----> Caution:Don't change the order.Using ordinal values. 

    OTHER, BLIND, CONSULTATIVE, CONSULTATIVE_HOLD, IVR;

    public String toReadableString() {
        StringBuilder sb = new StringBuilder(this.toString().toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(this.toString().charAt(0)));
        return sb.toString();
    }
}
