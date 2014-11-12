package classes;

import entity.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemClass {
    private Item mat;
    private List<StoreClass> stores;
    // 1: Holt
    // 2: Winter
    private int forecastType;
    
    public ItemClass() {
        forecastType = 1;
    }

    public Item getMat() {
        return mat;
    }

    public void setMat(Item mat) {
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
