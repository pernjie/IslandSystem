/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Customer;
import entity.Facility;
import entity.Material;
import entity.Region;
import entity.Service;
import entity.ServiceRecord;
import entity.ServiceRecordItem;
import entity.TransactionRecord;
import enumerator.SvcCat;
import enumerator.SvcRecStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.sql.DataSource;
import org.primefaces.event.RowEditEvent;
import services.SvcRecService;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "serviceRecordItemBean")
@SessionScoped
public class ServiceRecordItemBean implements Serializable {

    @Resource(name = "islandFurnitureSystemDataSource")
    private DataSource islandFurnitureSystemDataSource;

    @EJB
    private OpCrmBean ocb;
    private Long id;
    private String custName;
    private String address;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private Date orderTime;
    private Date svcDate;
    private Long storeId;
    private Long custId;
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
    private ServiceRecordItem selectedServiceRecordItem;
    private ServiceRecordItem svcRecItem;
    private SvcCat svcCat;
    private Material material;
    private Boolean notDelivery = true;
    private Service service;
    private Long svcId;
    private Long matId;
    private Integer svcRecItemQty;
    private List<Material> regionMaterials;
    private List<Service> regionServices;
    private SvcRecService svcRecService;
    private List<ServiceRecordItem> serviceRecordItems;
    private List<ServiceRecordItem> filteredServiceRecordItems;
    private Double currentTotalMaterialPrice;
    private Service overallDelivery;

    @PostConstruct
    public void init() {
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        selectedServiceRecord = (ServiceRecord) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedServiceRecord");
        serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);
        stores = ocb.getAllStores();
        customers = ocb.getAllCustomers();
        transactRecs = ocb.getAllTransactionRecords();
        regionMaterials = ocb.getMaterials(userRegion);
        svcRecService = new SvcRecService();
        regionServices = ocb.getNonDeliveryServices(userRegion);
        currentTotalMaterialPrice = getCurrentTotalMaterialPrice();
        overallDelivery = getOverallDeliveryForServiceRecord();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("overallDeliveryForServiceRecord", overallDelivery);
        //System.out.println("CURRENT TOTAL MAT PRICE:" + currentTotalMaterialPrice);
        //System.out.println("Overall Delivery for service record: " + getOverallDeliveryForServiceRecord().getName());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("stores", stores);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("transactRecs", transactRecs);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regionServices", regionServices);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regionMaterials", regionMaterials);

    }

    public Double getCurrentTotalMaterialPrice() {
        return ocb.getTotalMaterialPrice(selectedServiceRecord, userRegion);
    }

    public Service getOverallDeliveryForServiceRecord() {
        Service delivery = ocb.getDeliveryForServiceRecord(userRegion, currentTotalMaterialPrice);
        return delivery;
    }

    public void saveNewServiceRecordItem(ActionEvent event) {
        System.out.println("saveNewServiceRecordItem initiated");
        try {
            System.out.println("selected SVC REC: " + selectedServiceRecord.getId());
            if (svcCat != SvcCat.DELIVERY) {
                newServiceRecordItemId = ocb.addNewServiceRecordItem(svcId, matId, selectedServiceRecord.getId(), svcRecItemQty);
            } else {
                newServiceRecordItemId = ocb.addNewServiceRecordItem(Long.valueOf("0"), matId, selectedServiceRecord.getId(), svcRecItemQty);
            }
            statusMessage = "New Service Price Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ServiceRecord Result: "
                    + statusMessage + " (New ServiceRecord ID is " + newServiceRecordItemId + ")", ""));
            serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
        } catch (Exception ex) {
            serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
            newServiceRecordId = -1L;
            statusMessage = "New Service Price Record Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ServiceRecord Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            ocb.updateServiceRecordItem((ServiceRecordItem) event.getObject());
            serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
            FacesMessage msg = new FacesMessage("ServiceRecord Edited", ((ServiceRecord) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((ServiceRecordItem) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<Material> completeMaterial(String query) {
        List<Material> filteredMaterials = new ArrayList<Material>();

        for (int i = 0; i < regionMaterials.size(); i++) {
            Material skin = regionMaterials.get(i);
            if (skin.getName().toLowerCase().contains(query) || skin.getName().toUpperCase().contains(query)) {
                filteredMaterials.add(skin);
            }
        }

        return filteredMaterials;
    }

    public void deleteServiceRecordItem() {
        try {
            System.out.println("Try delete!");
            ocb.removeServiceRecordItem(selectedServiceRecordItem);
            serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
            FacesMessage msg = new FacesMessage("Service Record Item Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            System.out.println("Catch!");
            serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onCategoryChange() {
        if (svcCat != SvcCat.DELIVERY) {
            notDelivery = true;
        } else {
            notDelivery = false;
        }
    }

    public void addOverallDelivery() {
        ocb.addOverallDelivery(overallDelivery, selectedServiceRecord);
        serviceRecordItems = ocb.getServiceRecordItems(selectedServiceRecord.getId());
    }

    public List<ServiceRecordItem> getServiceRecordItems() {
        return serviceRecordItems;
    }

    public void setServiceRecordItems(List<ServiceRecordItem> serviceRecordItems) {
        this.serviceRecordItems = serviceRecordItems;
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

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getTransactRecId() {
        return transactRecId;
    }

    public Boolean getNotDelivery() {
        return notDelivery;
    }

    public void setNotDelivery(Boolean notDelivery) {
        this.notDelivery = notDelivery;
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

    public ServiceRecordItem getSvcRecItem() {
        return svcRecItem;
    }

    public void setSvcRecItem(ServiceRecordItem svcRecItem) {
        this.svcRecItem = svcRecItem;
    }

    public SvcCat getSvcCat() {
        return svcCat;
    }

    public void setSvcCat(SvcCat svcCat) {
        this.svcCat = svcCat;
    }

    public Long getSvcId() {
        return svcId;
    }

    public void setSvcId(Long svcId) {
        this.svcId = svcId;
    }

    public Long getMatId() {
        return matId;
    }

    public void setMatId(Long matId) {
        this.matId = matId;
    }

    public Integer getSvcRecItemQty() {
        return svcRecItemQty;
    }

    public void setSvcRecItemQty(Integer svcRecItemQty) {
        this.svcRecItemQty = svcRecItemQty;
    }

    public List<Material> getRegionMaterials() {
        return regionMaterials;
    }

    public void setRegionMaterials(List<Material> regionMaterials) {
        this.regionMaterials = regionMaterials;
    }

    public List<Service> getRegionServices() {
        return regionServices;
    }

    public void setRegionServices(List<Service> regionServices) {
        this.regionServices = regionServices;
    }

    public SvcRecService getSvcRecService() {
        return svcRecService;
    }

    public void setSvcRecService(SvcRecService svcRecService) {
        this.svcRecService = svcRecService;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Region getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(Region userRegion) {
        this.userRegion = userRegion;
    }

    public List<ServiceRecordItem> getFilteredServiceRecordItems() {
        return filteredServiceRecordItems;
    }

    public void setFilteredServiceRecordItems(List<ServiceRecordItem> filteredServiceRecordItems) {
        this.filteredServiceRecordItems = filteredServiceRecordItems;
    }

    public ServiceRecordItem getSelectedServiceRecordItem() {
        return selectedServiceRecordItem;
    }

    public void setSelectedServiceRecordItem(ServiceRecordItem selectedServiceRecordItem) {
        this.selectedServiceRecordItem = selectedServiceRecordItem;
    }

}
