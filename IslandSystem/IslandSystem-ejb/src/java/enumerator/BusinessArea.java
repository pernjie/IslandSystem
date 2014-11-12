/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package enumerator;

/**
 *
 * @author nataliegoh
 */
public enum BusinessArea {
    FURNITURE ("Furniture"),
    KITCHEN ("Kitchen"),
    PRODUCT ("Product");
    
    private final String label; //private variable
    
    BusinessArea(String label){  //constructor
        this.label = label;       
    }
    
    public String getLabel(){
        return label;
    }
    
    public static BusinessArea getIndex(int ord) {
        return BusinessArea.values()[ord]; // less safe
    }
}


