package classes;

import entity.Material;
import java.util.ArrayList;
import java.util.List;

public class MaterialClass {
    private Material mat;
    private List<StoreClass> stores;
    // 1: Holt
    // 2: Winter
    private int forecastType;
    
    public MaterialClass() {
        forecastType = 1;
    }

    public Material getMat() {
        return mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public List<StoreClass> getStores() {
        return stores;
    }

    public void setStores(List<StoreClass> stores) {
        this.stores = stores;
    }

    public int getForecastType() {
        return forecastType;
    }

    public void setForecastType(int forecastType) {
        this.forecastType = forecastType;
    }
}
