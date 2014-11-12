/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Owner
 */
@Entity
@Table(name = "FACILITY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Facility.findAll", query = "SELECT f FROM Facility f"),
    @NamedQuery(name = "Facility.findAllGhqs", query = "SELECT f FROM Facility f WHERE f.type = 'Global HQ'"),
    @NamedQuery(name = "Facility.findAllMfs", query = "SELECT f FROM Facility f WHERE f.type = 'Manufacturing'"),
    @NamedQuery(name = "Facility.findAllMfsStores", query = "SELECT f FROM Facility f WHERE f.type = 'Manufacturing' OR f.type = 'Store'"),
    @NamedQuery(name = "Facility.findAllStores", query = "SELECT f FROM Facility f WHERE f.type = 'Store'"),
    @NamedQuery(name = "Facility.findAllStore", query = "SELECT f FROM Facility f WHERE f.type = 'Store'"),
    @NamedQuery(name = "Facility.findAllOnlines", query = "SELECT f FROM Facility f WHERE f.type = 'Online'"),
    @NamedQuery(name = "Facility.findById", query = "SELECT f FROM Facility f WHERE f.id = :id"),
    @NamedQuery(name = "Facility.findByName", query = "SELECT f FROM Facility f WHERE f.name = :name"),
    @NamedQuery(name = "Facility.findByPostalCode", query = "SELECT f FROM Facility f WHERE f.postalCode = :postalCode"),
    @NamedQuery(name = "Facility.findByCity", query = "SELECT f FROM Facility f WHERE f.city = :city"),
    @NamedQuery(name = "Facility.findByCountry", query = "SELECT f FROM Facility f WHERE f.country = :country"),
    @NamedQuery(name = "Facility.findByType", query = "SELECT f FROM Facility f WHERE f.type = :type")})
public class Facility implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "POSTAL_CODE")
    private String postalCode;
    @Column(name = "CITY")
    private String city;
    @JoinColumn(name = "COUNTRY", referencedColumnName = "ID")
    @ManyToOne
    private Country country;
    @JoinColumn(name = "REGION", referencedColumnName = "ID")
    @ManyToOne
    private Region region;
    @Column(name = "TYPE")
    private String type;

    public Facility() {
    }

    public Facility(Long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(object instanceof Facility)) {
            return false;
        }
        Facility other = (Facility) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Facility[ id=" + id + " ]";
    }

}
