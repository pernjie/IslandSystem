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
@Table(name = "TRANSACTIONITEM")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransactionItem.findAll", query = "SELECT t FROM TransactionItem t"),
    @NamedQuery(name = "TransactionItem.findById", query = "SELECT t FROM TransactionItem t WHERE t.id = :id"),
    @NamedQuery(name = "TransactionItem.findByQuantity", query = "SELECT t FROM TransactionItem t WHERE t.quantity = :quantity"),
    @NamedQuery(name = "TransactionItem.findByTransact", query = "SELECT t FROM TransactionItem t WHERE t.transact = :transact"),
    @NamedQuery(name = "TransactionItem.findByItem", query = "SELECT t FROM TransactionItem t WHERE t.item = :item"),
    @NamedQuery(name = "TransactionItem.findByPrice", query = "SELECT t FROM TransactionItem t WHERE t.price = :price")})
public class TransactionItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     
    @Column(name = "ID")
    private Long id;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Double price;
    @JoinColumn(name = "TRANSACT", referencedColumnName = "ID")
    @ManyToOne
    private TransactionRecord transact;
    @JoinColumn(name = "ITEM", referencedColumnName = "ID")
    @ManyToOne
    private Item item;
    @Column(name = "RETURNED_QTY")
    private Integer returnedQty;

    public TransactionItem() {
    }

    public TransactionItem(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getReturnedQty() {
        return returnedQty;
    }

    public void setReturnedQty(Integer returnedQty) {
        this.returnedQty = returnedQty;
    }

    public TransactionRecord getTransact() {
        return transact;
    }

    public void setTransact(TransactionRecord transact) {
        this.transact = transact;
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
        if (!(object instanceof TransactionItem)) {
            return false;
        }
        TransactionItem other = (TransactionItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TransactionItem[ id=" + id + " ]";
    }
    
}
