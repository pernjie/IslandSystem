/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "SetItem.findAll", query = "SELECT m FROM SetItem m")})
public class SetItem extends Item implements Serializable {
    
    public SetItem() {
    }
    
}
