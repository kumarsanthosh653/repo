package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "Chat_Logs")
public class ChatLog extends BaseObject implements Serializable {

    private Long monitorUcid;
    private String chat;

    public ChatLog() {
    }

    public ChatLog(Long monitorUcid, String chat) {
        this.monitorUcid = monitorUcid;
        this.chat = chat;
    }

    @Id
    @Column(name = "Monitor_UCID")
    public Long getMonitorUcid() {
        return monitorUcid;
    }

    public void setMonitorUcid(Long monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    @Column(name = "Chat")
    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return "ChatLog{" + "monitorUcid=" + monitorUcid + ", chat=" + chat + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.monitorUcid);
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
        final ChatLog other = (ChatLog) obj;
        if (!Objects.equals(this.monitorUcid, other.monitorUcid)) {
            return false;
        }
        return true;
    }

}
