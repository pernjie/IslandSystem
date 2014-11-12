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
@Table(name = "PURCHASEPLANNINGRECORD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PurchasePlanningRecord.findAll", query = "SELECT p FROM PurchasePlanningRecord p"),
    @NamedQuery(name = "PurchasePlanningRecord.findById", query = "SELECT p FROM PurchasePlanningRecord p WHERE p.id = :id"),
    @NamedQuery(name = "PurchasePlanningRecord.findByPeriod", query = "SELECT p FROM PurchasePlanningRecord p WHERE p.period = :period"),
    @NamedQuery(name = "PurchasePlanningRecord.findByYear", query = "SELECT p FROM PurchasePlanningRecord p WHERE p.year = :year")})
public class PurchasePlanningRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PERIOD")
    private Integer period;
    @Column(name = "YEAR")
    private Integer year;
    @Column(name = "QUANTITY_W1")
    private Integer quantityW1;
    @Column(name = "QUANTITY_W2")
    private Integer quantityW2;
    @Column(name = "QUANTITY_W3")
    private Integer quantityW3;
    @Column(name = "QUANTITY_W4")
    private Integer quantityW4;
    @Column(name = "QUANTITY_W5")
    private Integer quantityW5;
    @JoinColumn(name = "PROD", referencedColumnName = "ID")
    @ManyToOne
    private Product prod;
    @JoinColumn(name = "STORE", referencedColumnName = "ID")
    @ManyToOne
    private Facility store;

    public PurchasePlanningRecord() {
    }

    public PurchasePlanningRecord(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getQuantityW1() {
        return quantityW1;
    }

    public void setQuantityW1(Integer quantityW1) {
        this.quantityW1 = quantityW1;
    }

    public Integer getQuantityW2() {
        return quantityW2;
    }

    public void setQuantityW2(Integer quantityW2) {
        this.quantityW2 = quantityW2;
    }

    public Integer getQuantityW3() {
        return quantityW3;
    }

    public void setQuantityW3(Integer quantityW3) {
        this.quantityW3 = quantityW3;
    }

    public Integer getQuantityW4() {
        return quantityW4;
    }

    public void setQuantityW4(Integer quantityW4) {
        this.quantityW4 = quantityW4;
    }

    public Integer getQuantityW5() {
        return quantityW5;
    }

    public void setQuantityW5(Integer quantityW5) {
        this.quantityW5 = quantityW5;
    }


    public Product getProd() {
        return prod;
    }

    public void setProd(Product prod) {
        this.prod = prod;
    }

    public Facility getStore() {
        return store;
    }

    public void setStore(Facility store) {
        this.store = store;
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
        if (!(object instanceof PurchasePlanningRecord)) {
            return false;
        }
        PurchasePlanningRecord other = (PurchasePlanningRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PurchasePlanningRecord[ id=" + id + " ]";
    }

}
