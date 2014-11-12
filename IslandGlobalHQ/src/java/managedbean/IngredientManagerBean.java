/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Ingredient;
import java.io.Serializable;
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
@ManagedBean(name = "ingredientManagerBean")
@ViewScoped
public class IngredientManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String name;
    private String description;
    private Integer shelfLifeInDays;
    private Long newIngredientId;
    private String statusMessage;
    private Ingredient selectedIngredient;
    private List<Ingredient> filteredIngredients;
    private List<Ingredient> ingredients;
    private Ingredient newIngredient = new Ingredient();

    @PostConstruct
    
    public void init() {
        ingredients = globalHqBean.getAllIngredients();
    }


    
    public Ingredient getNewIngredient() {
        return newIngredient;
    }

    
    public void setNewIngredient(Ingredient newIngredient) {
        this.newIngredient = newIngredient;
    }

    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public List<Ingredient> getFilteredIngredients() {
        return filteredIngredients;
    }

    
    public void setFilteredIngredients(List<Ingredient> filteredIngredients) {
        this.filteredIngredients = filteredIngredients;
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

    
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    
    public Ingredient getSelectedIngredient() {
        return selectedIngredient;
    }

    
    public void setSelectedIngredient(Ingredient selectedIngredient) {
        this.selectedIngredient = selectedIngredient;
    }

    /**
     * Creates a new instance of IngredientManagerBean
     */
    public IngredientManagerBean() {

    }

    
    public Long getNewIngredientId() {
        return newIngredientId;
    }

    
    public void setNewIngredientId(Long newIngredientId) {
        this.newIngredientId = newIngredientId;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    public Integer getShelfLifeInDays() {
        return shelfLifeInDays;
    }

    public void setShelfLifeInDays(Integer shelfLifeInDays) {
        this.shelfLifeInDays = shelfLifeInDays;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    public void saveNewIngredient(ActionEvent event) {
        try {
            newIngredient = new Ingredient();
            newIngredient.setName(name);
            newIngredient.setShortDesc(description);
            newIngredientId = globalHqBean.addNewIngredient(newIngredient);
            statusMessage = "New Ingredient Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Ingredient Result: "
                    + statusMessage + " (New Ingredient ID is " + newIngredientId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newIngredientId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Ingredient Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newIngredientId = -1L;
            statusMessage = "New Ingredient Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Ingredient Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    
    public void deleteIngredient() {
        try {
            globalHqBean.removeIngredient(selectedIngredient);
            ingredients = globalHqBean.getAllIngredients();
            FacesMessage msg = new FacesMessage("Ingredient Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            ingredients = globalHqBean.getAllIngredients();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    
    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateIngredient((Ingredient) event.getObject());
            ingredients = globalHqBean.getAllIngredients();
            FacesMessage msg = new FacesMessage("Ingredient Edited", ((Ingredient) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            ingredients = globalHqBean.getAllIngredients();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Ingredient) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
