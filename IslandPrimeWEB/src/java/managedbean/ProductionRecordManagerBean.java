/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.WeekHelper;
import entity.Facility;
import entity.Material;
import entity.ProductionRecord;
import entity.Staff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import session.stateless.GlobalHqBean;
import session.stateless.ScmBean;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "productionRecordManagerBean")
@ViewScoped
public class ProductionRecordManagerBean implements Serializable {

    @EJB
    private ScmBean sb;
    private String loggedInEmail;
    private List<Facility> mfs;
    private List<Facility> stores;
    private List<Facility> facilities;
    private List<Material> materials;
    private String statusMessage;
    private ProductionRecord selectedProductionRecord;
    private List<ProductionRecord> filteredProductionRecords;
    private List<ProductionRecord> productionRecords;

    @PostConstruct
    public void init() {
        WeekHelper wh = new WeekHelper();
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Facility mf = sb.getFac(loggedInEmail);
        productionRecords = sb.getProductionRecords(mf, wh.getYear(0), wh.getPeriod(0));
        materials = sb.getAllFurniture();
        stores = sb.getAllStores();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("materials", materials);
    }

    public List<ProductionRecord> getFilteredProductionRecords() {
        return filteredProductionRecords;
    }

    public void setFilteredProductionRecords(List<ProductionRecord> filteredProductionRecords) {
        this.filteredProductionRecords = filteredProductionRecords;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<ProductionRecord> getProductionRecords() {
        return productionRecords;
    }

    public ProductionRecord getSelectedProductionRecord() {
        return selectedProductionRecord;
    }

    public void setSelectedProductionRecord(ProductionRecord selectedProductionRecord) {
        this.selectedProductionRecord = selectedProductionRecord;
    }

    /**
     * Creates a new instance of ProductionRecordManagerBean
     */
    public ProductionRecordManagerBean() {

    }

    public List<Material> getMaterials() {
        return materials;
    }

    public List<Facility> getMfs() {
        return mfs;
    }

    public void setMfs(List<Facility> mfs) {
        this.mfs = mfs;
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

    public List<String> getMfNames() {
        List<String> mfNames = new ArrayList<String>();
        for (int i = 0; i < mfs.size(); i++) {
            mfNames.add(i, mfs.get(i).getName());
        }
        return mfNames;
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

}
