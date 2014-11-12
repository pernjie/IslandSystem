/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nataliegoh
 */
@Entity
@Table(name = "STAFF")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Staff.findAll", query = "SELECT s FROM Staff s"),
    @NamedQuery(name = "Staff.findById", query = "SELECT s FROM Staff s WHERE s.id = :id"),
    @NamedQuery(name = "Staff.findByName", query = "SELECT s FROM Staff s WHERE s.name = :name"),
    @NamedQuery(name = "Staff.findByEmail", query = "SELECT s FROM Staff s WHERE s.email = :email"),
    @NamedQuery(name = "Staff.findByContact", query = "SELECT s FROM Staff s WHERE s.contact = :contact")})
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL")
    private String email;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "ROLE_1")
    private String role1;
    @Column(name = "ROLE_2")
    private String role2;
    @Column(name = "ROLE_3")
    private String role3;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;
    @OneToMany(cascade = {CascadeType.PERSIST})
    Collection<Log> log = new ArrayList<Log>();

    public Staff() {
    }

    public Staff(Long id) {
        this.id = id;
    }

    public Staff(String email, String name, String contact, String password, String role1, String role2, String role3) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.password = password;
        this.role1 = role1;
        this.role2 = role2;
        this.role3 = role3;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRole1() {
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
    }

    public String getRole2() {
        return role2;
    }

    public void setRole2(String role2) {
        if (role2 != null && role2.trim().length() > 0) {
            this.role2 = role2;
        }
    }

    public String getRole3() {
        return role3;
    }

    public void setRole3(String role3) {
        if (role3 != null && role3.trim().length() > 0) {
            this.role3 = role3;
        }
    }

    public Collection<Log> getLog() {
        return log;
    }

    public void setLog(Collection<Log> log) {
        this.log = log;
    }

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
    }

    public void create(String email, String name, String contact, String password, String role1, String role2, String role3, Facility fac) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.password = password;
        this.role1 = role1;
        this.role2 = role2;
        this.role3 = role3;
        this.fac = fac;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Staff)) {
            return false;
        }
        Staff other = (Staff) object;
        return (this.email != null || other.email == null) && (this.email == null || this.email.equals(other.email));
    }

    @Override
    public String toString() {
        return "ejb.StaffEntity[ id=" + email + " ]";
    }

}
