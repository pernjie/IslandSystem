/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import entity.Product;
import java.util.List;

/**
 *
 * @author Owner
 */
public class ProductClass {
    private Product prod;
    private List<StoreClass> stores;
    // 1: Holt
    // 2: Winter
    private int forecastType;
    
    public ProductClass() {
        forecastType = 1;
    }

    public List<StoreClass> getStores() {
        return stores;
    }

    public void setStores(List<StoreClass> stores) {
        this.stores = stores;
    }

    public Product getProd() {
        return prod;
    }

    public void setProd(Product prod) {
        this.prod = prod;
    }

    public int getForecastType() {
        return forecastType;
    }

    public void setForecastType(int forecastType) {
        this.forecastType = forecastType;
    }
}
