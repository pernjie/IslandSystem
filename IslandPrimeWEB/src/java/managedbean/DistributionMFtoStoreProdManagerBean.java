/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Product;
import entity.DistributionMFtoStoreProd;
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
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "distributionMFtoStoreProdManagerBean")
@ViewScoped
public class DistributionMFtoStoreProdManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String mfIdS;
    private String prodIdS;
    private String storeIdS;
    private String mfName;
    private String prodName;
    private String storeName;
    private List<Facility> mfs;
    private List<Facility> stores;
    private List<Facility> facilities;
    private List<Product> products;

    private Long newDistributionMFtoStoreProdId;
    private String statusMessage;
    private DistributionMFtoStoreProd selectedDistributionMFtoStoreProd;
    private List<DistributionMFtoStoreProd> filteredDistributionMFtoStoreProds;
    private List<DistributionMFtoStoreProd> distributionMFtoStoreProds;
    private DistributionMFtoStoreProd newDistributionMFtoStoreProd = new DistributionMFtoStoreProd();

    @PostConstruct
    public void init() {
        distributionMFtoStoreProds = globalHqBean.getAllDistributionMFtoStoreProds();
        mfs = globalHqBean.getAllMFs();
        products = globalHqBean.getMfProducts("Jurong MF");
        stores = globalHqBean.getAllStores();
        facilities = globalHqBean.getAllFacilities();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("products", products);
    }

    public void saveNewDistributionMFtoStoreProd(ActionEvent event) {
        System.out.println(mfIdS);
        System.out.println(prodIdS);
        System.out.println(storeIdS);
        Long mfId = Long.valueOf(mfIdS);
        Long prodId = Long.valueOf(prodIdS);
        Long storeId = Long.valueOf(storeIdS);

        try {
            newDistributionMFtoStoreProdId = globalHqBean.addNewDistributionMFtoStoreProd(mfId, prodId, storeId);
            statusMessage = "New DistributionMFtoStoreProd Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New DistributionMFtoStoreProd Result: "
                    + statusMessage + " (New DistributionMFtoStoreProd ID is " + newDistributionMFtoStoreProdId + ")", ""));
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newDistributionMFtoStoreProdId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New DistributionMFtoStoreProd Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newDistributionMFtoStoreProdId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New DistributionMFtoStoreProd Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newDistributionMFtoStoreProdId = -1L;
            statusMessage = "New DistributionMFtoStoreProd Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New DistributionMFtoStoreProd Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteDistributionMFtoStoreProd() {
         try {
            globalHqBean.removeDistributionMFtoStoreProd(selectedDistributionMFtoStoreProd);
            FacesMessage msg = new FacesMessage("Distribution Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            distributionMFtoStoreProds = globalHqBean.getAllDistributionMFtoStoreProds();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateDistributionMFtoStoreProd((DistributionMFtoStoreProd) event.getObject());
            FacesMessage msg = new FacesMessage("DistributionMFtoStoreProd Edited", ((DistributionMFtoStoreProd) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            distributionMFtoStoreProds = globalHqBean.getAllDistributionMFtoStoreProds();
            statusMessage = dcx.getMessage();
            System.out.println(statusMessage);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((DistributionMFtoStoreProd) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public DistributionMFtoStoreProd getNewDistributionMFtoStoreProd() {
        return newDistributionMFtoStoreProd;
    }

    public void setNewDistributionMFtoStoreProd(DistributionMFtoStoreProd newDistributionMFtoStoreProd) {
        this.newDistributionMFtoStoreProd = newDistributionMFtoStoreProd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DistributionMFtoStoreProd> getFilteredDistributionMFtoStoreProds() {
        return filteredDistributionMFtoStoreProds;
    }

    public void setFilteredDistributionMFtoStoreProds(List<DistributionMFtoStoreProd> filteredDistributionMFtoStoreProds) {
        this.filteredDistributionMFtoStoreProds = filteredDistributionMFtoStoreProds;
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

    public List<DistributionMFtoStoreProd> getDistributionMFtoStoreProds() {
        return distributionMFtoStoreProds;
    }

    public DistributionMFtoStoreProd getSelectedDistributionMFtoStoreProd() {
        return selectedDistributionMFtoStoreProd;
    }

    public void setSelectedDistributionMFtoStoreProd(DistributionMFtoStoreProd selectedDistributionMFtoStoreProd) {
        this.selectedDistributionMFtoStoreProd = selectedDistributionMFtoStoreProd;
    }

    /**
     * Creates a new instance of DistributionMFtoStoreProdManagerBean
     */
    public DistributionMFtoStoreProdManagerBean() {

    }

    public Long getNewDistributionMFtoStoreProdId() {
        return newDistributionMFtoStoreProdId;
    }

    public void setNewDistributionMFtoStoreProdId(Long newDistributionMFtoStoreProdId) {
        this.newDistributionMFtoStoreProdId = newDistributionMFtoStoreProdId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getMfIdS() {
        return mfIdS;
    }

    public void setMfIdS(String mfIdS) {
        this.mfIdS = mfIdS;
    }

    public String getProdIdS() {
        return prodIdS;
    }

    public void setProdIdS(String prodIdS) {
        this.prodIdS = prodIdS;
    }

    public String getStoreIdS() {
        return storeIdS;
    }

    public void setStoreIdS(String storeIdS) {
        this.storeIdS = storeIdS;
    }

    public String getMfName() {
        return mfName;
    }

    public void setMfName(String mfName) {
        this.mfName = mfName;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public List<String> getProdNames() {
        List<String> prodNames = new ArrayList<String>();
        for (int i = 0; i < products.size(); i++) {
            prodNames.add(i, products.get(i).getName());
        }
        return prodNames;
    }

    public List<String> getStoreNames() {
        List<String> storeNames = new ArrayList<String>();
        for (int i = 0; i < stores.size(); i++) {
            storeNames.add(i, stores.get(i).getName());
        }
        return storeNames;
    }

}
