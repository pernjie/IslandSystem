/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Item;
import entity.Component;
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
@ManagedBean(name = "componentManagerBean")
@ViewScoped
public class ComponentManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private Item main;
    private Item consistOf;
    private Double weight;
    private Integer quantity;
    private Long newComponentId;
    private List<Item> mains;
    private List<Item> consistOfMats;
    private List<Item> consistOfKits;
    private List<Item> consistOfs;
    private List<Item> items;
    private Component newComponent;
    private String statusMessage;
    private Component selectedComponent;
    private List<Component> filteredComponents;
    private List<Component> components;
    private Boolean mainIsMat;
    private Boolean mainIsKit;
    

    @PostConstruct
    public void init() {
        mainIsKit = false;
        mainIsMat = false;
        weight = null;
        quantity = null;
        components = globalHqBean.getAllComponents();
        mains = globalHqBean.getAllMatAndKits();
        consistOfMats = globalHqBean.getAllMatItems();
        consistOfKits = globalHqBean.getAllKitItems();
        consistOfs = globalHqBean.getAllItems();
        items = globalHqBean.getAllItems();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("items", items);
    }
    public void onMainChange() {
        if (main.getItemType().equals("Material")) {
            consistOfs = consistOfMats;
        } else {
            consistOfs = consistOfKits;
        }
    }
    public void saveNewComponent(ActionEvent event) {

        try {
            if (weight != null && quantity != null) {
                statusMessage = "Do not fill up both weight and quantity.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please resolve: "
                        + statusMessage, ""));
            } else if ((weight!=null && weight <= 0) || (quantity !=null && quantity <= 0.0)) {
                statusMessage = "Do not fill in smaller or equals to zero.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please resolve: "
                        + statusMessage, ""));
            } else {
                newComponent = new Component();
                System.out.println("CONSIST OF IS: "+ consistOf.getName());
                newComponent.setConsistOf(consistOf);
                if (weight != null) {
                    newComponent.setWeight(weight);
                }
                if (quantity != null) {
                    newComponent.setQuantity(quantity);
                }
                newComponent.setMain(main);
                newComponentId = globalHqBean.addNewComponent(newComponent);
                components = globalHqBean.getAllComponents();
                statusMessage = "New Component Saved Successfully.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Component Result: "
                        + statusMessage + " (New Component ID is " + newComponentId + ")", ""));
            }
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            components = globalHqBean.getAllComponents();
            newComponentId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Component Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newComponentId = -1L;
            components = globalHqBean.getAllComponents();
            statusMessage = "New Component Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Component Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteComponent() {
        try {
            globalHqBean.removeComponent(selectedComponent);
            components = globalHqBean.getAllComponents();
            FacesMessage msg = new FacesMessage("Component Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            components = globalHqBean.getAllComponents();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            Component editedCk = (Component) event.getObject();
            if (editedCk.getQuantity() != null && editedCk.getWeight() != null) {
                components = globalHqBean.getAllComponents();
                statusMessage = "Do not fill up both quantity and weight";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please resolve: "
                        + statusMessage, ""));
            } else if ((editedCk.getWeight()!=null && editedCk.getWeight() <= 0) || (editedCk.getQuantity() !=null && editedCk.getQuantity() <= 0.0)) {
                components = globalHqBean.getAllComponents();
                statusMessage = "Do not fill in smaller or equals to zero.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please resolve: "
                        + statusMessage, ""));
            } else {
                globalHqBean.updateComponent((Component) event.getObject());
                components = globalHqBean.getAllComponents();
                FacesMessage msg = new FacesMessage("Component Edited", ((Component) event.getObject()).getId().toString());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (DetailsConflictException dcx) {
            components = globalHqBean.getAllComponents();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Component) event.getObject()).getId().toString());
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
     * Creates a new instance of ComponentManagerBean
     */
    public ComponentManagerBean() {

    }

    public List<Item> getConsistOfs() {
        return consistOfs;
    }

    public void setConsistOfs(List<Item> consistOfs) {
        this.consistOfs = consistOfs;
    }

    public Long getNewComponentId() {
        return newComponentId;
    }

    public void setNewComponentId(Long newComponentId) {
        this.newComponentId = newComponentId;
    }

    public Item getConsistOf() {
        return consistOf;
    }

    public void setConsistOf(Item consistOf) {
        this.consistOf = consistOf;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Component getNewComponent() {
        return newComponent;
    }

    public void setNewComponent(Component newComponent) {
        this.newComponent = newComponent;
    }

    public Component getSelectedComponent() {
        return selectedComponent;
    }

    public void setSelectedComponent(Component selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    public List<Component> getFilteredComponents() {
        return filteredComponents;
    }

    public void setFilteredComponents(List<Component> filteredComponents) {
        this.filteredComponents = filteredComponents;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public Item getMain() {
        return main;
    }

    public void setMain(Item main) {
        this.main = main;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Item> getMains() {
        return mains;
    }

    public void setMains(List<Item> mains) {
        this.mains = mains;
    }


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getConsistOfMats() {
        return consistOfMats;
    }

    public void setConsistOfMats(List<Item> consistOfMats) {
        this.consistOfMats = consistOfMats;
    }

    public List<Item> getConsistOfKits() {
        return consistOfKits;
    }

    public void setConsistOfKits(List<Item> consistOfKits) {
        this.consistOfKits = consistOfKits;
    }

    public Boolean getMainIsMat() {
        return mainIsMat;
    }

    public void setMainIsMat(Boolean mainIsMat) {
        this.mainIsMat = mainIsMat;
    }

    public Boolean getMainIsKit() {
        return mainIsKit;
    }

    public void setMainIsKit(Boolean mainIsKit) {
        this.mainIsKit = mainIsKit;
    }

}
