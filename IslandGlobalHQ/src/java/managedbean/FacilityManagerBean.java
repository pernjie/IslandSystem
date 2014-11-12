/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Country;
import entity.Facility;
import entity.Region;
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
import services.FacilityService;
import session.stateless.GlobalHqBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "facilityManagerBean")
@ViewScoped
public class FacilityManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private FacilityService facService;
    private Long id;
    private String name;
    private String address;
    private String postalCode;
    private String city;
    private Country country;
    private List<Country> countries;
    private String regionId;
    private String type;
    private Long newFacilityId;
    private String statusMessage;
    private Facility selectedFacility;
    private List<Facility> filteredFacilities;
    private List<Facility> facilities;
    private List<Region> regions;
    private Facility newFacility = new Facility();

    @PostConstruct
    public void init() {
        facilities = globalHqBean.getAllFacilities();
        regions = globalHqBean.getAllRegions();
        countries = globalHqBean.getAllCountries();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regions", regions);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("countries", countries);
        facService = new FacilityService();
    }

    public void saveNewFacility(ActionEvent event) {

        try {
            Region region = globalHqBean.getRegion(Long.valueOf(regionId));
            newFacility = new Facility();
            newFacility.setName(name);
            newFacility.setAddress(address);
            newFacility.setPostalCode(postalCode);
            newFacility.setCity(city);
            newFacility.setCountry(country);
            newFacility.setRegion(region);
            newFacility.setType(type);
            newFacilityId = globalHqBean.addNewFacility(newFacility);
            statusMessage = "New Facility Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Facility Result: "
                    + statusMessage + " (New Facility ID is " + newFacilityId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newFacilityId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Facility Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newFacilityId = -1L;
            statusMessage = "New Facility Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Facility Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteFacility() {
        try {
            System.out.println("deleteFacility() initiated");
            globalHqBean.removeFacility(selectedFacility);
            facilities = globalHqBean.getAllFacilities();
            FacesMessage msg = new FacesMessage("Facility Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            facilities = globalHqBean.getAllFacilities();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateFacility((Facility) event.getObject());
            facilities = globalHqBean.getAllFacilities();
            FacesMessage msg = new FacesMessage("Facility Edited", ((Facility) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            facilities = globalHqBean.getAllFacilities();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Facility) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public FacilityService getFacService() {
        return facService;
    }

    public List<Region> getRegions() {
        return globalHqBean.getAllRegions();
    }

    
    public Facility getNewFacility() {
        return newFacility;
    }

    public void setNewFacility(Facility newFacility) {
        this.newFacility = newFacility;
    }

    public List<String> getTypes() {
        return facService.getTypes();
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Facility> getFilteredFacilities() {
        return filteredFacilities;
    }

    public void setFilteredFacilities(List<Facility> filteredFacilities) {
        this.filteredFacilities = filteredFacilities;
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

    public List<Facility> getFacilities() {
        return facilities;
    }

    public Facility getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Facility selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    /**
     * Creates a new instance of FacilityManagerBean
     */
    public FacilityManagerBean() {

    }

    public Long getNewFacilityId() {
        return newFacilityId;
    }

    public void setNewFacilityId(Long newFacilityId) {
        this.newFacilityId = newFacilityId;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }


    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
