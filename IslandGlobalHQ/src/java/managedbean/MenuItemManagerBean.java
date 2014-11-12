/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.MenuItem;
import enumerator.MealCourse;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
import session.stateless.GlobalHqBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "menuItemManagerBean")
@ViewScoped
public class MenuItemManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String name;
    private String description;
    private Long newMenuItemId;
    private String statusMessage;
    private MenuItem selectedMenuItem;
    private List<MenuItem> filteredMenuItems;
    private List<MenuItem> menuItems;
    private MenuItem newMenuItem = new MenuItem();
    private MealCourse mealCourse;
    private String recipe;

    @PostConstruct

    public void init() {
        menuItems = globalHqBean.getAllMenuItems();
    }

    public void saveNewMenuItem(ActionEvent event) {
        try {
            newMenuItem = new MenuItem();
            newMenuItem.setName(name);
            newMenuItem.setShortDesc(description);
            newMenuItem.setMealCourse(mealCourse);
            newMenuItem.setRecipe(recipe);
            newMenuItemId = globalHqBean.addNewMenuItem(newMenuItem);
            statusMessage = "New MenuItem Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New MenuItem Result: "
                    + statusMessage + " (New MenuItem ID is " + newMenuItemId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newMenuItemId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New MenuItem Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newMenuItemId = -1L;
            statusMessage = "New MenuItem Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New MenuItem Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteMenuItem() {
        try {
            globalHqBean.removeMenuItem(selectedMenuItem);
            menuItems = globalHqBean.getAllMenuItems();
            FacesMessage msg = new FacesMessage("MenuItem Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            menuItems = globalHqBean.getAllMenuItems();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateMenuItem((MenuItem) event.getObject());
            menuItems = globalHqBean.getAllMenuItems();
            FacesMessage msg = new FacesMessage("MenuItem Edited", ((MenuItem) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            menuItems = globalHqBean.getAllMenuItems();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((MenuItem) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<MealCourse> getMealCourses() {
        MealCourse[] mealCourses = new MealCourse[4];
        mealCourses[0] = MealCourse.BEVERAGE;
        mealCourses[1] = MealCourse.DESSERT;
        mealCourses[2] = MealCourse.MAINCOURSE;
        mealCourses[3] = MealCourse.SIDEITEM;
        return Arrays.asList(mealCourses);

    }

    public MenuItem getNewMenuItem() {
        return newMenuItem;
    }

    public void setNewMenuItem(MenuItem newMenuItem) {
        this.newMenuItem = newMenuItem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MenuItem> getFilteredMenuItems() {
        return filteredMenuItems;
    }

    public void setFilteredMenuItems(List<MenuItem> filteredMenuItems) {
        this.filteredMenuItems = filteredMenuItems;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public GlobalHqBean getGlobalHqBean() {
        return globalHqBean;
    }

    public void setGlobalHqBean(GlobalHqBean globalHqBean) {
        this.globalHqBean = globalHqBean;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }

    /**
     * Creates a new instance of MenuItemManagerBean
     */
    public MenuItemManagerBean() {

    }

    public Long getNewMenuItemId() {
        return newMenuItemId;
    }

    public void setNewMenuItemId(Long newMenuItemId) {
        this.newMenuItemId = newMenuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
