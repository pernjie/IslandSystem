/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enumerator;

import java.io.Serializable;

/**
 *
 * @author Anna
 */
public enum InvenLoc implements Serializable {
    MF("Manufacturing Facility"), //Named constants
    FRONTEND_WAREHOUSE("FrontEnd Warehouse"), 
    BACKEND_WAREHOUSE("BackEnd Warehouse"), 
    FRONTEND_STORE("FrontEnd Store"),
    RETAIL_SCORE("Retail Store"),
    KITCHEN("Kitchen");   
    
    private final String label; //private variable
    
    InvenLoc(String label){  //constructor
        this.label = label;       
    }
    
    public String getLabel(){
        return label;
    }
    
   public static InvenLoc getIndex(int ord) {
        return InvenLoc.values()[ord]; // less safe
    }
}
