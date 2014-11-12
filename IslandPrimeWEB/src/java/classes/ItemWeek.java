/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import entity.Item;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pern
 */
public class ItemWeek {
    private Item mat;
    private List<StoreClassWeekly> stores;
    
    public ItemWeek() {
        stores = new ArrayList<>();
    }

    public Item getMat() {
        return mat;
    }

    public void setMat(Item mat) {
        this.mat = mat;
    }

    public List<StoreClassWeekly> getStores() {
        return stores;
    }

    public void setStores(List<StoreClassWeekly> stores) {
        this.stores = stores;
    }
}
