/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import entity.Product;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pern
 */
public class MatWeekProd {
    private Product prod;
    private List<StoreClassWeekly> stores;
    
    public MatWeekProd() {
        stores = new ArrayList<>();
    }

    public Product getProd() {
        return prod;
    }

    public void setProd(Product prod) {
        this.prod = prod;
    }

    public List<StoreClassWeekly> getStores() {
        return stores;
    }

    public void setStores(List<StoreClassWeekly> stores) {
        this.stores = stores;
    }
}
