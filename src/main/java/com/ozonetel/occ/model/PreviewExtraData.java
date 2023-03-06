/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import javax.persistence.*;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "Pre_Extra_Data")
@NamedQueries({
    @NamedQuery(name = "getCustomerData", query = "select d from PreviewExtraData d where d.previewData.id= :dataId")
})
public class PreviewExtraData extends BaseObject {

    private Long id;
    private PreviewData previewData;
    private String data;

    public PreviewExtraData() {
    }

    public PreviewExtraData(PreviewData previewData, String data) {
        this.previewData = previewData;
        this.data = data;
    }

    @Column(name = "data")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(targetEntity = PreviewData.class)
    @JoinColumn(name = "pid")
    public PreviewData getPreviewData() {
        return previewData;
    }

    public void setPreviewData(PreviewData previewData) {
        this.previewData = previewData;
    }

    @Override
    public String toString() {
        return "PreviewExtraData{" + "id=" + id + ", previewData=" + previewData + ", data=" + data + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PreviewExtraData other = (PreviewExtraData) obj;
        if (this.previewData != other.previewData && (this.previewData == null || !this.previewData.equals(other.previewData))) {
            return false;
        }
        if ((this.data == null) ? (other.data != null) : !this.data.equals(other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.previewData != null ? this.previewData.hashCode() : 0);
        hash = 37 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }
}
