/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import enumerator.SvcCat;
import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * @author dyihoon90
 */
@Named(value = "svcRecService")
@ApplicationScoped
public class SvcRecService {

    private final static SvcCat[] categories;

    public SvcRecService() {
    }

    static {

        categories = new SvcCat[3];
        categories[0] = SvcCat.ASSEMBLY;
        categories[1] = SvcCat.DELIVERY;
        categories[2] = SvcCat.INSTALLATION;
    }

    public List<SvcCat> getCategories() {
        return Arrays.asList(categories);
    }


}
