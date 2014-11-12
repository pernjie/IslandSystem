/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Anna
 */
@Entity
@Table(name = "shelfSlot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ShelfSlot.findAll", query = "SELECT s FROM ShelfSlot s"),
    @NamedQuery(name = "ShelfSlot.findById", query = "SELECT s FROM ShelfSlot s WHERE s.id = :id"),
    @NamedQuery(name = "ShelfSlot.findByShelf", query = "SELECT s FROM ShelfSlot s WHERE s.shelf = :shelf"),
    @NamedQuery(name = "ShelfSlot.findByPosition", query = "SELECT s FROM ShelfSlot s WHERE s.position = :position"),
    @NamedQuery(name = "ShelfSlot.findByOccupied", query = "SELECT s FROM ShelfSlot s WHERE s.occupied = :occupied"),
    @NamedQuery(name = "ShelfSlot.getOccupied", query = "SELECT s FROM ShelfSlot s WHERE s.shelf = :shelf ORDER BY s.position"),
    @NamedQuery(name = "ShelfSlot.findSlot", query = "SELECT s FROM ShelfSlot s WHERE s.position = :position AND s.shelf = :shelf")})
  
public class ShelfSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "SHELF", referencedColumnName = "ID")
    @ManyToOne
    private Shelf shelf;
    @Column(name = "position")
    private Integer position;
    @Column(name = "occupied")
    private Boolean occupied;

    public ShelfSlot() {
    }

    public ShelfSlot(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getOccupied() {
        return occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
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
        if (!(object instanceof ShelfSlot)) {
            return false;
        }
        ShelfSlot other = (ShelfSlot) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Shelfslot[ id=" + id + " ]";
    }
    
}
