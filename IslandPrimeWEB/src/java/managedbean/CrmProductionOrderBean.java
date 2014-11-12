/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Material;
import entity.ProductionOrder;
import entity.Region;
import entity.Staff;
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
import session.stateless.CIBeanLocal;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "crmProductionOrderBean")
@ViewScoped
public class CrmProductionOrderBean implements Serializable {

    @EJB
    private OpCrmBean opCrmBean;
    @EJB
    private CIBeanLocal cib;
    private Long id;
    private String matIdS;
    private String storeIdS;
    private String matName;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private String storeName;
    private List<Facility> stores;
    private List<Facility> facilities;
    private List<Material> materials;
    private String period;
    private String year;
    private String quantity;

    private Long newProductionOrderId;
    private String statusMessage;
    private ProductionOrder selectedProductionOrder;
    private List<ProductionOrder> filteredProductionOrders;
    private List<ProductionOrder> productionOrders;
    private ProductionOrder newProductionOrder = new ProductionOrder();

    @PostConstruct
    public void init() {
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = opCrmBean.getUserFacility(userEmail);
        userRegion = opCrmBean.getUserRegion(userFacility);
        productionOrders = opCrmBean.getProductionOrders(userFacility);
        materials = opCrmBean.getAllFurniture();
        stores = opCrmBean.getAllStores();
        facilities = opCrmBean.getAllFacilities();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("materials", materials);

    }

    public void saveNewProductionOrder(ActionEvent event) {
        System.out.println(matIdS);
        System.out.println(storeIdS);
        Long matId = Long.valueOf(matIdS);
        Integer periodInt = Integer.valueOf(period);
        Integer yearInt = Integer.valueOf(year);
        Integer quantityInt = Integer.valueOf(quantity);

        try {
            newProductionOrderId = opCrmBean.addNewProductionOrder(periodInt, yearInt, quantityInt, matId, userFacility.getId());
            statusMessage = "New ProductionOrder Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ProductionOrder Result: "
                    + statusMessage + " (New ProductionOrder ID is " + newProductionOrderId + ")", ""));
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Added New Production Order: " + newProductionOrderId);
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newProductionOrderId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New ProductionOrder Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newProductionOrderId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New ProductionOrder Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newProductionOrderId = -1L;
            statusMessage = "Create new Production Order Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ProductionOrder Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteProductionOrder() {
        try {
            Long PdtnOId = selectedProductionOrder.getId();
            opCrmBean.removeProductionOrder(selectedProductionOrder);
            productionOrders = opCrmBean.getAllProductionOrders();
            FacesMessage msg = new FacesMessage("Production Order Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Production Order: " + PdtnOId);
        } catch (ReferenceConstraintException ex) {
            productionOrders = opCrmBean.getAllProductionOrders();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            opCrmBean.updateProductionOrder((ProductionOrder) event.getObject());
            productionOrders = opCrmBean.getAllProductionOrders();
            FacesMessage msg = new FacesMessage("Production Order Edited", ((ProductionOrder) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Edited Production Order: " + ((ProductionOrder) event.getObject()).getId().toString());
        } catch (DetailsConflictException dcx) {
            productionOrders = opCrmBean.getAllProductionOrders();
            statusMessage = dcx.getMessage();
            System.out.println(statusMessage);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((ProductionOrder) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public ProductionOrder getNewProductionOrder() {
        return newProductionOrder;
    }

    public void setNewProductionOrder(ProductionOrder newProductionOrder) {
        this.newProductionOrder = newProductionOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductionOrder> getFilteredProductionOrders() {
        return filteredProductionOrders;
    }

    public void setFilteredProductionOrders(List<ProductionOrder> filteredProductionOrders) {
        this.filteredProductionOrders = filteredProductionOrders;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public OpCrmBean getOpCrmBean() {
        return opCrmBean;
    }

    public void setOpCrmBean(OpCrmBean opCrmBean) {
        this.opCrmBean = opCrmBean;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Facility getUserFacility() {
        return userFacility;
    }

    public void setUserFacility(Facility userFacility) {
        this.userFacility = userFacility;
    }

    public Region getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(Region userRegion) {
        this.userRegion = userRegion;
    }

    public List<ProductionOrder> getProductionOrders() {
        return productionOrders;
    }

    public ProductionOrder getSelectedProductionOrder() {
        return selectedProductionOrder;
    }

    public void setSelectedProductionOrder(ProductionOrder selectedProductionOrder) {
        this.selectedProductionOrder = selectedProductionOrder;
    }

    /**
     * Creates a new instance of ProductionOrderManagerBean
     */
    public CrmProductionOrderBean() {

    }

    public Long getNewProductionOrderId() {
        return newProductionOrderId;
    }

    public void setNewProductionOrderId(Long newProductionOrderId) {
        this.newProductionOrderId = newProductionOrderId;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public String getMatIdS() {
        return matIdS;
    }

    public void setMatIdS(String matIdS) {
        this.matIdS = matIdS;
    }

    public String getStoreIdS() {
        return storeIdS;
    }

    public void setStoreIdS(String storeIdS) {
        this.storeIdS = storeIdS;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<Facility> getStores() {
        return stores;
    }

    public void setStores(List<Facility> stores) {
        this.stores = stores;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<String> getMatNames() {
        List<String> matNames = new ArrayList<String>();
        for (int i = 0; i < materials.size(); i++) {
            matNames.add(i, materials.get(i).getName());
        }
        return matNames;
    }

    public List<String> getStoreNames() {
        List<String> storeNames = new ArrayList<String>();
        for (int i = 0; i < stores.size(); i++) {
            storeNames.add(i, stores.get(i).getName());
        }
        return storeNames;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
