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
import org.apache.commons.math3.stat.descriptive.summary.Product;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "PRODUCTPRICE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductPrice.findAll", query = "SELECT s FROM ProductPrice s"),
    @NamedQuery(name = "ProductPrice.findById", query = "SELECT s FROM ProductPrice s WHERE s.id = :id"),
    @NamedQuery(name = "ProductPrice.findByPrice", query = "SELECT s FROM ProductPrice s WHERE s.price = :price")})
public class ProductPrice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRICE")
    private Double price;
    @JoinColumn(name = "PROD", referencedColumnName = "ID")
    @ManyToOne
    private Item prod;
    @JoinColumn(name = "STORE", referencedColumnName = "ID")
    @ManyToOne
    private Facility store;

    public ProductPrice() {
    }

    public ProductPrice(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Item getProd() {
        return prod;
    }

    public void setProd(Item prod) {
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
        if (!(object instanceof ProductPrice)) {
            return false;
        }
        ProductPrice other = (ProductPrice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProductPrice[ id=" + id + " ]";
    }

}
