/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import static com.sun.faces.facelets.util.Path.context;
import entity.Facility;
import entity.InventoryMaterial;
import entity.Item;
import entity.Material;
import entity.Product;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import session.stateless.CIBeanLocal;
import session.stateless.InventoryBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;
/**
 *
 * @author Anna
 */
@ManagedBean(name = "furnInventoryMB")
@ViewScoped
public class FurnInventoryMB implements Serializable{
    private Item furn;
    private Item tempFurn;
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
    private Double furnLengthRes;
    private Double furnBreadthRes;
    private Double furnHeightRes;
    private Integer resUpperThres;
    private Integer resLowerThres;
    private Integer upperThresValue;
    private Integer upperLowerThresValue;
    private Long newInvenFurnId;
    
    private InvenLoc loc;
    
    private InventoryMaterial selectedInvenFurn;
    private InventoryMaterial filteredInvenFurn;
    private List<InventoryMaterial> invenFurns;
    
    @EJB
    private InventoryBean inventoryBean;
    @EJB
    private CIBeanLocal cib;
    
    @PostConstruct
    public void init() {
        zoneShelfEntities= inventoryBean.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();
        invenFurns = inventoryBean.getAllInvenFurns();
        
        resUpperThres = null;
        
        for(Shelf s:zoneShelfEntities)
            System.out.println(s.getZone());
        
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);
        
    } 
    
    public List<Item> completeText(String query) {
        List<Item> allFurns = inventoryBean.getFurnitures();
        List<Item> filteredResults = new ArrayList<Item>();
       
        for (Item indiv : allFurns) {
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
      furn = (Item) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("furn");
      //furn.getClass().getSimpleName();
      //furn instanceof Material;
              
      //if(furn instanceof Material) {
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
    
            System.out.println("FURN" + furn);
            try{
                Double furnLength = furn.getLength();
                Double furnBreadth = furn.getBreadth();
                Double furnHeight = furn.getHeight();
                Map<String, Double> finalvalues = new HashMap<String, Double>();

                finalvalues = inventoryBean.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
                furnLengthRes = finalvalues.get("lengthUsed");
                furnBreadthRes = finalvalues.get("breadthUsed");
                furnHeightRes = finalvalues.get("heightUsed");
                Double upperThreshold = finalvalues.get("upperThreshold");
                Double lowerThreshold = finalvalues.get("lowerThreshold");

                resUpperThres = upperThreshold.intValue();
                System.out.println("resUpperThreshold 2: " + resUpperThres);

                resLowerThres = lowerThreshold.intValue();
                System.out.println("resUpperThreshold 2: " + resLowerThres);

                System.out.println("Length to use 2: " + furnLengthRes);
                System.out.println("Breadth to use 2: " + furnBreadthRes);
                System.out.println("Height to use 2: " + furnHeightRes);
                System.out.println("Upper Threshold 2: " + resUpperThres);
                System.out.println("Lower Threshold 2: " + resLowerThres);
            } catch(Exception ex){
               
            }
            
        }
        else
            shelfSlots = new ArrayList<ShelfSlot>(); 
    }
    
   
   public void handleSelect(SelectEvent event) {
         tempFurn = (Item) event.getObject();
         System.out.println("HANDLE_SELECTED: "+tempFurn.getId());
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("furn", tempFurn);
         System.out.println("HANDLE_SELECTED 2: "+ furn);
    }
   
   
    public SelectItem[] getFurnLocValues() {
  
        Facility fac = (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
         SelectItem[] items=null;
        if (fac.getType().equals("Manufacturing")) {
            items = new SelectItem[1];
            items[0] = new SelectItem(InvenLoc.getIndex(0), InvenLoc.getIndex(0).getLabel());
        }
        else if (fac.getType().equals("Store")) 
            
            items = new SelectItem[4];
            int j=0;
            int i;
            for(i=1; i<5; i++) {
                System.out.println("arr =" + i);
                InvenLoc il= InvenLoc.getIndex(i);
               items[j++] = new SelectItem(il, il.getLabel());
            }
    return items;
  }
    
    public void saveNewInventory(ActionEvent event) {
        System.out.println(furn);
        System.out.println(zon);
        System.out.println(shelfValue);
        System.out.println(shelfSlotPos);
        System.out.println(loc);
        
        Long shelfValueLong = Long.valueOf(shelfValue);
        Integer shelfSlotInt = Integer.valueOf(shelfSlotPos);
        
        try {
            newInvenFurnId = inventoryBean.addNewInventoryRawMat(furn, shelfValueLong, shelfSlotInt, loc, zon, resUpperThres, resLowerThres, furnLengthRes, furnBreadthRes, furnHeightRes);
            statusMessage = "New Inventory Furniture Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Furniture Record Result: "
                    + statusMessage + " (New Inventory Furniture Record ID is " + newInvenFurnId  + ")", ""));
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Added New Inventory Record (Furniture): " + newInvenFurnId);
            setFurn(null);
            setZon(null);
            setShelfValue(null);
            setShelfSlotPos(null);
            setLoc(null);
            
            
        } catch (Exception ex) {
             newInvenFurnId  = -1L;
            statusMessage = "New Inventory Furniture Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Furniture Record Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    } 
    
     public void deleteInvenFurn() {
        try {
            Long furnId = selectedInvenFurn.getId();
            inventoryBean.removeFurn(selectedInvenFurn);
            invenFurns = inventoryBean.getAllInvenFurns();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Inventory Record (Furniture): " + furnId);
        } catch (ReferenceConstraintException ex) {
            invenFurns = inventoryBean.getAllInvenFurns();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }
     
         
    public void onRowEdit(RowEditEvent event) {   
        try {
            inventoryBean.updateInvenMat((InventoryMaterial) event.getObject());
            invenFurns = inventoryBean.getAllInvenFurns();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Edited", ((InventoryMaterial) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
             invenFurns = inventoryBean.getAllInvenFurns();
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
     */
    public FurnInventoryMB() {

    }
   
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

    public Double getFurnLengthRes() {
        return furnLengthRes;
    }

    public void setFurnLengthRes(Double furnLengthRes) {
        this.furnLengthRes = furnLengthRes;
    }

    public Double getFurnBreadthRes() {
        return furnBreadthRes;
    }

    public void setFurnBreadthRes(Double furnBreadthRes) {
        this.furnBreadthRes = furnBreadthRes;
    }

    public Double getFurnHeightRes() {
        return furnHeightRes;
    }

    public void setFurnHeightRes(Double furnHeightRes) {
        this.furnHeightRes = furnHeightRes;
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

    public Long getNewInvenFurnId() {
        return newInvenFurnId;
    }

    public void setNewInvenFurnId(Long newInvenFurnId) {
        this.newInvenFurnId = newInvenFurnId;
    }

    public Item getFurn() {
        return furn;
    }

    public void setFurn(Item furn) {
        this.furn = furn;
    }

    public Item getTempFurn() {
        return tempFurn;
    }

    public void setTempFurn(Item tempFurn) {
        this.tempFurn = tempFurn;
    }

    public InventoryMaterial getSelectedInvenFurn() {
        return selectedInvenFurn;
    }

    public void setSelectedInvenFurn(InventoryMaterial selectedInvenFurn) {
        this.selectedInvenFurn = selectedInvenFurn;
    }

    public List<InventoryMaterial> getInvenFurns() {
        return invenFurns;
    }

    public void setInvenFurns(List<InventoryMaterial> invenFurns) {
        this.invenFurns = invenFurns;
    }

    public InventoryMaterial getFilteredInvenFurn() {
        return filteredInvenFurn;
    }

    public void setFilteredInvenFurn(InventoryMaterial filteredInvenFurn) {
        this.filteredInvenFurn = filteredInvenFurn;
    }

    
}

 