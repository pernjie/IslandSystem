/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * @author dyihoon90
 */
@Named(value = "productService")
@ApplicationScoped
public class ProductService {

    private final static String[] categories;

    public ProductService() {
    }

    static {

        categories = new String[5];
        categories[0] = "Chilled";
        categories[1] = "Frozen";
        categories[2] = "Raw";
        categories[3] = "Snacks";
        categories[4] = "Ready Meal";
    }

    public List<String> getCategories() {
        return Arrays.asList(categories);
    }


}
