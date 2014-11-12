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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "REGIONITEMPRICE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegionItemPrice.findAll", query = "SELECT s FROM RegionItemPrice s"),
    @NamedQuery(name = "RegionItemPrice.findById", query = "SELECT s FROM RegionItemPrice s WHERE s.id = :id"),
    @NamedQuery(name = "RegionItemPrice.findByPrice", query = "SELECT s FROM RegionItemPrice s WHERE s.price = :price"),
    @NamedQuery(name = "RegionItemPrice.findByRegionItem", query = "SELECT s FROM RegionItemPrice s WHERE s.region= :region AND s.item = :item")})
public class RegionItemPrice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRICE")
    private Double price;
    @JoinColumn(name = "ITEM", referencedColumnName = "ID")
    @ManyToOne
    private Item item;
    @JoinColumn(name = "REGION", referencedColumnName = "ID")
    @ManyToOne
    private Region region;

    public RegionItemPrice() {
    }

    public RegionItemPrice(Long id) {
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
        if (!(object instanceof RegionItemPrice)) {
            return false;
        }
        RegionItemPrice other = (RegionItemPrice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RegionItemPrice[ id=" + id + " ]";
    }

}
