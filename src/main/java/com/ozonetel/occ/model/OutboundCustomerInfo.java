package com.ozonetel.occ.model;

/**
 *
 * @author pavanj
 */
public class OutboundCustomerInfo {

    private String columnNames;
    private String rawData;

    public OutboundCustomerInfo() {
    }

    public OutboundCustomerInfo(String columnNames, String rawData) {
        this.columnNames = columnNames;
        this.rawData = rawData;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    @Override
    public String toString() {
        return "CustomerInfo{" + "columnNames=" + columnNames + ", rawData=" + rawData + '}';
    }

}
