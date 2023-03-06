package com.ozonetel.occ.service.impl;

/**
 * Defines possible types of participants in conference/transfer.
 *
 * @author pavanj
 */
public enum Participant {

    //
    // ----> Caution:Don't change the order.Using ordinal values.
    OTHER, AGENT, SKILL, PHONE;

    public String toReadableString() {
        StringBuilder sb = new StringBuilder(this.toString().toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(this.toString().charAt(0)));
        return sb.toString();
    }
}
