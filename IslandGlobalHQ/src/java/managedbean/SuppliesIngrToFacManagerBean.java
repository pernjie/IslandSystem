/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Ingredient;
import entity.Supplier;
import entity.SuppliesIngrToFac;
import java.io.Serializable;
import java.util.ArrayList;
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
@ManagedBean(name = "suppliesIngrToFacManagerBean")
@ViewScoped
public class SuppliesIngrToFacManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private Facility fac;
    private Ingredient ingredient;
    private Supplier supplier;
    private Integer lotSize;
    private Double lotWeight;
    private Double unitPrice;
    private Integer leadTime;
    private Long newSuppliesIngrToFacId;
    private List<Facility> facilities;
    private List<Ingredient> ingredients;
    private List<Supplier> suppliers;
    private SuppliesIngrToFac newSuppliesIngrToFac;
    private String statusMessage;
    private SuppliesIngrToFac selectedSuppliesIngrToFac;
    private List<SuppliesIngrToFac> filteredSuppliesIngrToFacs;
    private List<SuppliesIngrToFac> suppliesIngrToFacs;

    @PostConstruct
    public void init() {
        lotSize = null;
        lotWeight = null;
        suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
        facilities = globalHqBean.getAllStores();
        ingredients = globalHqBean.getAllIngredients();
        suppliers = globalHqBean.getAllSuppliers();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ingredients", ingredients);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("suppliers", suppliers);
    }
    
    public void doGenerate(){
    }
    public void saveNewSuppliesIngrToFac(ActionEvent event) {

        try {
            newSuppliesIngrToFac = new SuppliesIngrToFac();
            newSuppliesIngrToFac.setFac(fac);
            newSuppliesIngrToFac.setIngredient(ingredient);
            newSuppliesIngrToFac.setSup(supplier);
            newSuppliesIngrToFac.setLotSize(lotSize);
            newSuppliesIngrToFac.setLotWeight(lotWeight);
            newSuppliesIngrToFac.setUnitPrice(unitPrice);
            newSuppliesIngrToFac.setLeadTime(leadTime);
            newSuppliesIngrToFacId = globalHqBean.addNewSuppliesIngrToFac(newSuppliesIngrToFac);
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            statusMessage = "New SuppliesIngrToFac Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SuppliesIngrToFac Result: "
                    + statusMessage + " (New SuppliesIngrToFac ID is " + newSuppliesIngrToFacId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            newSuppliesIngrToFacId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New SuppliesIngrToFac Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newSuppliesIngrToFacId = -1L;
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            statusMessage = "New SuppliesIngrToFac Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SuppliesIngrToFac Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteSuppliesIngrToFac() {
        try {
            globalHqBean.removeSuppliesIngrToFac(selectedSuppliesIngrToFac);
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            FacesMessage msg = new FacesMessage("SPF Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateSuppliesIngrToFac((SuppliesIngrToFac) event.getObject());
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            FacesMessage msg = new FacesMessage("SuppliesIngrToFac Edited", ((SuppliesIngrToFac) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            suppliesIngrToFacs = globalHqBean.getAllSuppliesIngrToFacs();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((SuppliesIngrToFac) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /**
     * Creates a new instance of SuppliesIngrToFacManagerBean
     */
    public SuppliesIngrToFacManagerBean() {

    }

    public Long getNewSuppliesIngrToFacId() {
        return newSuppliesIngrToFacId;
    }

    public void setNewSuppliesIngrToFacId(Long newSuppliesIngrToFacId) {
        this.newSuppliesIngrToFacId = newSuppliesIngrToFacId;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public List<String> getFacNames() {
        List<String> facName = new ArrayList<String>();
        for (int i = 0; i < facilities.size(); i++) {
            facName.add(i, facilities.get(i).getName());
        }
        return facName;
    }

    public List<String> getProdNames() {
        List<String> prodName = new ArrayList<String>();
        for (int i = 0; i < ingredients.size(); i++) {
            prodName.add(i, ingredients.get(i).getName());
        }
        return prodName;
    }

    public List<String> getSupNames() {
        List<String> supName = new ArrayList<String>();
        for (int i = 0; i < suppliers.size(); i++) {
            supName.add(i, suppliers.get(i).getName());
        }
        return supName;
    }

    public Facility getFac() {
        return fac;
    }

    public void setFac(Facility fac) {
        this.fac = fac;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public Double getLotWeight() {
        return lotWeight;
    }

    public void setLotWeight(Double lotWeight) {
        this.lotWeight = lotWeight;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public SuppliesIngrToFac getNewSuppliesIngrToFac() {
        return newSuppliesIngrToFac;
    }

    public void setNewSuppliesIngrToFac(SuppliesIngrToFac newSuppliesIngrToFac) {
        this.newSuppliesIngrToFac = newSuppliesIngrToFac;
    }

    public SuppliesIngrToFac getSelectedSuppliesIngrToFac() {
        return selectedSuppliesIngrToFac;
    }

    public void setSelectedSuppliesIngrToFac(SuppliesIngrToFac selectedSuppliesIngrToFac) {
        this.selectedSuppliesIngrToFac = selectedSuppliesIngrToFac;
    }

    public List<SuppliesIngrToFac> getFilteredSuppliesIngrToFacs() {
        return filteredSuppliesIngrToFacs;
    }

    public void setFilteredSuppliesIngrToFacs(List<SuppliesIngrToFac> filteredSuppliesIngrToFacs) {
        this.filteredSuppliesIngrToFacs = filteredSuppliesIngrToFacs;
    }

    public List<SuppliesIngrToFac> getSuppliesIngrToFacs() {
        return suppliesIngrToFacs;
    }

    public void setSuppliesIngrToFacs(List<SuppliesIngrToFac> suppliesIngrToFacs) {
        this.suppliesIngrToFacs = suppliesIngrToFacs;
    }

}
