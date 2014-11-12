/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.InventoryLocation;
import classes.WeekHelper;
import entity.DeliverySchedule;
import entity.Facility;
import entity.InventoryMaterial;
import entity.Item;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import enumerator.InvenLoc;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import session.stateless.InventoryBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author MY ASUS
 */
@ManagedBean(name = "confirmRestockBean")
@ViewScoped
public class ConfirmRestockBean implements Serializable {

    @EJB
    private InventoryBean ib;
    private List<Facility> suppliers;
    private Long supplier;
    private Facility fac;
    private List<DeliverySchedule> piList;
    private List<DeliverySchedule> restockmats;
    private List<InventoryLocation> ilList;
    private final static String[] statuses;
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
    
    static {
        statuses = new String[4];
        statuses[0] = "Unreceived";
        statuses[1] = "Received";
        statuses[2] = "Partial";
        statuses[3] = "Rejected";
        //statuses[4] = "Late";
        //statuses[5] = "Late Received";
        //statuses[6] = "Late Partial";
        //statuses[7] = "Late Rejected";
    }
    
    public ConfirmRestockBean() {
    }
    
    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("confirmrestockbean: init()");
        
        //ilList = restockBean.getIlList();
        zoneShelfEntities= inventoryBean.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();
        invenFurns = inventoryBean.getAllInvenFurns();
        
        resUpperThres = null;
        /*
        for(Shelf s:zoneShelfEntities)
            System.out.println(s.getZone());
        
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);
        */
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
               ex.printStackTrace();
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
    SelectItem[] items = new SelectItem[3];
    int j=0;
    int i;
    for(i=0; i< (InvenLoc.values().length-1); ++i) {
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
            inventoryBean.removeFurn(selectedInvenFurn);
            invenFurns = inventoryBean.getAllInvenFurns();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
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
        
    public List<Facility> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Facility> suppliers) {
        this.suppliers = suppliers;
    }

    public Long getSupplier() {
        return supplier;
    }

    public void setSupplier(Long supplier) {
        this.supplier = supplier;
    }

    public String getMatName(Long matId) {
        return ib.getMatName(matId);
    }

    public List<InventoryLocation> getIlList() {
        return ilList;
    }

    public void setIlList(List<InventoryLocation> ilList) {
        this.ilList = ilList;
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

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void updateInventory() throws IOException {
        System.err.println("updateInventory()");
        ilList = new ArrayList<InventoryLocation>();
        for (DeliverySchedule ds : restockmats) {
            ds.setStatus("Received");
            ib.updateDeliverySchedule(ds);
            //System.err.println("poitem ID: " + pm.getId());
            System.err.println("searching for inventorymaterial..");
            InventoryMaterial mat = new InventoryMaterial();
            InventoryLocation il = new InventoryLocation();
            mat = ib.getInventoryMat(ds.getMat(), fac, InvenLoc.BACKEND_WAREHOUSE);
            System.err.println("fount mat id: " + ds.getMat());
            il.setInvItem(mat.getId());
            il.setQty(ds.getQuantity());
            Integer qty = mat.getQuantity() + ds.getQuantity();
            if (qty <= mat.getUppThreshold()) {
                System.err.println("shelf has enough space for: " + qty);
                mat.setQuantity(qty);
                ib.updateInventory(mat);
                il.setMove(true);
            } else {
                System.err.println("shelf does not have enough space for: " + qty);
                il.setMove(false);
            }
            ilList.add(il);
            System.err.println(ilList.size());
        }
        System.err.println("inventory update complete.");  
        if(ilList.isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../scm/scm_home.xhtml");
        }
        else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../scm/scm_view_restock_location.xhtml");
        }
    }
    
    
}
