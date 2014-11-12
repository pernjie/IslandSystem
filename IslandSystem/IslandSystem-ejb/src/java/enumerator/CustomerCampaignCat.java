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
public enum CustomerCampaignCat implements Serializable{

    ALL("All Customers"),
    NEW("New Customers"),
    ACTIVE("Active Customers"),
    INACTIVE("Inactive Customers");

    private final String label; //private variable

    CustomerCampaignCat(String label) {  //constructor
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CustomerCampaignCat getIndex(int ord) {
        return CustomerCampaignCat.values()[ord]; // less safe
    }
}
