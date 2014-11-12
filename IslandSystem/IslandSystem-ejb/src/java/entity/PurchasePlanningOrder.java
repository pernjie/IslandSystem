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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "PURCHASEPLANNINGORDER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PurchasePlanningOrder.findAll", query = "SELECT p FROM PurchasePlanningOrder p"),
    @NamedQuery(name = "PurchasePlanningOrder.findById", query = "SELECT p FROM PurchasePlanningOrder p WHERE p.id = :id"),
    @NamedQuery(name = "PurchasePlanningOrder.findByPeriod", query = "SELECT p FROM PurchasePlanningOrder p WHERE p.period = :period"),
    @NamedQuery(name = "PurchasePlanningOrder.findByYear", query = "SELECT p FROM PurchasePlanningOrder p WHERE p.year = :year"),
    @NamedQuery(name = "PurchasePlanningOrder.findByQuantity", query = "SELECT p FROM PurchasePlanningOrder p WHERE p.quantity = :quantity")})
public class PurchasePlanningOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PERIOD")
    private Integer period;
    @Column(name = "YEAR")
    private Integer year;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Size(max = 64)
    @Column(name = "STATUS")
    private String status;
    @JoinColumn(name = "PROD", referencedColumnName = "ID")
    @ManyToOne
    private Product prod;
    @JoinColumn(name = "STORE", referencedColumnName = "ID")
    @ManyToOne
    private Facility store;

    public PurchasePlanningOrder() {
    }

    public PurchasePlanningOrder(Long id) {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!(object instanceof PurchasePlanningOrder)) {
            return false;
        }
        PurchasePlanningOrder other = (PurchasePlanningOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PurchasePlanningOrder[ id=" + id + " ]";
    }

}
