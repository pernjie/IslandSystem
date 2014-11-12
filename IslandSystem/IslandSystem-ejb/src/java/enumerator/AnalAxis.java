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
public enum AnalAxis implements Serializable{

    AGE("Age"),
    GENDER("Gender"),
    RFM("RFM Score"),
    NUMCUST("Number of Customers"),
    CAMPAIGNPROMOCODE("Number of campaign promo code used"),
    CAMPAIGNNUMHIT("Number of campaign hits"),
    CAMPAIGNNUMCUST("Number of customers in campaign"),
    ITEM("Item"),
    CATEGORY("Category"),
    QUANTITY("Total Sales Quantity"),
    EXPENDITURE("Total Sales Figures");
    private final String label; //private variable

    AnalAxis(String label) {  //constructor
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AnalAxis getIndex(int ord) {
        return AnalAxis.values()[ord]; // less safe
    }
}
