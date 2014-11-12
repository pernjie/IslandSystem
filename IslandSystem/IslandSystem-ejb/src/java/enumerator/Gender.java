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
public enum Gender implements Serializable {

    MALE("Male"),
    FEMALE("Female"),
    ALL("All");

    private final String label; //private variable

    Gender(String label) {  //constructor
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Gender getIndex(int ord) {
        return Gender.values()[ord]; // less safe
    }
}
