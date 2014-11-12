/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Material;
import entity.Supplier;
import entity.SuppliesMatToFac;
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
@ManagedBean(name = "suppliesMatToFacManagerBean")
@ViewScoped
public class SuppliesMatToFacManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String fac;
    private String mat;
    private String sup;
    private String facName;
    private String matName;
    private String supName;
    private String lotSize;
    private String unitPrice;
    private String leadTime;
    private List<Facility> facilities;
    private List<Material> materials;
    private List<Supplier> suppliers;
    private Facility facility;
    private Facility newFacility;

    private Long newSuppliesMatToFacId;
    private String statusMessage;
    private SuppliesMatToFac selectedSuppliesMatToFac;
    private List<SuppliesMatToFac> filteredSuppliesMatToFacs;
    private List<SuppliesMatToFac> suppliesMatToFacs;
    private SuppliesMatToFac newSuppliesMatToFac = new SuppliesMatToFac();

    @PostConstruct
    public void init() {
        suppliesMatToFacs = globalHqBean.getAllSuppliesMatToFacs();
        facilities = globalHqBean.getAllMFs();
        materials = globalHqBean.getAllRawMat();
        suppliers = globalHqBean.getAllSuppliers();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("materials", materials);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("suppliers", suppliers);
    }

    public void saveNewSuppliesMatToFac(ActionEvent event) {
        System.out.println(fac);
        System.out.println(mat);
        System.out.println(sup);
        Long facId = Long.valueOf(fac);
        Long matId = Long.valueOf(mat);
        Long supId = Long.valueOf(sup);
        Integer lotSizeInt = Integer.valueOf(lotSize);
        Double unitPriceDble = Double.valueOf(unitPrice);
        Integer leadTimeInt = Integer.valueOf(leadTime);

        try {
            newSuppliesMatToFacId = globalHqBean.addNewSuppliesMatToFac(facId, matId, supId, lotSizeInt, unitPriceDble, leadTimeInt);
            statusMessage = "New SuppliesMatToFac Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SuppliesMatToFac Result: "
                    + statusMessage + " (New SuppliesMatToFac ID is " + newSuppliesMatToFacId + ")", ""));
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newSuppliesMatToFacId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New SuppliesMatToFac Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newSuppliesMatToFacId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New SuppliesMatToFac Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newSuppliesMatToFacId = -1L;
            statusMessage = "New SuppliesMatToFac Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SuppliesMatToFac Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteSuppliesMatToFac() {
        try {
            globalHqBean.removeSuppliesMatToFac(selectedSuppliesMatToFac);
            suppliesMatToFacs = globalHqBean.getAllSuppliesMatToFacs();
            FacesMessage msg = new FacesMessage("SMF Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            suppliesMatToFacs = globalHqBean.getAllSuppliesMatToFacs();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateSuppliesMatToFac((SuppliesMatToFac) event.getObject());
            suppliesMatToFacs = globalHqBean.getAllSuppliesMatToFacs();
            FacesMessage msg = new FacesMessage("SuppliesMatToFac Edited", ((SuppliesMatToFac) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            suppliesMatToFacs = globalHqBean.getAllSuppliesMatToFacs();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((SuppliesMatToFac) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public SuppliesMatToFac getNewSuppliesMatToFac() {
        return newSuppliesMatToFac;
    }

    public void setNewSuppliesMatToFac(SuppliesMatToFac newSuppliesMatToFac) {
        this.newSuppliesMatToFac = newSuppliesMatToFac;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SuppliesMatToFac> getFilteredSuppliesMatToFacs() {
        return filteredSuppliesMatToFacs;
    }

    public void setFilteredSuppliesMatToFacs(List<SuppliesMatToFac> filteredSuppliesMatToFacs) {
        this.filteredSuppliesMatToFacs = filteredSuppliesMatToFacs;
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

    public List<SuppliesMatToFac> getSuppliesMatToFacs() {
        return suppliesMatToFacs;
    }

    public SuppliesMatToFac getSelectedSuppliesMatToFac() {
        return selectedSuppliesMatToFac;
    }

    public void setSelectedSuppliesMatToFac(SuppliesMatToFac selectedSuppliesMatToFac) {
        this.selectedSuppliesMatToFac = selectedSuppliesMatToFac;
    }

    /**
     * Creates a new instance of SuppliesMatToFacManagerBean
     */
    public SuppliesMatToFacManagerBean() {

    }

    public Long getNewSuppliesMatToFacId() {
        return newSuppliesMatToFacId;
    }

    public void setNewSuppliesMatToFacId(Long newSuppliesMatToFacId) {
        this.newSuppliesMatToFacId = newSuppliesMatToFacId;
    }

    public String getFac() {
        return fac;
    }

    public void setFac(String fac) {
        this.fac = fac;
    }

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public String getLotSize() {
        return lotSize;
    }

    public void setLotSize(String lotSize) {
        this.lotSize = lotSize;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(String leadTime) {
        this.leadTime = leadTime;
    }

    public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String supName) {
        this.supName = supName;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public Facility getNewFacility() {
        return newFacility;
    }

    public void setNewFacility(Facility newFacility) {
        this.newFacility = newFacility;
    }

    public List<String> getFacNames() {
        List<String> facName = new ArrayList<String>();
        for (int i = 0; i < facilities.size(); i++) {
            facName.add(i, facilities.get(i).getName());
        }
        return facName;
    }

    public List<String> getMatNames() {
        List<String> matName = new ArrayList<String>();
        for (int i = 0; i < materials.size(); i++) {
            matName.add(i, materials.get(i).getName());
        }
        return matName;
    }

    public List<String> getSupNames() {
        List<String> supName = new ArrayList<String>();
        for (int i = 0; i < suppliers.size(); i++) {
            supName.add(i, suppliers.get(i).getName());
        }
        return supName;
    }

}
