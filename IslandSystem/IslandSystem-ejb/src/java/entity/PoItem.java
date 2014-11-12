/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MY ASUS
 */
@Entity
@Table(name = "PoItem")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PoItem.findAll", query = "SELECT p FROM PoItem p"),
    @NamedQuery(name = "PoItem.findById", query = "SELECT p FROM PoItem p WHERE p.id = :id"),
    @NamedQuery(name = "PoItem.findByDeliveryDate", query = "SELECT p FROM PoItem p WHERE p.deliveryDate = :deliveryDate"),
    @NamedQuery(name = "PoItem.findByQuantity", query = "SELECT p FROM PoItem p WHERE p.quantity = :quantity"),
    @NamedQuery(name = "PoItem.findByTotalPrice", query = "SELECT p FROM PoItem p WHERE p.totalPrice = :totalPrice"),
    @NamedQuery(name = "PoItem.findByStatus", query = "SELECT p FROM PoItem p WHERE p.status = :status"),
    @NamedQuery(name = "PoItem.findByPo", query = "SELECT p FROM PoItem p WHERE p.po = :po"),
    @NamedQuery(name = "PoItem.findByProduct", query = "SELECT p FROM PoItem p WHERE p.item = :item")})
public class PoItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "delivery_date")
    @Temporal(TemporalType.DATE)
    private Date deliveryDate;
    @Column(name = "quantity")
    private Integer quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_price")
    private Double totalPrice;
    @Size(max = 50)
    @Column(name = "status")
    private String status;
    @Column(name = "remarks")
    private String remarks;
    @JoinColumn(name = "po", referencedColumnName = "id")
    @ManyToOne
    private PoRecord po;
    @JoinColumn(name = "prod", referencedColumnName = "id")
    @ManyToOne
    private Item item;

    public PoItem() {
    }

    public PoItem(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public PoRecord getPo() {
        return po;
    }

    public void setPo(PoRecord po) {
        this.po = po;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
        if (!(object instanceof PoItem)) {
            return false;
        }
        PoItem other = (PoItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Poproduct[ id=" + id + " ]";
    }
    
}
