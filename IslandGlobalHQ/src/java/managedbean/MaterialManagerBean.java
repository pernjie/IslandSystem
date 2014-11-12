/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Material;
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
import services.MaterialService;
import session.stateless.GlobalHqBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "materialManagerBean")
@ViewScoped
public class MaterialManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private MaterialService materialService;
    private Long id;
    private String name;
    private String shortDesc;
    private String longDesc;
    private Double length;
    private Double breadth;
    private Double height;
    private Double weight;
    private String matCategory;
    private Integer genCategory;
    private String ItemPerBox;
    private Long newMaterialId;
    private String statusMessage;
    private Material selectedMaterial;
    private List<Material> filteredMaterials;
    private List<Material> materials;
    private Material newMaterial = new Material();

    @PostConstruct
    public void init() {
        materials = globalHqBean.getAllMaterials();
        materialService = new MaterialService();
    }

    public MaterialService getMaterialService() {
        return materialService;
    }

    public Material getNewMaterial() {
        return newMaterial;
    }

    public void setNewMaterial(Material newMaterial) {
        this.newMaterial = newMaterial;
    }

    public List<String> getMatCategories() {
        return materialService.getMatCategories();
    }

    public List<Integer> getGenCategories() {
        return materialService.getGenCategories();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Material> getFilteredMaterials() {
        return filteredMaterials;
    }

    public void setFilteredMaterials(List<Material> filteredMaterials) {
        this.filteredMaterials = filteredMaterials;
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

    public List<Material> getMaterials() {
        return materials;
    }

    public Material getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(Material selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    /**
     * Creates a new instance of MaterialManagerBean
     */
    public MaterialManagerBean() {

    }

    public Long getNewMaterialId() {
        return newMaterialId;
    }

    public void setNewMaterialId(Long newMaterialId) {
        this.newMaterialId = newMaterialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getBreadth() {
        return breadth;
    }

    public void setBreadth(Double breadth) {
        this.breadth = breadth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getMatCategory() {
        return matCategory;
    }

    public void setMatCategory(String matCategory) {
        this.matCategory = matCategory;
    }

    public Integer getGenCategory() {
        return genCategory;
    }

    public void setGenCategory(Integer genCategory) {
        this.genCategory = genCategory;
    }

    public String getItemPerBox() {
        return ItemPerBox;
    }

    public void setItemPerBox(String ItemPerBox) {
        this.ItemPerBox = ItemPerBox;
    }
    
    

    public void saveNewMaterial(ActionEvent event) {

        try {
            newMaterial = new Material();
            newMaterial.setName(name);
            newMaterial.setShortDesc(shortDesc);
            newMaterial.setLongDesc(longDesc);
            newMaterial.setLength(length);
            newMaterial.setBreadth(breadth);
            newMaterial.setHeight(height);
            newMaterial.setWeight(weight);
            newMaterial.setGenCategory(genCategory);
            newMaterial.setMatCategory(matCategory);
            newMaterial.setItemPerBox(Integer.valueOf(ItemPerBox));
            newMaterialId = globalHqBean.addNewMaterial(newMaterial);
            statusMessage = "New Material Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Material Result: "
                    + statusMessage + " (New Material ID is " + newMaterialId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newMaterialId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Material Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newMaterialId = -1L;
            statusMessage = "New Material Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Material Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteMaterial() {
        try {
            globalHqBean.removeMaterial(selectedMaterial);
            materials = globalHqBean.getAllMaterials();
            FacesMessage msg = new FacesMessage("Material Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            materials = globalHqBean.getAllMaterials();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateMaterial((Material) event.getObject());
            materials = globalHqBean.getAllMaterials();
            FacesMessage msg = new FacesMessage("Material Edited", ((Material) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException nex) {
            materials = globalHqBean.getAllMaterials();
            statusMessage = "Name already exist in database.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Material) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
