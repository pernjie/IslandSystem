/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Customer;
import entity.Facility;
import entity.ServiceRecord;
import entity.Staff;
import entity.TransactionRecord;
import enumerator.SvcCat;
import enumerator.SvcRecStatus;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
import services.SvcRecService;
import session.stateless.CIBeanLocal;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "serviceRecordBean")
@ViewScoped
public class ServiceRecordBean implements Serializable {

    @EJB
    private OpCrmBean ocb;
    @EJB
    private CIBeanLocal cib;
    private Long id;
    private String custName;
    private String address;
    private String userEmail;
    private Facility userFacility;
    private Date orderTime;
    private Date svcDate;
    private Long storeId;
    private Long transactRecId;
    private SvcRecStatus status;
    private List<Facility> stores;
    private List<Customer> customers;
    private List<TransactionRecord> transactRecs;
    private Facility newFacility;
    private Long newServiceRecordId;
    private Long newServiceRecordItemId;
    private String statusMessage;
    private ServiceRecord selectedServiceRecord;
    private List<ServiceRecord> filteredServiceRecords;
    private List<ServiceRecord> serviceRecords;
    private ServiceRecord newServiceRecord = new ServiceRecord();
    private SvcRecService svcRecService;

    @PostConstruct
    public void init() {
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = ocb.getUserFacility(userEmail);
        serviceRecords = ocb.getServiceRecords(userFacility);
        stores = ocb.getAllStores();
        customers = ocb.getAllCustomers();
        transactRecs = ocb.getAllTransactionRecords();
        svcRecService = new SvcRecService();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("customers", customers);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("stores", stores);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("transactRecs", transactRecs);
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("storeServices", storeServices);
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("storeMaterials", storeMaterials);

    }

    public void saveNewServiceRecord(ActionEvent event) {
        Boolean persisted = false;
        try {
            selectedServiceRecord = ocb.addNewServiceRecord(custName, address, svcDate, userFacility.getId());
            statusMessage = "New Service Price Record Saved Successfully.";
            persisted = true;

            if (persisted) {
                try {
                    Long servId = selectedServiceRecord.getId();
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedServiceRecord", selectedServiceRecord);
                    FacesContext.getCurrentInstance().getExternalContext().redirect("op_add_item_to_selected_service_record.xhtml");
                    Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
                    cib.addLog(staff, "Added New Service Record: " + servId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                FacesMessage msg = new FacesMessage("Eror", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception ex) {
            newServiceRecordId = -1L;
            statusMessage = "New Service Price Record Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ServiceRecord Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteServiceRecord() {
        try {
            Long servId = selectedServiceRecord.getId();
            ocb.removeServiceRecord(selectedServiceRecord);
            serviceRecords = ocb.getServiceRecords(userFacility);
            FacesMessage msg = new FacesMessage("Service Price Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Service Record: " + servId);
            
        } catch (ReferenceConstraintException ex) {
            serviceRecords = ocb.getServiceRecords(userFacility);
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void viewSelectedServiceRecord() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedServiceRecord", selectedServiceRecord);
            FacesContext.getCurrentInstance().getExternalContext().redirect("op_add_new_store_service_record.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            ocb.updateServiceRecord((ServiceRecord) event.getObject());
            serviceRecords = ocb.getServiceRecords(userFacility);
            FacesMessage msg = new FacesMessage("ServiceRecord Edited", ((ServiceRecord) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            serviceRecords = ocb.getServiceRecords(userFacility);
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((ServiceRecord) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Long getNewServiceRecordItemId() {
        return newServiceRecordItemId;
    }

    public void setNewServiceRecordItemId(Long newServiceRecordItemId) {
        this.newServiceRecordItemId = newServiceRecordItemId;
    }

    public List<SvcCat> getCategories() {
        return svcRecService.getCategories();
    }

    public OpCrmBean getOcb() {
        return ocb;
    }

    public void setOcb(OpCrmBean ocb) {
        this.ocb = ocb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getSvcDate() {
        return svcDate;
    }

    public void setSvcDate(Date svcDate) {
        this.svcDate = svcDate;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }


    public Long getTransactRecId() {
        return transactRecId;
    }

    public void setTransactRecId(Long transactRecId) {
        this.transactRecId = transactRecId;
    }

    public SvcRecStatus getStatus() {
        return status;
    }

    public void setStatus(SvcRecStatus status) {
        this.status = status;
    }

    public List<Facility> getStores() {
        return stores;
    }

    public void setStores(List<Facility> stores) {
        this.stores = stores;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<TransactionRecord> getTransactRecs() {
        return transactRecs;
    }

    public void setTransactRecs(List<TransactionRecord> transactRecs) {
        this.transactRecs = transactRecs;
    }

    public Facility getNewFacility() {
        return newFacility;
    }

    public void setNewFacility(Facility newFacility) {
        this.newFacility = newFacility;
    }

    public Long getNewServiceRecordId() {
        return newServiceRecordId;
    }

    public void setNewServiceRecordId(Long newServiceRecordId) {
        this.newServiceRecordId = newServiceRecordId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public ServiceRecord getSelectedServiceRecord() {
        return selectedServiceRecord;
    }

    public void setSelectedServiceRecord(ServiceRecord selectedServiceRecord) {
        this.selectedServiceRecord = selectedServiceRecord;
    }

    public List<ServiceRecord> getFilteredServiceRecords() {
        return filteredServiceRecords;
    }

    public void setFilteredServiceRecords(List<ServiceRecord> filteredServiceRecords) {
        this.filteredServiceRecords = filteredServiceRecords;
    }

    public List<ServiceRecord> getServiceRecords() {
        return serviceRecords;
    }

    public void setServiceRecords(List<ServiceRecord> serviceRecords) {
        this.serviceRecords = serviceRecords;
    }

    public ServiceRecord getNewServiceRecord() {
        return newServiceRecord;
    }

    public void setNewServiceRecord(ServiceRecord newServiceRecord) {
        this.newServiceRecord = newServiceRecord;
    }

    public SvcRecService getSvcRecService() {
        return svcRecService;
    }

    public void setSvcRecService(SvcRecService svcRecService) {
        this.svcRecService = svcRecService;
    }

}
