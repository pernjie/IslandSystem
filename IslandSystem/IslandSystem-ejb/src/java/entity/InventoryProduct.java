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
import java.io.Serializable;
import java.math.BigInteger;
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
import enumerator.InvenLoc;
/**
 *
 * @author nataliegoh
 */
@Entity
@Table(name = "InventoryProduct")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InventoryProduct.findAll", query = "SELECT i FROM InventoryProduct i"),
    @NamedQuery(name = "InventoryProduct.findById", query = "SELECT i FROM InventoryProduct i WHERE i.id = :id"),
    @NamedQuery(name = "InventoryProduct.findByFac", query = "SELECT i FROM InventoryProduct i WHERE i.fac = :fac"),
    @NamedQuery(name = "InventoryProduct.findByProd", query = "SELECT i FROM InventoryProduct i WHERE i.prod = :prod"),
    @NamedQuery(name = "InventoryProduct.findByZone", query = "SELECT i FROM InventoryProduct i WHERE i.zone = :zone"),
    @NamedQuery(name = "InventoryProduct.findByShelf", query = "SELECT i FROM InventoryProduct i WHERE i.shelf = :shelf"),
    @NamedQuery(name = "InventoryProduct.findByShelfSlot", query = "SELECT i FROM InventoryProduct i WHERE i.shelfSlot = :shelfSlot"),
    @NamedQuery(name = "InventoryProduct.findByQuantity", query = "SELECT i FROM InventoryProduct i WHERE i.quantity = :quantity"),
    @NamedQuery(name = "InventoryProduct.findByUppThreshold", query = "SELECT i FROM InventoryProduct i WHERE i.uppThreshold = :uppThreshold"),
    @NamedQuery(name = "InventoryProduct.findByLowThreshold", query = "SELECT i FROM InventoryProduct i WHERE i.lowThreshold = :lowThreshold")})
public class InventoryProduct implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;
    @JoinColumn(name = "PROD", referencedColumnName = "ID")
    @ManyToOne
    private Item prod;
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
    @Column(name = "expiryDate")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    @Column(name="LOCATION")
    private InvenLoc location;
    @Column(name = "pdtLength")
    private Double pdtLength;
    @Column(name = "pdtBreadth")
    private Double pdtBreadth;
    @Column(name = "pdtHeight")
    private Double pdtHeight;  
    
    public InventoryProduct() {
    }

    public InventoryProduct(Long id) {
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

    public Item getProd() {
        return prod;
    }

    public void setProd(Item prod) {
        this.prod = prod;
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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public InvenLoc getLocation() {
        return location;
    }

    public void setLocation(InvenLoc location) {
        this.location = location;
    }

    public Double getPdtLength() {
        return pdtLength;
    }

    public void setPdtLength(Double pdtLength) {
        this.pdtLength = pdtLength;
    }

    public Double getPdtBreadth() {
        return pdtBreadth;
    }

    public void setPdtBreadth(Double pdtBreadth) {
        this.pdtBreadth = pdtBreadth;
    }

    public Double getPdtHeight() {
        return pdtHeight;
    }

    public void setPdtHeight(Double pdtHeight) {
        this.pdtHeight = pdtHeight;
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
        if (!(object instanceof InventoryProduct)) {
            return false;
        }
        InventoryProduct other = (InventoryProduct) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.InventoryProduct[ id=" + id + " ]";
    }
    
}
