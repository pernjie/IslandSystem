/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "shelftype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ShelfType.findAll", query = "SELECT s FROM ShelfType s"),
    @NamedQuery(name = "ShelfType.findById", query = "SELECT s FROM ShelfType s WHERE s.id = :id"),
    @NamedQuery(name = "ShelfType.findByName", query = "SELECT s FROM ShelfType s WHERE s.name = :name"),
    @NamedQuery(name = "ShelfType.findByNumSlot", query = "SELECT s FROM ShelfType s WHERE s.numSlot = :numSlot"),
    @NamedQuery(name = "ShelfType.findByLength", query = "SELECT s FROM ShelfType s WHERE s.length = :length"),
    @NamedQuery(name = "ShelfType.findByBreadth", query = "SELECT s FROM ShelfType s WHERE s.breadth = :breadth"),
    @NamedQuery(name = "ShelfType.findByHeight", query = "SELECT s FROM ShelfType s WHERE s.height = :height"),
    @NamedQuery(name = "ShelfType.findByWeightLimit", query = "SELECT s FROM ShelfType s WHERE s.weightLimit = :weightLimit")})
public class ShelfType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Size(max = 64)
    @Column(name = "name")
    private String name;
    @Column(name = "num_slot")
    private Integer numSlot;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "length")
    private Double length;
    @Column(name = "breadth")
    private Double breadth;
    @Column(name = "height")
    private Double height;
    @Column(name = "weight_limit")
    private Double weightLimit;

    public ShelfType() {
    }

    public ShelfType(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumSlot() {
        return numSlot;
    }

    public void setNumSlot(Integer numSlot) {
        this.numSlot = numSlot;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getBreadth() {
        return breadth;
    }

    public void setBreadth(Double breadth) {
        this.breadth = breadth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeightLimit() {
        return weightLimit;
    }

    public void setWeightLimit(Double weightLimit) {
        this.weightLimit = weightLimit;
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
        if (!(object instanceof ShelfType)) {
            return false;
        }
        ShelfType other = (ShelfType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Shelftype[ id=" + id + " ]";
    }
    
}
