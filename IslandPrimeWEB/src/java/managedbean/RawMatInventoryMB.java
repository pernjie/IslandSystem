/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import static com.sun.faces.facelets.util.Path.context;
import entity.InventoryMaterial;
import entity.Item;
import entity.Material;
import java.util.ArrayList;
import java.util.List;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Staff;
import enumerator.InvenLoc;
import java.awt.Event;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
 
import org.primefaces.event.SelectEvent;
import session.stateless.GlobalHqBean;
import session.stateless.InventoryBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;
/**
 *
 * @author Anna
 */
@ManagedBean(name = "rawMatInventoryMB")
@ViewScoped
public class RawMatInventoryMB implements Serializable{
    private Item rawMat;
    private Item tempMat;
    private List<Shelf> zoneShelfEntities;
    private Shelf zoneShelfEntity;
    
    private List<Shelf> shelfEntities;
    private Shelf shelfEntity;
    
    private List<ShelfSlot> shelfSlots;
    private ShelfSlot shelfSlot;
    
    private ShelfType shelfTypeSelected;
    
    private String zon;
    private String shelfValue;
    private String shelfSlotPos;
    private String statusMessage;
    private Double matLengthRes;
    private Double matBreadthRes;
    private Double matHeightRes;
    private Integer resUpperThres;
    private Integer resLowerThres;
    private Integer upperThresValue;
    private Integer upperLowerThresValue;
    private Long newInvenRawMatId;
    
    private InvenLoc loc;

    private List<InventoryMaterial> filteredInvenMat;
    private List<InventoryMaterial> invenMats;
    private InventoryMaterial selectedInvenMat;
    
    @EJB
    private InventoryBean inventoryBean;
    
    
    public RawMatInventoryMB() {
        rawMat = null;
    }
    
    
    @PostConstruct
    public void init() {
        zoneShelfEntities= inventoryBean.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();
        invenMats = inventoryBean.getAllInvenMats();
        resUpperThres = null;
        
        for(Shelf s:zoneShelfEntities)
            System.out.println(s.getZone());
        
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);
        
    } 
    
    public List<Item> completeText(String query) {
        
        List<Item> allRawMats = inventoryBean.getRawMats();
        List<Item> filteredResults = new ArrayList<Item>();
       
        for (Item indiv : allRawMats) {
            if(indiv.getName().toLowerCase().contains(query)) {
                filteredResults.add(indiv);
            }
        }

        return filteredResults;
    }
    
 public void onZoneChange() {
        System.out.println("ZONE: "+zon);
        if(zon !=null && !zon.equals("")){
            shelfEntities = inventoryBean.getShelfEntities(zon);
           System.out.println("SUCCESS");
        }
        else
            shelfEntities = new ArrayList<Shelf>();
    }
    
 public void onShelfChange() throws Exception {
      rawMat = (Item) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("rawMat");
      System.out.println("In FUNCTION");
        System.out.println("SHELF: " +shelfValue);
        if(shelfValue !=null && !shelfValue.equals("")){
            Long shelfNum = Long.valueOf(shelfValue);
            System.out.println(shelfNum);
            shelfSlots = inventoryBean.getShelfSlots(shelfNum);
            shelfTypeSelected = inventoryBean.getShelfType(shelfNum);
            Double slotLength = shelfTypeSelected.getLength();
            Double slotBreadth= shelfTypeSelected.getBreadth();
            Double slotHeight= shelfTypeSelected.getHeight();
              System.out.println("SUCCESS 2");
            System.out.println("SlotLength "+slotLength);
            System.out.println("SlotHeight "+slotHeight);
            System.out.println("SlotBreadth "+slotBreadth);
    
            System.out.println("RAW_MAT" + rawMat);
            try{
                Double rawMatLength = rawMat.getLength();
                Double rawMatBreadth = rawMat.getBreadth();
                Double rawMatHeight = rawMat.getHeight();
                Map<String, Double> finalvalues = new HashMap<String, Double>();

                finalvalues = inventoryBean.calcThresValues(slotLength, slotBreadth, slotHeight, rawMatLength, rawMatBreadth, rawMatHeight);
                matLengthRes = finalvalues.get("lengthUsed");
                matBreadthRes = finalvalues.get("breadthUsed");
                matHeightRes = finalvalues.get("heightUsed");
                Double upperThreshold = finalvalues.get("upperThreshold");
                Double lowerThreshold = finalvalues.get("lowerThreshold");

                resUpperThres = upperThreshold.intValue();
                System.out.println("resUpperThreshold 2: " + resUpperThres);

                resLowerThres = lowerThreshold.intValue();
                System.out.println("resUpperThreshold 2: " + resLowerThres);

                System.out.println("Length to use 2: " + matLengthRes);
                System.out.println("Breadth to use 2: " + matBreadthRes);
                System.out.println("Height to use 2: " + matHeightRes);
                System.out.println("Upper Threshold 2: " + resUpperThres);
                System.out.println("Lower Threshold 2: " + resLowerThres);
            } catch(Exception ex){
               
            }
            
        }
        else
            shelfSlots = new ArrayList<ShelfSlot>(); 
    }
    
   
   public void handleSelect(SelectEvent event) {
         tempMat = (Item) event.getObject();
         System.out.println("HANDLE_SELECTED: "+tempMat.getId());
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rawMat", tempMat);
         System.out.println("HANDLE_SELECTED 2: "+ rawMat);
    }
   
   
    public SelectItem[] getRawMatLocValues() {
    SelectItem[] items = new SelectItem[1];

       InvenLoc il= InvenLoc.getIndex(0);
       items[0] = new SelectItem(il, il.getLabel());
    return items;
  }
    
    public void saveNewInventory(ActionEvent event) {
        System.out.println(rawMat);
        System.out.println(zon);
        System.out.println(shelfValue);
        System.out.println(shelfSlotPos);
        System.out.println(loc);
        
        Long shelfValueLong = Long.valueOf(shelfValue);
        Integer shelfSlotInt = Integer.valueOf(shelfSlotPos);
        
        try {
            newInvenRawMatId = inventoryBean.addNewInventoryRawMat(rawMat, shelfValueLong, shelfSlotInt, loc, zon, resUpperThres, resLowerThres, matLengthRes, matBreadthRes, matHeightRes);
            statusMessage = "New Inventory Raw Mat Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Raw Mat Record Result: "
                    + statusMessage + " (New Inventory Raw Mat Record ID is " + newInvenRawMatId  + ")", ""));
        } catch (Exception ex) {
             newInvenRawMatId  = -1L;
            statusMessage = "New Inventory Raw Mat Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Raw Mat Record Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    } 
    
      public void deleteRawMat() {
        try {
            inventoryBean.removeInvenMat(selectedInvenMat);
            invenMats = inventoryBean.getAllInvenMats();
            FacesMessage msg = new FacesMessage("Raw Material Inventory Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            invenMats = inventoryBean.getAllInvenMats();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }
        
        
    public void onRowEdit(RowEditEvent event) {

        try {
            inventoryBean.updateInvenMat((InventoryMaterial) event.getObject());
             invenMats = inventoryBean.getAllInvenMats();
            FacesMessage msg = new FacesMessage("Raw Material Inventory Record Edited", ((InventoryMaterial) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
             invenMats =  inventoryBean.getAllInvenMats();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }
    
       public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((InventoryMaterial) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

  
     /**
     * Creates a new instance of SuppliesMatToFacManagerBean
     * @return 
     */
   
    public InventoryBean getInventoryBean() {
        return inventoryBean;
    }

    public void setInventoryBean(InventoryBean inventoryBean) {
        this.inventoryBean = inventoryBean;
    }

    public List<Shelf> getZoneShelfEntities() {
        return zoneShelfEntities;
    }

    public void setZoneShelfEntities(List<Shelf> zoneShelfEntities) {
        this.zoneShelfEntities = zoneShelfEntities;
    }

    public Shelf getZoneShelfEntity() {
        return zoneShelfEntity;
    }

    public void setZoneShelfEntity(Shelf zoneShelfEntity) {
        this.zoneShelfEntity = zoneShelfEntity;
    }

    public List<Shelf> getShelfEntities() {
        return shelfEntities;
    }

    public void setShelfEntities(List<Shelf> shelfEntities) {
        this.shelfEntities = shelfEntities;
    }

    public Shelf getShelfEntity() {
        return shelfEntity;
    }

    public void setShelfEntity(Shelf shelfEntity) {
        this.shelfEntity = shelfEntity;
    }
 
    public String getZon() {
        return zon;
    }

    public void setZon(String zon) {
        this.zon = zon;
    }

    public String getShelfValue() {
        return shelfValue;
    }

    public void setShelfValue(String shelfValue) {
        this.shelfValue = shelfValue;
    }

    public List<ShelfSlot> getShelfSlots() {
        return shelfSlots;
    }

    public void setShelfSlots(List<ShelfSlot> shelfSlots) {
        this.shelfSlots = shelfSlots;
    }

    public ShelfSlot getShelfSlot() {
        return shelfSlot;
    }

    public void setShelfSlot(ShelfSlot shelfSlot) {
        this.shelfSlot = shelfSlot;
    }

    public String getShelfSlotPos() {
        return shelfSlotPos;
    }

    public void setShelfSlotPos(String shelfSlotPos) {
        this.shelfSlotPos = shelfSlotPos;
    }

    public ShelfType getShelfTypeSelected() {
        return shelfTypeSelected;
    }

    public void setShelfTypeSelected(ShelfType shelfTypeSelected) {
        this.shelfTypeSelected = shelfTypeSelected;
    }

    public Double getMatLengthRes() {
        return matLengthRes;
    }

    public void setMatLengthRes(Double matLengthRes) {
        this.matLengthRes = matLengthRes;
    }

    public Double getMatBreadthRes() {
        return matBreadthRes;
    }

    public void setMatBreadthRes(Double matBreadthRes) {
        this.matBreadthRes = matBreadthRes;
    }

    public Double getMatHeightRes() {
        return matHeightRes;
    }

    public void setMatHeightRes(Double matHeightRes) {
        this.matHeightRes = matHeightRes;
    }

    public Integer getResUpperThres() {
        return resUpperThres;
    }

    public void setResUpperThres(Integer resUpperThres) {
        this.resUpperThres = resUpperThres;
    }

    public Integer getResLowerThres() {
        return resLowerThres;
    }

    public void setResLowerThres(Integer resLowerThres) {
        this.resLowerThres = resLowerThres;
    }

    public Integer getUpperThresValue() {
        return upperThresValue;
    }

    public void setUpperThresValue(Integer upperThresValue) {
        this.upperThresValue = upperThresValue;
    }

    public Integer getUpperLowerThresValue() {
        return upperLowerThresValue;
    }

    public void setUpperLowerThresValue(Integer upperLowerThresValue) {
        this.upperLowerThresValue = upperLowerThresValue;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public InvenLoc getLoc() {
        return loc;
    }

    public void setLoc(InvenLoc loc) {
        this.loc = loc;
    }

    public Long getNewInvenRawMatId() {
        return newInvenRawMatId;
    }

    public void setNewInvenRawMatId(Long newInvenRawMatId) {
        this.newInvenRawMatId = newInvenRawMatId;
    }

    public Item getRawMat() {
        return rawMat;
    }

    public void setRawMat(Item rawMat) {
        this.rawMat = rawMat;
    }

    public Item getTempMat() {
        return tempMat;
    }

    public void setTempMat(Item tempMat) {
        this.tempMat = tempMat;
    }

    public List<InventoryMaterial> getInvenMats() {
        return invenMats;
    }

    public void setInvenMats(List<InventoryMaterial> invenMats) {
        this.invenMats = invenMats;
    }

    public InventoryMaterial getSelectedInvenMat() {
        return selectedInvenMat;
    }

    public void setSelectedInvenMat(InventoryMaterial selectedInvenMat) {
        this.selectedInvenMat = selectedInvenMat;
    }

    public List<InventoryMaterial> getFilteredInvenMat() {
        return filteredInvenMat;
    }

    public void setFilteredInvenMat(List<InventoryMaterial> filteredInvenMat) {
        this.filteredInvenMat = filteredInvenMat;
    }
}

 