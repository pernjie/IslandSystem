
package enumerator;

import java.io.Serializable;

/**
 *
 * @author dyihoon90
 */
public enum MealCourse implements Serializable{

    BEVERAGE("Beverage"),
    DESSERT("Dessert"),
    MAINCOURSE("Main Course"),
    SIDEITEM("Side Item");
    private final String label; //private variable

    MealCourse(String label) {  //constructor
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static MealCourse getIndex(int ord) {
        return MealCourse.values()[ord]; // less safe
    }
}
