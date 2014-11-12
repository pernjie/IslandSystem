/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "CHATRECORD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ChatRecord.findAll", query = "SELECT c FROM ChatRecord c"),
    @NamedQuery(name = "ChatRecord.findById", query = "SELECT c FROM ChatRecord c WHERE c.id = :id"),
    @NamedQuery(name = "ChatRecord.findByMsgTime", query = "SELECT c FROM ChatRecord c WHERE c.msgTime = :msgTime"),
    @NamedQuery(name = "ChatRecord.findByChannel", query = "SELECT c FROM ChatRecord c WHERE c.channel = :channel"),
    @NamedQuery(name = "ChatRecord.findByMessage", query = "SELECT c FROM ChatRecord c WHERE c.message = :message")})
public class ChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "MSG_TIME")
    private String msgTime;
    @Column(name = "CHANNEL")
    private String channel;
    @Column(name = "MESSAGE")
    private String message;
    @JoinColumn(name = "SENDER", referencedColumnName = "ID")
    @ManyToOne
    private Staff sender;
    @JoinColumn(name = "RECIPIENT", referencedColumnName = "ID")
    @ManyToOne
    private Staff recipient;

    public ChatRecord() {
    }

    public ChatRecord(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Staff getSender() {
        return sender;
    }

    public void setSender(Staff sender) {
        this.sender = sender;
    }

    public Staff getRecipient() {
        return recipient;
    }

    public void setRecipient(Staff recipient) {
        this.recipient = recipient;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChatRecord)) {
            return false;
        }
        ChatRecord other = (ChatRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ChatRecord[ id=" + id + " ]";
    }

}
