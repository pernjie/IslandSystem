/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Item;
import entity.Material;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import session.stateless.InventoryBean;

/**
 *
 * @author Anna
 */
@ManagedBean(name="furnAutoManagerBean")
@ApplicationScoped
public class FurnAutoManagerBean implements Serializable {
    private List<Item> rawMats;
    
    @EJB
    InventoryBean inventoryBean;
    
    @PostConstruct
    public void init(){
        rawMats = new ArrayList<Item>();
        rawMats = inventoryBean.getRawMats();
    }
    
    public List<Item> getRawMats(){
        return rawMats;
    }

    public InventoryBean getInventoryBean() {
        return inventoryBean;
    }

    public void setInventoryBean(InventoryBean inventoryBean) {
        this.inventoryBean = inventoryBean;
    }
    
    
}
