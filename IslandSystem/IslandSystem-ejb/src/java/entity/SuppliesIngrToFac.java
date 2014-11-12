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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "SUPPLIESINGRTOFAC")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SuppliesIngrToFac.findAll", query = "SELECT s FROM SuppliesIngrToFac s"),
    @NamedQuery(name = "SuppliesIngrToFac.findById", query = "SELECT s FROM SuppliesIngrToFac s WHERE s.id = :id"),
    @NamedQuery(name = "SuppliesIngrToFac.findByLotSize", query = "SELECT s FROM SuppliesIngrToFac s WHERE s.lotSize = :lotSize"),
    @NamedQuery(name = "SuppliesIngrToFac.findByUnitPrice", query = "SELECT s FROM SuppliesIngrToFac s WHERE s.unitPrice = :unitPrice"),
    @NamedQuery(name = "SuppliesIngrToFac.findByLeadTime", query = "SELECT s FROM SuppliesIngrToFac s WHERE s.leadTime = :leadTime")})
public class SuppliesIngrToFac implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "LOT_SIZE")
    private Integer lotSize;
    @Column(name = "LOT_WEIGHT")
    private Double lotWeight;
    @Column(name = "UNIT_PRICE")
    private Double unitPrice;
    @Column(name = "LEAD_TIME")
    private Integer leadTime;
    @JoinColumn(name = "SUP", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Supplier sup;
    @JoinColumn(name = "INGREDIENT", referencedColumnName = "ID")
    @ManyToOne
    private Ingredient ingredient;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;

    public SuppliesIngrToFac() {
    }

    public SuppliesIngrToFac(Long id) {
        this.id = id;
    }

    public SuppliesIngrToFac(Long id, Integer lotSize, Double unitPrice, Integer leadTime) {
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

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public Supplier getSup() {
        return sup;
    }

    public void setSup(Supplier sup) {
        this.sup = sup;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
    }

    public Double getLotWeight() {
        return lotWeight;
    }

    public void setLotWeight(Double lotWeight) {
        this.lotWeight = lotWeight;
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
        if (!(object instanceof SuppliesIngrToFac)) {
            return false;
        }
        SuppliesIngrToFac other = (SuppliesIngrToFac) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SuppliesIngrToFac[ id=" + id + " ]";
    }

}
