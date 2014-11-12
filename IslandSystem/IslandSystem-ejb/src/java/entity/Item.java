/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "ITEM")
@XmlRootElement

@XmlSeeAlso({
    Material.class,
    Product.class,
    MenuItem.class,
    SetItem.class,
    Ingredient.class
})

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT m FROM Item m"),
    @NamedQuery(name = "Item.findById", query = "SELECT m FROM Item m WHERE m.id = :id"),
    @NamedQuery(name = "Item.findByName", query = "SELECT m FROM Item m WHERE m.name = :name")})
@DiscriminatorColumn(name = "ITEMTYPE")
public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "LONGDESC")
    private String longDesc;
    @Column(name = "SHORTDESC")
    private String shortDesc;
    @Column(name = "LENGTH")
    private Double length;
    @Column(name = "BREADTH")
    private Double breadth;
    @Column(name = "HEIGHT")
    private Double height;
    @Column(name = "WEIGHT")
    private Double weight;
    @Column(name = "ITEMPERBOX")
    private Integer ItemPerBox;
    @Column(name = "ITEMTYPE")
    private String ItemType;

    public Item() {
    }

    public Item(Long id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public Integer getItemPerBox() {
        return ItemPerBox;
    }

    public void setItemPerBox(Integer ItemPerBox) {
        this.ItemPerBox = ItemPerBox;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
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

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String ItemType) {
        this.ItemType = ItemType;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
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
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Item[ id=" + id + " ]";
    }

}