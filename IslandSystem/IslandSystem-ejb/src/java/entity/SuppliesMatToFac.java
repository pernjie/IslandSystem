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
@Table(name = "SUPPLIESMATTOFAC")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SuppliesMatToFac.findAll", query = "SELECT s FROM SuppliesMatToFac s"),
    @NamedQuery(name = "SuppliesMatToFac.findById", query = "SELECT s FROM SuppliesMatToFac s WHERE s.id = :id"),
    @NamedQuery(name = "SuppliesMatToFac.findByLotSize", query = "SELECT s FROM SuppliesMatToFac s WHERE s.lotSize = :lotSize"),
    @NamedQuery(name = "SuppliesMatToFac.findByUnitPrice", query = "SELECT s FROM SuppliesMatToFac s WHERE s.unitPrice = :unitPrice"),
    @NamedQuery(name = "SuppliesMatToFac.findByLeadTime", query = "SELECT s FROM SuppliesMatToFac s WHERE s.leadTime = :leadTime")})
public class SuppliesMatToFac implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "LOT_SIZE")
    private int lotSize;
    @Column(name = "UNIT_PRICE")
    private double unitPrice;
    @Column(name = "LEAD_TIME")
    private int leadTime;
    @JoinColumn(name = "SUP", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Supplier sup;
    @JoinColumn(name = "MAT", referencedColumnName = "ID")
    @ManyToOne
    private Material mat;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;

    public SuppliesMatToFac() {
    }

    public SuppliesMatToFac(Long id) {
        this.id = id;
    }

    public SuppliesMatToFac(Long id, int lotSize, double unitPrice, int leadTime) {
        this.id = id;
        this.lotSize = lotSize;
        this.unitPrice = unitPrice;
        this.leadTime = leadTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLotSize() {
        return lotSize;
    }

    public void setLotSize(int lotSize) {
        this.lotSize = lotSize;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(int leadTime) {
        this.leadTime = leadTime;
    }

    public Supplier getSup() {
        return sup;
    }

    public void setSup(Supplier sup) {
        this.sup = sup;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
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
        if (!(object instanceof SuppliesMatToFac)) {
            return false;
        }
        SuppliesMatToFac other = (SuppliesMatToFac) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SuppliesMatToFac[ id=" + id + " ]";
    }

}
