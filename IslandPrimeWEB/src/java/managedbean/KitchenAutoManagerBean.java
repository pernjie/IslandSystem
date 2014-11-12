/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Item;
import entity.Product;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import session.stateless.InventoryBean;
import session.stateless.KitchenBean;

/**
 *
 * @author Anna
 */
@ManagedBean(name="productAutoManagerBean")
@ApplicationScoped
public class KitchenAutoManagerBean implements Serializable {
    private List<Item> ingredients;
    
    @EJB
    KitchenBean kb;
    
    @PostConstruct
    public void init(){
        ingredients = new ArrayList<Item>();
        ingredients = kb.getAllIngr();
    }
    
    public List<Item> getProducts(){
        return ingredients;
    }

    public KitchenBean getProductBean() {
        return kb;
    }

    public void setProductBean(KitchenBean productBean) {
        this.kb = productBean;
    }
    
    
}
