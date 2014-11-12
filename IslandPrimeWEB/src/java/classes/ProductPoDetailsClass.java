/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import entity.Item;
import entity.Product;
import java.util.Date;

/**
 *
 * @author MY ASUS
 */
public class ProductPoDetailsClass {
    
    private Product item;
    private Integer matQty;
    private Double unitPrice;
    private Date deliveryDate;
    
    public ProductPoDetailsClass() {
        this.item = new Product();
        this.matQty = 0;
        this.unitPrice = 0.0;
        this.deliveryDate = new Date();
    }

    public ProductPoDetailsClass(Product item, Integer matQty, Double unitPrice, Date deliveryDate) {
        this.item = item;
        this.matQty = matQty;
        this.unitPrice = unitPrice;
        this.deliveryDate = deliveryDate;
    }
  
    public Item getItem() {
        return item;
    }

    public void setItem(Product item) {
        this.item = item;
    }
    
    public Integer getMatQty() {
        return matQty;
    }

    public void setMatQty(Integer matQty) {
        this.matQty = matQty;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
