/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enumerator.MealCourse;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "MenuItem.findAll", query = "SELECT m FROM MenuItem m")})
public class MenuItem extends Item implements Serializable {

    @Column(name = "MEAL_COURSE")
    private MealCourse mealCourse;
    @Column(name = "RECIPE")
    private String recipe;

    public MenuItem() {
    }

    public MealCourse getMealCourse() {
        return mealCourse;
    }

    public void setMealCourse(MealCourse mealCourse) {
        this.mealCourse = mealCourse;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

}
