/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Region;
import entity.Service;
import entity.RegionServicePrice;
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
@ManagedBean(name = "regionServicePriceManagerBean")
@ViewScoped
public class RegionServicePriceManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String region;
    private String svc;
    private String regionName;
    private String svcName;
    private String priceS;
    private List<Region> regions;
    private List<Service> services;
    private Region newRegion;
    private Long newServicePriceId;
    private String statusMessage;
    private RegionServicePrice selectedServicePrice;
    private List<RegionServicePrice> filteredServicePrices;
    private List<RegionServicePrice> servicePrices;
    private RegionServicePrice newServicePrice = new RegionServicePrice();

    @PostConstruct
    
    public void init() {
        servicePrices = globalHqBean.getAllRegionServicePrices();
        regions = globalHqBean.getAllRegions();
        services = globalHqBean.getAllServices();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regions", regions);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("services", services);
    }

    
    public void saveNewRegionServicePrice(ActionEvent event) {
        Long regionId = Long.valueOf(region);
        Long svcId = Long.valueOf(svc);
        Double price = Double.valueOf(priceS);
 
        try {
            newServicePriceId = globalHqBean.addNewRegionServicePrice(regionId, svcId, price);
            statusMessage = "New Service Price Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ServicePrice Result: "
                    + statusMessage + " (New ServicePrice ID is " + newServicePriceId + ")", ""));
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newServicePriceId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New ServicePrice Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newServicePriceId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New ServicePrice Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newServicePriceId = -1L;
            statusMessage = "New Service Price Record Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ServicePrice Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    
    public void deleteRegionServicePrice() {
        try {
            globalHqBean.removeRegionServicePrice(selectedServicePrice);
            servicePrices = globalHqBean.getAllRegionServicePrices();
            FacesMessage msg = new FacesMessage("Service Price Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            servicePrices = globalHqBean.getAllRegionServicePrices();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    
    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateRegionServicePrice((RegionServicePrice) event.getObject());
            servicePrices = globalHqBean.getAllRegionServicePrices();
            FacesMessage msg = new FacesMessage("ServicePrice Edited", ((RegionServicePrice) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            servicePrices = globalHqBean.getAllRegionServicePrices();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((RegionServicePrice) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    
    public RegionServicePrice getNewRegionServicePrice() {
        return newServicePrice;
    }

    
    public void setNewRegionServicePrice(RegionServicePrice newServicePrice) {
        this.newServicePrice = newServicePrice;
    }

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public List<RegionServicePrice> getFilteredRegionServicePrices() {
        return filteredServicePrices;
    }

    
    public void setFilteredRegionServicePrices(List<RegionServicePrice> filteredServicePrices) {
        this.filteredServicePrices = filteredServicePrices;
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

    
    public List<RegionServicePrice> getRegionServicePrices() {
        return servicePrices;
    }

    
    public RegionServicePrice getSelectedRegionServicePrice() {
        return selectedServicePrice;
    }

    
    public void setSelectedRegionServicePrice(RegionServicePrice selectedServicePrice) {
        this.selectedServicePrice = selectedServicePrice;
    }

    /**
     * Creates a new instance of ServicePriceManagerBean
     */
    public RegionServicePriceManagerBean() {

    }

    
    public Long getNewServicePriceId() {
        return newServicePriceId;
    }

    
    public void setNewServicePriceId(Long newServicePriceId) {
        this.newServicePriceId = newServicePriceId;
    }

    
    public String getRegion() {
        return region;
    }

    
    public void setRegion(String region) {
        this.region = region;
    }

    
    public List<Region> getRegions() {
        return regions;
    }

    
    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    
    public String getPriceS() {
        return priceS;
    }

    
    public void setPriceS(String priceS) {
        this.priceS = priceS;
    }

    
    public String getSvc() {
        return svc;
    }

    
    public void setSvc(String svc) {
        this.svc = svc;
    }

    
    public String getRegionName() {
        return regionName;
    }

    
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    
    public String getSvcName() {
        return svcName;
    }

    
    public void setSvcName(String svcName) {
        this.svcName = svcName;
    }

    
    public List<Service> getServices() {
        return services;
    }

    
    public void setServices(List<Service> services) {
        this.services = services;
    }

    
    public Region getNewRegion() {
        return newRegion;
    }

    
    public void setNewRegion(Region newRegion) {
        this.newRegion = newRegion;
    }

    
    public List<String> getFacNames() {
        List<String> regionName = new ArrayList<String>();
        for (int i = 0; i < regions.size(); i++) {
            regionName.add(i, regions.get(i).getName());
        }
        return regionName;
    }

    
    public List<String> getSvcNames() {
        List<String> svcName = new ArrayList<String>();
        for (int i = 0; i < services.size(); i++) {
            svcName.add(i, services.get(i).getName());
        }
        return svcName;
    }

}
