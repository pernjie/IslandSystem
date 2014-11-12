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
 * @author dyihoon90
 */
@Entity
@Table(name = "REGIONSERVICEPRICE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegionServicePrice.findAll", query = "SELECT s FROM RegionServicePrice s"),
    @NamedQuery(name = "RegionServicePrice.findById", query = "SELECT s FROM RegionServicePrice s WHERE s.id = :id"),
    @NamedQuery(name = "RegionServicePrice.findByPrice", query = "SELECT s FROM RegionServicePrice s WHERE s.price = :price")})
public class RegionServicePrice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PRICE")
    private double price;
    @JoinColumn(name = "SERVICE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Service svc;
    @JoinColumn(name = "REGION", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Region region;

    public RegionServicePrice() {
    }

    public RegionServicePrice(Long id) {
        this.id = id;
    }

    public RegionServicePrice(Long id, double price) {
        this.id = id;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Service getSvc() {
        return svc;
    }

    public void setSvc(Service svc) {
        this.svc = svc;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
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
        if (!(object instanceof RegionServicePrice)) {
            return false;
        }
        RegionServicePrice other = (RegionServicePrice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RegionServicePrice[ id=" + id + " ]";
    }

}
