/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.WeekHelper;
import entity.Facility;
import entity.Product;
import entity.PurchasePlanningRecord;
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
@ManagedBean(name = "purchasePlanningRecordManagerBean")
@ViewScoped
public class PurchasePlanningRecordManagerBean implements Serializable {

    @EJB
    private ScmBean sb;
    private String loggedInEmail;
    private List<Facility> mfs;
    private List<Facility> stores;
    private List<Facility> facilities;
    private List<Product> products;
    private String statusMessage;
    private PurchasePlanningRecord selectedPurchasePlanningRecord;
    private List<PurchasePlanningRecord> filteredPurchasePlanningRecords;
    private List<PurchasePlanningRecord> purchasePlanningRecords;

    @PostConstruct
    public void init() {
        WeekHelper wh = new WeekHelper();
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Facility mf = sb.getFac(loggedInEmail);
        purchasePlanningRecords = sb.getPurchasePlanningRecords(mf, wh.getYear(0), wh.getPeriod(0));
        products = sb.getAllProducts();
        stores = sb.getAllStores();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("products", products);
    }

    public List<PurchasePlanningRecord> getFilteredPurchasePlanningRecords() {
        return filteredPurchasePlanningRecords;
    }

    public void setFilteredPurchasePlanningRecords(List<PurchasePlanningRecord> filteredPurchasePlanningRecords) {
        this.filteredPurchasePlanningRecords = filteredPurchasePlanningRecords;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<PurchasePlanningRecord> getPurchasePlanningRecords() {
        return purchasePlanningRecords;
    }

    public PurchasePlanningRecord getSelectedPurchasePlanningRecord() {
        return selectedPurchasePlanningRecord;
    }

    public void setSelectedPurchasePlanningRecord(PurchasePlanningRecord selectedPurchasePlanningRecord) {
        this.selectedPurchasePlanningRecord = selectedPurchasePlanningRecord;
    }

    /**
     * Creates a new instance of PurchasePlanningRecordManagerBean
     */
    public PurchasePlanningRecordManagerBean() {

    }

    public List<Product> getProducts() {
        return products;
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

    public void setProducts(List<Product> products) {
        this.products = products;
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
        for (int i = 0; i < products.size(); i++) {
            matNames.add(i, products.get(i).getName());
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
