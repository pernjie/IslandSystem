/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author nataliegoh
 */
@Entity(name="Log")
public class Log implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "LOGDATE")
    private String logDate;
    @Column(name = "LOGDETAILS")
    private String logDetails;
    

    public Log() {
    }
    
    public String getLogDetails() {
        return logDetails;
    }

    public void setLogDetails(String log) {
        System.err.println("InSETLOG: "+log);
        logDetails = log;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void create(String email, String log){
        setEmail(email);
        setLogDate();
        System.err.println("LOGB4: "+log);
        setLogDetails(log);
        System.err.println("LOG: "+log);
   } 

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate() {
        java.util.Date D = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss");
        this.logDate = dateFormat.format(D);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "ejb.Log[ id=" + id + " ]";
    }
    
}
