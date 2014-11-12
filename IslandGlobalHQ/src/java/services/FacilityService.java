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
@Named(value = "facilityService")
@ApplicationScoped
public class FacilityService {

    private final static String[] types;

    public FacilityService() {
    }

    static {
        types = new String[4];
        types[0] = "Regional HQ";
        types[1] = "Manufacturing";
        types[2] = "Store";
        types[3] = "Online";



    }

    public List<String> getTypes() {
        return Arrays.asList(types);
    }

}
