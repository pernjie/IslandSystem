/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enumerator;

import java.io.Serializable;

/**
 *
 * @author dyihoon90
 */
public enum CustomerActivity implements Serializable{

    ALL("All customers"),
    ACTIVE("Active customers only"),
    INACTIVE("Inactive customers only");
    private final String label; //private variable

    CustomerActivity(String label) {  //constructor
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CustomerActivity getIndex(int ord) {
        return CustomerActivity.values()[ord]; // less safe
    }
}
