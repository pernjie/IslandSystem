/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import entity.Material;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pern
 */
public class MatWeek {
    private Material mat;
    private List<StoreClassWeekly> stores;
    
    public MatWeek() {
        stores = new ArrayList<>();
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public List<StoreClassWeekly> getStores() {
        return stores;
    }

    public void setStores(List<StoreClassWeekly> stores) {
        this.stores = stores;
    }
}
