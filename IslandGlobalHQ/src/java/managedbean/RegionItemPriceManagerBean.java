/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Region;
import entity.Item;
import entity.RegionItemPrice;
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
@ManagedBean(name = "regionItemPriceManagerBean")
@ViewScoped
public class RegionItemPriceManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String region;
    private String mat;
    private String regionName;
    private String matName;
    private String priceS;
    private List<Region> regions;
    private List<Item> items;
    private Region newRegion;
    private Long newRegionItemPriceId;
    private String statusMessage;
    private RegionItemPrice selectedRegionItemPrice;
    private List<RegionItemPrice> filteredRegionItemPrices;
    private List<RegionItemPrice> regionItemPrices;
    private RegionItemPrice newRegionItemPrice = new RegionItemPrice();

    @PostConstruct
    public void init() {
        regionItemPrices = globalHqBean.getAllRegionItemPrices();
        regions = globalHqBean.getAllRegions();
        items = globalHqBean.getAllItems();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regions", regions);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("items", items);
    }

    public void saveNewRegionItemPrice(ActionEvent event) {
        Long regionId = Long.valueOf(region);
        Long matId = Long.valueOf(mat);
        Double price = Double.valueOf(priceS);
 
        try {
            newRegionItemPriceId = globalHqBean.addNewRegionItemPrice(regionId, matId, price);
            statusMessage = "New RegionItemPrice Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New RegionItemPrice Result: "
                    + statusMessage + " (New RegionItemPrice ID is " + newRegionItemPriceId + ")", ""));
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newRegionItemPriceId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New RegionItemPrice Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newRegionItemPriceId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New RegionItemPrice Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newRegionItemPriceId = -1L;
            statusMessage = "New RegionItemPrice Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New RegionItemPrice Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteRegionItemPrice() {
        try {
            globalHqBean.removeRegionItemPrice(selectedRegionItemPrice);
            regionItemPrices = globalHqBean.getAllRegionItemPrices();
            FacesMessage msg = new FacesMessage("Item Price Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            regionItemPrices = globalHqBean.getAllRegionItemPrices();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateRegionItemPrice((RegionItemPrice) event.getObject());
            regionItemPrices = globalHqBean.getAllRegionItemPrices();
            FacesMessage msg = new FacesMessage("RegionItemPrice Edited", ((RegionItemPrice) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            regionItemPrices = globalHqBean.getAllRegionItemPrices();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((RegionItemPrice) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public RegionItemPrice getNewRegionItemPrice() {
        return newRegionItemPrice;
    }

    public void setNewRegionItemPrice(RegionItemPrice newRegionItemPrice) {
        this.newRegionItemPrice = newRegionItemPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RegionItemPrice> getFilteredRegionItemPrices() {
        return filteredRegionItemPrices;
    }

    public void setFilteredRegionItemPrices(List<RegionItemPrice> filteredRegionItemPrices) {
        this.filteredRegionItemPrices = filteredRegionItemPrices;
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

    public List<RegionItemPrice> getRegionItemPrices() {
        return regionItemPrices;
    }

    public RegionItemPrice getSelectedRegionItemPrice() {
        return selectedRegionItemPrice;
    }

    public void setSelectedRegionItemPrice(RegionItemPrice selectedRegionItemPrice) {
        this.selectedRegionItemPrice = selectedRegionItemPrice;
    }

    /**
     * Creates a new instance of RegionItemPriceManagerBean
     */
    public RegionItemPriceManagerBean() {

    }

    public Long getNewRegionItemPriceId() {
        return newRegionItemPriceId;
    }

    public void setNewRegionItemPriceId(Long newRegionItemPriceId) {
        this.newRegionItemPriceId = newRegionItemPriceId;
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

    public String getItem() {
        return mat;
    }

    public void setItem(String mat) {
        this.mat = mat;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getItemName() {
        return matName;
    }

    public void setItemName(String matName) {
        this.matName = matName;
    }


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Region getNewRegion() {
        return newRegion;
    }

    public void setNewRegion(Region newRegion) {
        this.newRegion = newRegion;
    }

    public List<String> getRegionNames() {
        List<String> facName = new ArrayList<String>();
        for (int i = 0; i < regions.size(); i++) {
            facName.add(i, regions.get(i).getName());
        }
        return facName;
    }

    public List<String> getItemNames() {
        List<String> matName = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            matName.add(i, items.get(i).getName());
        }
        return matName;
    }

}
