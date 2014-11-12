/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.ShelfType;
import entity.Staff;
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
import session.stateless.CIBeanLocal;
import session.stateless.InventoryBean;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "shelfTypeManagerBean")
@ViewScoped
public class ShelfTypeManagerBean implements Serializable{
    @EJB
    private InventoryBean inventoryBean;
    @EJB
    private CIBeanLocal cib;
    private String length;
    private String breadth;
    private String height;
    private String name;
    private String numSlot;
    private String weight;
    private Long id;
    private Long newShelfTypeId;
    private String statusMessage;
    private ShelfType selectedShelfType;
    private List<ShelfType> shelfTypes;
    private List<ShelfType> filteredShelfTypes;
    private ShelfType newShelfType;
    
    @PostConstruct
    public void init(){
         shelfTypes = inventoryBean.getAllShelfTypes();
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("shelftypes", shelfTypes);
    }
    
    public void saveNewShelfType(ActionEvent event) {
        System.out.println(name);
        System.out.println(weight);
        System.out.println(numSlot);
        System.out.println(length);
        System.out.println(breadth);
        System.out.println(height);
        
        Double lengthDouble = Double.valueOf(length);
        Double breadthDouble = Double.valueOf(breadth);
        Double heightDouble = Double.valueOf(height);
        Double weightDouble = Double.valueOf(weight); 
        
        Integer numSlotInt = Integer.valueOf(numSlot);

        try {
            newShelfType = new ShelfType();
            newShelfType.setName(name);
            newShelfType.setWeightLimit(weightDouble);
            newShelfType.setNumSlot(numSlotInt);
            newShelfType.setLength(lengthDouble);
            newShelfType.setBreadth(breadthDouble);
            newShelfType.setHeight(heightDouble);
            newShelfTypeId = inventoryBean.addNewShelfType(newShelfType);
            statusMessage = "New Shelf Type Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Shelf Type Result: "
                    + statusMessage + " (New Shelf Type ID is " + newShelfTypeId + ")", ""));
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Created new Shelf Type: " + newShelfTypeId);
            setName(null);
            setWeight(null);
            setNumSlot(null);
            setLength(null);
            setBreadth(null);
            setHeight(null);
            
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newShelfTypeId  = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Shelf Type Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newShelfTypeId  = -1L;
            statusMessage = "New Shelf Type Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Shelf Type Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }
    
  
    
    
    public void deleteShelfType() {
        try {
            Long shelfTypeId = selectedShelfType.getId();
            inventoryBean.removeShelfType(selectedShelfType);
            shelfTypes = inventoryBean.getAllShelfTypes();
            FacesMessage msg = new FacesMessage("Shelf Type Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Shelf Type: " + shelfTypeId);
        } catch (ReferenceConstraintException ex) {
            shelfTypes = inventoryBean.getAllShelfTypes();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public List<ShelfType> getFilteredShelfTypes() {
        return filteredShelfTypes;
    }

    public void setFilteredShelfTypes(List<ShelfType> filteredShelfTypes) {
        this.filteredShelfTypes = filteredShelfTypes;
    }
    
    /**
     * Creates a new instance of SuppliesMatToFacManagerBean
     */
    public ShelfTypeManagerBean() {

    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public InventoryBean getInventoryBean() {
        return inventoryBean;
    }

    public void setInventoryBean(InventoryBean inventoryBean) {
        this.inventoryBean = inventoryBean;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getBreadth() {
        return breadth;
    }

    public void setBreadth(String breadth) {
        this.breadth = breadth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumSlot() {
        return numSlot;
    }

    public void setNumSlot(String numSlot) {
        this.numSlot = numSlot;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShelfType getSelectedShelfType() {
        return selectedShelfType;
    }

    public Long getNewShelfTypeId() {
        return newShelfTypeId;
    }

    public ShelfType getNewShelfType() {
        return newShelfType;
    }

    public void setNewShelfType(ShelfType newShelfType) {
        this.newShelfType = newShelfType;
    }

    public void setNewShelfTypeId(Long newShelfTypeId) {
        this.newShelfTypeId = newShelfTypeId;
    }

    public void setSelectedShelfType(ShelfType selectedShelfType) {
        this.selectedShelfType = selectedShelfType;
    }

    public List<ShelfType> getShelfTypes() {
        return shelfTypes;
    }

    public void setShelfTypes(List<ShelfType> shelfTypes) {
        this.shelfTypes = shelfTypes;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    

}
