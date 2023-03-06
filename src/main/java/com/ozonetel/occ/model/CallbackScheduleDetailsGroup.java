package com.ozonetel.occ.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author pavanj
 */
public class CallbackScheduleDetailsGroup {

    private int minHour;
    private int maxHour;
    private List<CallbackScheduleDetails> callbackScheduleDetailsList = new ArrayList<CallbackScheduleDetails>();

    public CallbackScheduleDetailsGroup(int minHour, int maxHour) {
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public int getMinHour() {
        return minHour;
    }

    public void setMinHour(int minHour) {
        this.minHour = minHour;
    }

    public int getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }

    public List<CallbackScheduleDetails> getCallbackScheduleDetailsList() {
        return callbackScheduleDetailsList;
    }

    public void setCallbackScheduleDetailsList(List<CallbackScheduleDetails> callbackScheduleDetailsList) {
        this.callbackScheduleDetailsList = callbackScheduleDetailsList;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.minHour;
        hash = 89 * hash + this.maxHour;
        hash = 89 * hash + Objects.hashCode(this.callbackScheduleDetailsList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CallbackScheduleDetailsGroup other = (CallbackScheduleDetailsGroup) obj;
        if (this.minHour != other.minHour) {
            return false;
        }
        if (this.maxHour != other.maxHour) {
            return false;
        }
        if (!Objects.equals(this.callbackScheduleDetailsList, other.callbackScheduleDetailsList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CallbackScheduleDetailsGroup{" + "MinHour=" + minHour + ", MaxHour=" + maxHour + ", callbackScheduleDetailsList=" + callbackScheduleDetailsList + '}';
    }

}
