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

/**
 *
 * @author Anna
 */
@ManagedBean(name="productAutoManagerBean")
@ApplicationScoped
public class ProductAutoManagerBean implements Serializable {
    private List<Item> products;
    
    @EJB
    InventoryBean ib;
    
    @PostConstruct
    public void init(){
        products = new ArrayList<Item>();
        products = ib.getAllProducts();
    }
    
    public List<Item> getProducts(){
        return products;
    }

    public InventoryBean getProductBean() {
        return ib;
    }

    public void setProductBean(InventoryBean productBean) {
        this.ib = productBean;
    }
    
    
}
