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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MY ASUS
 */
@Entity
@Table(name = "poRecord")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PoRecord.findAll", query = "SELECT p FROM PoRecord p"),
    @NamedQuery(name = "PoRecord.findById", query = "SELECT p FROM PoRecord p WHERE p.id = :id"),
    @NamedQuery(name = "PoRecord.findByOrderDate", query = "SELECT p FROM PoRecord p WHERE p.orderDate = :orderDate"),
    @NamedQuery(name = "PoRecord.findByStatus", query = "SELECT p FROM PoRecord p WHERE p.status = :status"),
    @NamedQuery(name = "PoRcord.findByFac", query = "SELECT p FROM PoRecord p WHERE p.fac = :fac"),
    @NamedQuery(name = "PoRecord.findBySup", query = "SELECT p FROM PoRecord p WHERE p.sup = :sup")})
public class PoRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "order_date")
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_price")
    private Double totalPrice;
    @Size(max = 50)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;
    @JoinColumn(name = "SUP", referencedColumnName = "ID")
    @ManyToOne
    private Supplier sup;

    public PoRecord() {
    }

    public PoRecord(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
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

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
    }

    public Supplier getSup() {
        return sup;
    }

    public void setSup(Supplier sup) {
        this.sup = sup;
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
        if (!(object instanceof PoRecord)) {
            return false;
        }
        PoRecord other = (PoRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Porecord[ id=" + id + " ]";
    }
    
}
