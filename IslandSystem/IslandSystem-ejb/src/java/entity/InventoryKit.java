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
@Table(name = "InventoryKit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InventoryKit.findAll", query = "SELECT i FROM InventoryKit i"),
    @NamedQuery(name = "InventoryKit.findById", query = "SELECT i FROM InventoryKit i WHERE i.id = :id"),
    @NamedQuery(name = "InventoryKit.findByFac", query = "SELECT i FROM InventoryKit i WHERE i.fac = :fac"),
    @NamedQuery(name = "InventoryKit.findByIngr", query = "SELECT i FROM InventoryKit i WHERE i.ingr = :ingr"),
    @NamedQuery(name = "InventoryKit.findByZone", query = "SELECT i FROM InventoryKit i WHERE i.zone = :zone"),
    @NamedQuery(name = "InventoryKit.findByShelf", query = "SELECT i FROM InventoryKit i WHERE i.shelf = :shelf"),
    @NamedQuery(name = "InventoryKit.findByShelfSlot", query = "SELECT i FROM InventoryKit i WHERE i.shelfSlot = :shelfSlot"),
    @NamedQuery(name = "InventoryKit.findByQuantity", query = "SELECT i FROM InventoryKit i WHERE i.quantity = :quantity"),
    @NamedQuery(name = "InventoryKit.findByUppThreshold", query = "SELECT i FROM InventoryKit i WHERE i.uppThreshold = :uppThreshold"),
    @NamedQuery(name = "InventoryKit.findByLowThreshold", query = "SELECT i FROM InventoryKit i WHERE i.lowThreshold = :lowThreshold")})
public class InventoryKit implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "FAC", referencedColumnName = "ID")
    @ManyToOne
    private Facility fac;
    @JoinColumn(name = "INGR", referencedColumnName = "ID")
    @ManyToOne
    private Item ingr;
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
    @Column(name = "ingLength")
    private Double ingLength;
    @Column(name = "ingBreadth")
    private Double ingBreadth;
    @Column(name = "ingHeight")
    private Double ingHeight;  
    
    public InventoryKit() {
    }

    public InventoryKit(Long id) {
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

    public Item getIngr() {
        return ingr;
    }

    public void setIngr(Item ingr) {
        this.ingr = ingr;
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

    public Double getIngLength() {
        return ingLength;
    }

    public void setIngLength(Double ingLength) {
        this.ingLength = ingLength;
    }

    public Double getIngBreadth() {
        return ingBreadth;
    }

    public void setIngBreadth(Double ingBreadth) {
        this.ingBreadth = ingBreadth;
    }

    public Double getIngHeight() {
        return ingHeight;
    }

    public void setIngHeight(Double ingHeight) {
        this.ingHeight = ingHeight;
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
        if (!(object instanceof InventoryKit)) {
            return false;
        }
        InventoryKit other = (InventoryKit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.InventoryKit[ id=" + id + " ]";
    }
    
}
