/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Supplier;
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
@ManagedBean(name = "supplierManagerBean")
@ViewScoped
public class SupplierManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String name;
    private String address;
    private String contactNumber;
    private String contactEmail;
    private Long newSupplierId;
    private String statusMessage;
    private Supplier selectedSupplier;
    private List<Supplier> filteredSuppliers;
    private List<Supplier> suppliers;
    private Supplier newSupplier = new Supplier();

    @PostConstruct
    public void init() {
        suppliers = globalHqBean.getAllSuppliers();
    }

    public Supplier getNewSupplier() {
        return newSupplier;
    }

    public void setNewSupplier(Supplier newSupplier) {
        this.newSupplier = newSupplier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Supplier> getFilteredSuppliers() {
        return filteredSuppliers;
    }

    public void setFilteredSuppliers(List<Supplier> filteredSuppliers) {
        this.filteredSuppliers = filteredSuppliers;
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

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    /**
     * Creates a new instance of SupplierManagerBean
     */
    public SupplierManagerBean() {

    }

    public Long getNewSupplierId() {
        return newSupplierId;
    }

    public void setNewSupplierId(Long newSupplierId) {
        this.newSupplierId = newSupplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void saveNewSupplier(ActionEvent event) {
        try {
            newSupplier = new Supplier();
            newSupplier.setName(name);
            newSupplier.setContactEmail(contactEmail);
            newSupplier.setAddress(address);
            newSupplier.setContactNumber(contactNumber);
            newSupplierId = globalHqBean.addNewSupplier(newSupplier);
            statusMessage = "New Supplier Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Supplier Result: "
                    + statusMessage + " (New Supplier ID is " + newSupplierId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newSupplierId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Supplier Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newSupplierId = -1L;
            statusMessage = "New Supplier Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Supplier Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteSupplier() {
        try {
            globalHqBean.removeSupplier(selectedSupplier);
            suppliers = globalHqBean.getAllSuppliers();
            FacesMessage msg = new FacesMessage("Supplier Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            suppliers = globalHqBean.getAllSuppliers();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateSupplier((Supplier) event.getObject());
            suppliers = globalHqBean.getAllSuppliers();
            FacesMessage msg = new FacesMessage("Supplier Edited", ((Supplier) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            suppliers = globalHqBean.getAllSuppliers();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Supplier) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
