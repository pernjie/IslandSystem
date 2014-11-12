/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dyihoon90
 */
@Entity
@Table(name = "MATERIAL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Material.findAll", query = "SELECT m FROM Material m"),
    @NamedQuery(name = "Material.findById", query = "SELECT m FROM Material m WHERE m.id = :id"),
    @NamedQuery(name = "Material.findByName", query = "SELECT m FROM Material m WHERE m.name = :name"),
    @NamedQuery(name = "Material.findByGenCategory", query = "SELECT m FROM Material m WHERE m.genCategory = :genCategory"),
    @NamedQuery(name = "Material.findAllMaterial", query = "SELECT m FROM Material m WHERE NOT m.genCategory = 0"),
    @NamedQuery(name = "Material.findAllRawMat", query = "SELECT m FROM Material m WHERE m.genCategory = 0"),
    @NamedQuery(name = "Material.findByMatCategory", query = "SELECT m FROM Material m WHERE m.matCategory = :matCategory"),
    @NamedQuery(name = "Material.findAllFurniture", query = "SELECT m FROM Material m WHERE NOT m.genCategory = 0")})
public class Material extends Item implements Serializable {

    @Column(name = "GEN_CATEGORY")
    private Integer genCategory;
    @Column(name = "MAT_CATEGORY")
    private String matCategory;

    public Material() {
    }

    public String getMatCategory() {
        return matCategory;
    }

    public void setMatCategory(String matCategory) {
        this.matCategory = matCategory;
    }

    public Integer getGenCategory() {
        return genCategory;
    }

    public void setGenCategory(Integer genCategory) {
        this.genCategory = genCategory;
    }

}