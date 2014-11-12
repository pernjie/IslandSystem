
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Material;
import entity.DistributionMFtoStore;
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
@ManagedBean(name = "distributionMFtoStoreManagerBean")
@ViewScoped
public class DistributionMFtoStoreManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String mfIdS;
    private String matIdS;
    private String storeIdS;
    private String mfName;
    private String matName;
    private String storeName;
    private List<Facility> mfs;
    private List<Facility> stores;
    private List<Facility> facilities;
    private List<Material> materials;

    private Long newDistributionMFtoStoreId;
    private String statusMessage;
    private DistributionMFtoStore selectedDistributionMFtoStore;
    private List<DistributionMFtoStore> filteredDistributionMFtoStores;
    private List<DistributionMFtoStore> distributionMFtoStores;
    private DistributionMFtoStore newDistributionMFtoStore = new DistributionMFtoStore();

    @PostConstruct
    public void init() {
        distributionMFtoStores = globalHqBean.getAllDistributionMFtoStores();
        mfs = globalHqBean.getAllMFs();
        materials = globalHqBean.getMfMaterials("Jurong MF");
        stores = globalHqBean.getAllStores();
        facilities = globalHqBean.getAllFacilities();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("materials", materials);
    }

    public void saveNewDistributionMFtoStore(ActionEvent event) {
        System.out.println(mfIdS);
        System.out.println(matIdS);
        System.out.println(storeIdS);
        Long mfId = Long.valueOf(mfIdS);
        Long matId = Long.valueOf(matIdS);
        Long storeId = Long.valueOf(storeIdS);

        try {
            newDistributionMFtoStoreId = globalHqBean.addNewDistributionMFtoStore(mfId, matId, storeId);
            statusMessage = "New DistributionMFtoStore Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New DistributionMFtoStore Result: "
                    + statusMessage + " (New DistributionMFtoStore ID is " + newDistributionMFtoStoreId + ")", ""));
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newDistributionMFtoStoreId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New DistributionMFtoStore Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newDistributionMFtoStoreId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New DistributionMFtoStore Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newDistributionMFtoStoreId = -1L;
            statusMessage = "New DistributionMFtoStore Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New DistributionMFtoStore Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteDistributionMFtoStore() {
         try {
            globalHqBean.removeDistributionMFtoStore(selectedDistributionMFtoStore);
            FacesMessage msg = new FacesMessage("Distribution Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            distributionMFtoStores = globalHqBean.getAllDistributionMFtoStores();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateDistributionMFtoStore((DistributionMFtoStore) event.getObject());
            FacesMessage msg = new FacesMessage("DistributionMFtoStore Edited", ((DistributionMFtoStore) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            distributionMFtoStores = globalHqBean.getAllDistributionMFtoStores();
            statusMessage = dcx.getMessage();
            System.out.println(statusMessage);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((DistributionMFtoStore) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public DistributionMFtoStore getNewDistributionMFtoStore() {
        return newDistributionMFtoStore;
    }

    public void setNewDistributionMFtoStore(DistributionMFtoStore newDistributionMFtoStore) {
        this.newDistributionMFtoStore = newDistributionMFtoStore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DistributionMFtoStore> getFilteredDistributionMFtoStores() {
        return filteredDistributionMFtoStores;
    }

    public void setFilteredDistributionMFtoStores(List<DistributionMFtoStore> filteredDistributionMFtoStores) {
        this.filteredDistributionMFtoStores = filteredDistributionMFtoStores;
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

    public List<DistributionMFtoStore> getDistributionMFtoStores() {
        return distributionMFtoStores;
    }

    public DistributionMFtoStore getSelectedDistributionMFtoStore() {
        return selectedDistributionMFtoStore;
    }

    public void setSelectedDistributionMFtoStore(DistributionMFtoStore selectedDistributionMFtoStore) {
        this.selectedDistributionMFtoStore = selectedDistributionMFtoStore;
    }

    /**
     * Creates a new instance of DistributionMFtoStoreManagerBean
     */
    public DistributionMFtoStoreManagerBean() {

    }

    public Long getNewDistributionMFtoStoreId() {
        return newDistributionMFtoStoreId;
    }

    public void setNewDistributionMFtoStoreId(Long newDistributionMFtoStoreId) {
        this.newDistributionMFtoStoreId = newDistributionMFtoStoreId;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public String getMfIdS() {
        return mfIdS;
    }

    public void setMfIdS(String mfIdS) {
        this.mfIdS = mfIdS;
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

    public String getMfName() {
        return mfName;
    }

    public void setMfName(String mfName) {
        this.mfName = mfName;
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
