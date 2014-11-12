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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author user
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Ingredient.findAll", query = "SELECT p FROM Ingredient p"),
    @NamedQuery(name = "Ingredient.findById", query = "SELECT p FROM Ingredient p WHERE p.id = :id"),
    @NamedQuery(name = "Ingredient.findByName", query = "SELECT p FROM Ingredient p WHERE p.name = :name")})
public class Ingredient extends Item implements Serializable {

    @Column(name = "SHELF_LIFE_IN_DAYS")
    private Integer shelfLifeInDays;

    public Ingredient() {
    }

    public Integer getShelfLifeInDays() {
        return shelfLifeInDays;
    }

    public void setShelfLifeInDays(Integer shelfLifeInDays) {
        this.shelfLifeInDays = shelfLifeInDays;
    }

}
