/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pavanj
 */
public class CallbacksGrouped {

    private int sno;
    private int count;
    private int minTime;
    private int maxTime;
    private String minHour;
    private String maxHour;
    private List<String> callbackDetails = new ArrayList<>();

    public CallbacksGrouped() {
    }

    public CallbacksGrouped(int minTime, int maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getCallbackDetails() {
        return callbackDetails;
    }

    public void setCallbackDetails(List<String> callbackDetails) {
        this.callbackDetails = callbackDetails;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public String getMinHour() {
        return minHour;
    }

    public void setMinHour(String minHour) {
        this.minHour = minHour;
    }

    public String getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(String maxHour) {
        this.maxHour = maxHour;
    }

    @Override
    public String toString() {
        return "CallbacksGrouped{" + "sno=" + sno + ", count=" + count + ", minTime=" + minTime + ", maxTime=" + maxTime + ", callbackDetails=" + callbackDetails + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.minTime;
        hash = 23 * hash + this.maxTime;
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
        final CallbacksGrouped other = (CallbacksGrouped) obj;
        if (this.minTime != other.minTime) {
            return false;
        }
        if (this.maxTime != other.maxTime) {
            return false;
        }
        return true;
    }
}
