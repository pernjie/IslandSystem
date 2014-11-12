/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.Facility;
import entity.Item;
import entity.Shelf;
import entity.ShelfSlot;
import enumerator.InvenLoc;
import java.io.Serializable;
import java.math.BigInteger;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nataliegoh
 */
@Entity
@Table(name = "InventoryMaterial")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InventoryMaterial.findAll", query = "SELECT i FROM InventoryMaterial i"),
    @NamedQuery(name = "InventoryMaterial.findById", query = "SELECT i FROM InventoryMaterial i WHERE i.id = :id"),
    @NamedQuery(name = "InventoryMaterial.findByFac", query = "SELECT i FROM InventoryMaterial i WHERE i.fac = :fac"),
    @NamedQuery(name = "InventoryMaterial.findByMat", query = "SELECT i FROM InventoryMaterial i WHERE i.mat = :mat"),
    @NamedQuery(name = "InventoryMaterial.findByZone", query = "SELECT i FROM InventoryMaterial i WHERE i.zone = :zone"),
    @NamedQuery(name = "InventoryMaterial.findByShelf", query = "SELECT i FROM InventoryMaterial i WHERE i.shelf = :shelf"),
    @NamedQuery(name = "InventoryMaterial.findByShelfSlot", query = "SELECT i FROM InventoryMaterial i WHERE i.shelfSlot = :shelfSlot"),
    @NamedQuery(name = "InventoryMaterial.findByQuantity", query = "SELECT i FROM InventoryMaterial i WHERE i.quantity = :quantity"),
    @NamedQuery(name = "InventoryMaterial.findByUppThreshold", query = "SELECT i FROM InventoryMaterial i WHERE i.uppThreshold = :uppThreshold"),
    @NamedQuery(name = "InventoryMaterial.findByLowThreshold", query = "SELECT i FROM InventoryMaterial i WHERE i.lowThreshold = :lowThreshold")})
public class InventoryMaterial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;
    @JoinColumn(name = "MAT", referencedColumnName = "ID")
    @ManyToOne
    private Item mat;
    @Size(max = 2)
    @Column(name = "zone")
    private String zone;
    @JoinColumn(name = "SHELF", referencedColumnName = "ID")
    @ManyToOne
    private Shelf shelf;
    @JoinColumn(name = "SHELFSLOT", referencedColumnName = "ID")
    @ManyToOne
    private ShelfSlot shelfSlot;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "uppThreshold")
    private Integer uppThreshold;
    @Column(name = "lowThreshold")
    private Integer lowThreshold;
    @Column(name="LOCATION")
    private InvenLoc location;
    @Column(name = "matLength")
    private Double matLength;
    @Column(name = "matBreadth")
    private Double matBreadth;
    @Column(name = "matHeight")
    private Double matHeight;    
    

    public InventoryMaterial() {
    }

    public InventoryMaterial(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
    }

    public Item getMat() {
        return mat;
    }

    public void setMat(Item mat) {
        this.mat = mat;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public ShelfSlot getShelfSlot() {
        return shelfSlot;
    }

    public void setShelfSlot(ShelfSlot shelfSlot) {
        this.shelfSlot = shelfSlot;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUppThreshold() {
        return uppThreshold;
    }

    public void setUppThreshold(Integer uppThreshold) {
        this.uppThreshold = uppThreshold;
    }

    public Integer getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(Integer lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public InvenLoc getLocation() {
        return location;
    }

    public void setLocation(InvenLoc location) {
        this.location = location;
    }

    public Double getMatLength() {
        return matLength;
    }

    public void setMatLength(Double matLength) {
        this.matLength = matLength;
    }

    public Double getMatBreadth() {
        return matBreadth;
    }

    public void setMatBreadth(Double matBreadth) {
        this.matBreadth = matBreadth;
    }

    public Double getMatHeight() {
        return matHeight;
    }

    public void setMatHeight(Double matHeight) {
        this.matHeight = matHeight;
    }


    
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InventoryMaterial)) {
            return false;
        }
        InventoryMaterial other = (InventoryMaterial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.InventoryMaterial[ id=" + id + " ]";
    }
    
}
