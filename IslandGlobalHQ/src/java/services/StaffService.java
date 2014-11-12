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
@Named(value = "staffService")
@ApplicationScoped
public class StaffService {

    private final static String[] roles;

    public StaffService() {
    }

    static {

        roles = new String[6];
        roles[0] = "";
        roles[1] = "System Admin";
        roles[2] = "Global HQ";
        roles[3] = "Raw";
        roles[4] = "Snacks";
        roles[5] = "Ready Meal";
    }

    public List<String> getRoles() {
        return Arrays.asList(roles);
    }


}
