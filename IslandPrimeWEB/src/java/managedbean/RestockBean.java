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
import entity.InventoryProduct;
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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
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
@ManagedBean(name = "restockBean")
@SessionScoped
public class RestockBean implements Serializable {

    @EJB
    private InventoryBean ib;
    private String loggedInEmail;
    private List<Facility> suppliers;
    private String supplier;
    private Date currDate;
    private Facility fac;
    private Facility sup;
    private List<DeliverySchedule> piList;
    private List<DeliverySchedule> restockmats;
    private List<InventoryLocation> ilList;
    private final static String[] statuses;
    private WeekHelper wh = new WeekHelper();

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
    private List<Facility> matSuppliers;
    private List<Facility> prodSuppliers;

    static {
        statuses = new String[4];
        statuses[0] = "Received";
        statuses[1] = "Partial";
        statuses[2] = "Rejected";
        statuses[3] = "Cancelled";
        //statuses[4] = "Late";
        //statuses[5] = "Late Received";
        //statuses[6] = "Late Partial";
        //statuses[7] = "Late Rejected";
    }

    public RestockBean() {
    }

    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("restockbean: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = ib.getFac(loggedInEmail);
        try {
            suppliers = ib.getMatFacilities(fac);
            suppliers.addAll(ib.getProdFacilities(fac));
        //matSuppliers = ib.getMatFacilities(fac);
            //prodSuppliers = ib.getProdFacilities(fac);
        } catch (Exception ex) {
            statusMessage = "No MF distributor found.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Receive Inventory from MF Result: "
                    + statusMessage, ""));

        }
        if (suppliers.isEmpty()) {
            System.err.println("no suppliers found!");
        }
        currDate = wh.getCurrDate();

        zoneShelfEntities = ib.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();
        invenFurns = ib.getAllInvenFurns();

        resUpperThres = null;

        for (Shelf s : zoneShelfEntities) {
            System.out.println(s.getZone());
        }

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);

    }

    public List<Item> completeText(String query) {
        List<Item> allFurns = ib.getFurnitures();
        List<Item> filteredResults = new ArrayList<Item>();

        for (Item indiv : allFurns) {
            if (indiv.getName().toLowerCase().contains(query)) {
                filteredResults.add(indiv);
            }
        }

        return filteredResults;
    }

    public void onZoneChange() {
        System.out.println("ZONE: " + zon);
        if (zon != null && !zon.equals("")) {
            shelfEntities = ib.getShelfEntities(zon);
            System.out.println("SUCCESS");
        } else {
            shelfEntities = new ArrayList<Shelf>();
        }
    }

    public void onShelfChange() throws Exception {
        furn = (Item) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("furn");
      //furn.getClass().getSimpleName();
        //furn instanceof Material;

        //if(furn instanceof Material) {
        System.out.println("In FUNCTION");
        System.out.println("SHELF: " + shelfValue);
        if (shelfValue != null && !shelfValue.equals("")) {
            Long shelfNum = Long.valueOf(shelfValue);
            System.out.println(shelfNum);
            shelfSlots = ib.getShelfSlots(shelfNum);
            shelfTypeSelected = ib.getShelfType(shelfNum);
            Double slotLength = shelfTypeSelected.getLength();
            Double slotBreadth = shelfTypeSelected.getBreadth();
            Double slotHeight = shelfTypeSelected.getHeight();
            System.out.println("SUCCESS 2");
            System.out.println("SlotLength " + slotLength);
            System.out.println("SlotHeight " + slotHeight);
            System.out.println("SlotBreadth " + slotBreadth);

            System.out.println("FURN" + furn);
            try {
                Double furnLength = furn.getLength();
                Double furnBreadth = furn.getBreadth();
                Double furnHeight = furn.getHeight();
                Map<String, Double> finalvalues = new HashMap<String, Double>();

                finalvalues = ib.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
            } catch (Exception ex) {

            }

        } else {
            shelfSlots = new ArrayList<ShelfSlot>();
        }
    }

    public void handleSelect(SelectEvent event) {
        tempFurn = (Item) event.getObject();
        System.out.println("HANDLE_SELECTED: " + tempFurn.getId());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("furn", tempFurn);
        System.out.println("HANDLE_SELECTED 2: " + furn);
    }

    public SelectItem[] getFurnLocValues() {
        SelectItem[] items = new SelectItem[3];
        int j = 0;
        int i;
        for (i = 0; i < (InvenLoc.values().length - 1); ++i) {
            InvenLoc il = InvenLoc.getIndex(i);
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
            newInvenFurnId = ib.addNewInventoryRawMat(furn, shelfValueLong, shelfSlotInt, loc, zon, resUpperThres, resLowerThres, furnLengthRes, furnBreadthRes, furnHeightRes);
            statusMessage = "New Inventory Furniture Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Furniture Record Result: "
                    + statusMessage + " (New Inventory Furniture Record ID is " + newInvenFurnId + ")", ""));
        } catch (Exception ex) {
            newInvenFurnId = -1L;
            statusMessage = "New Inventory Furniture Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Furniture Record Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteInvenFurn() {
        try {
            ib.removeFurn(selectedInvenFurn);
            invenFurns = ib.getAllInvenFurns();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            invenFurns = ib.getAllInvenFurns();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {
        try {
            ib.updateInvenMat((InventoryMaterial) event.getObject());
            invenFurns = ib.getAllInvenFurns();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Edited", ((InventoryMaterial) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            invenFurns = ib.getAllInvenFurns();
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

    public String[] getStatuses() {
        return statuses;
    }

    public List<Facility> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Facility> suppliers) {
        this.suppliers = suppliers;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getMatName(Long matId) {
        return ib.getMatName(matId);
    }

    public List<DeliverySchedule> getPiList() {
        return piList;
    }

    public void setPiList(List<DeliverySchedule> piList) {
        this.piList = piList;
    }

    public List<DeliverySchedule> getRestockmats() {
        return restockmats;
    }

    public void setRestockmats(List<DeliverySchedule> restockmats) {
        this.restockmats = restockmats;
    }

    public String dateString(Date date) {
        return wh.getDateString(date);
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

    public List<Facility> getMatSuppliers() {
        return matSuppliers;
    }

    public void setMatSuppliers(List<Facility> matSuppliers) {
        this.matSuppliers = matSuppliers;
    }

    public List<Facility> getProdSuppliers() {
        return prodSuppliers;
    }

    public void setProdSuppliers(List<Facility> prodSuppliers) {
        this.prodSuppliers = prodSuppliers;
    }

    public void updateTable() {
        System.err.println("function: updateTable");
        sup = ib.getFacility(Long.valueOf(supplier));
        //System.err.println("supplier: " + supplier);
        piList = ib.getDeliveryItem(fac, sup);
        if (piList.isEmpty()) {
            System.err.println("no delivery schedule found.");
        } else {
            System.err.println("delivery schedule found.");
        }
        restockmats = piList;
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
            ib.updateDeliverySchedule(ds);
            if (ds.getStatus().equals("Received") || ds.getStatus().equals("Partial")) {
                InventoryLocation il = new InventoryLocation();
                if (ib.getItemType(ds.getMat()) == 1) {
                    System.err.println("searching for inventorymaterial..");
                    InventoryMaterial mat = new InventoryMaterial();
                    Item furn = ds.getMat();
                    System.err.println("mat id:" + furn);
                    try {
                        mat = ib.getInventoryMat(furn, fac, InvenLoc.MF);
                        System.err.println("found invmat id: " + mat);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (mat == null) {
                        mat = new InventoryMaterial();
                        mat.setMat(furn);
                        System.err.println("new mat:" + furn);
                    }
                    else {
                        shelfSlot = ib.getAvailableShelfSlot(fac, InvenLoc.BACKEND_WAREHOUSE);
                        if (shelfSlot == null) {
                            il.setMove(false);
                        } else {
                            mat.setShelfSlot(shelfSlot);
                            shelfSlot.setOccupied(Boolean.TRUE);
                            mat.setShelf(shelfSlot.getShelf());
                            mat.setLocation(InvenLoc.BACKEND_WAREHOUSE);
                            ShelfType shelfType = shelfSlot.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = ib.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            mat.setLowThreshold(resUpperThres);
                            mat.setUppThreshold(resLowerThres);
                            mat.setMatBreadth(furnBreadthRes);
                            mat.setMatHeight(furnHeightRes);
                            mat.setMatLength(furnLengthRes);
                        }
                    }
                    il.setInvItem(mat.getId());
                    il.setItemType(1);
                    il.setQty(ds.getQuantity());
                    Integer qty = mat.getQuantity() + ds.getQuantity();
                    if (qty <= mat.getUppThreshold()) {
                        System.err.println("shelf has enough space for: " + qty);
                        mat.setQuantity(qty);
                        ib.updateInventoryMat(mat);
                        il.setMove(true);
                    } else if (qty > mat.getUppThreshold()) {
                        System.err.println("threshold exceeded");
                        InventoryMaterial mat2 = new InventoryMaterial();
                        mat2.setFac(fac);
                        mat2.setMat(mat.getMat());
                        ShelfSlot shelfSlot2 = new ShelfSlot();
                        shelfSlot2 = ib.getAvailableShelfSlot(mat.getId(), fac, InvenLoc.BACKEND_WAREHOUSE);
                        if (shelfSlot2 == null) {
                            shelfSlot2 = ib.getOtherShelfSlot(mat.getId(), fac, InvenLoc.BACKEND_WAREHOUSE);
                        }
                        if (shelfSlot2 == null) {
                            System.err.println("no shelfslot available");
                            il.setMove(false);
                        } else {
                            System.err.println("shelfslot position: " + shelfSlot2.getPosition());
                            mat2.setShelfSlot(shelfSlot2);
                            shelfSlot2.setOccupied(Boolean.TRUE);
                            mat2.setShelf(shelfSlot2.getShelf());
                            System.err.println("shelf: " + shelfSlot2.getShelf().getShelf());
                            mat2.setLocation(InvenLoc.BACKEND_WAREHOUSE);
                            ShelfType shelfType = shelfSlot2.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = ib.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            mat2.setLowThreshold(resUpperThres);
                            mat2.setUppThreshold(resLowerThres);
                            mat2.setMatBreadth(furnBreadthRes);
                            mat2.setMatHeight(furnHeightRes);
                            mat2.setMatLength(furnLengthRes);

                            il.setQty(mat.getUppThreshold() - mat.getQuantity());
                            if ((mat.getUppThreshold() - mat.getQuantity()) > 0) {
                                il.setMove(true);
                            }
                            //ilList.add(il);
                            mat.setQuantity(mat.getUppThreshold());
                            mat2.setQuantity(qty - mat.getUppThreshold());
                            ib.updateInventoryMat(mat);
                            ib.persistInventoryMaterial(mat2);
                            InventoryLocation il2 = new InventoryLocation();
                            il2.setInvItem(mat2.getId());
                            il2.setItemType(1);
                            il2.setQty(mat2.getQuantity());
                            il2.setMove(true);
                            ilList.add(il2);
                        }
                    }
                } else if (ib.getItemType(ds.getMat()) == 2) {
                    System.err.println("searching for inventoryproduct..");
                    InventoryProduct prod = new InventoryProduct();
                    Item furn2 = ds.getMat();
                    System.err.println("prod id:" + furn2);
                    try {
                        prod = ib.getInventoryProd(furn2, fac, InvenLoc.BACKEND_WAREHOUSE);
                        System.err.println("found invmat id: " + prod);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (prod == null) {
                        System.err.println("new mat:" + prod);
                        ShelfSlot shelfSlot = new ShelfSlot();
                        shelfSlot = ib.getAvailableShelfSlot(fac, InvenLoc.BACKEND_WAREHOUSE);
                        if (shelfSlot == null) {
                            il.setMove(false);
                        } else {
                            prod.setShelfSlot(shelfSlot);
                            shelfSlot.setOccupied(Boolean.TRUE);
                            prod.setShelf(shelfSlot.getShelf());
                            prod.setLocation(InvenLoc.BACKEND_WAREHOUSE);
                            ShelfType shelfType = shelfSlot.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn2.getLength();
                                Double furnBreadth = furn2.getBreadth();
                                Double furnHeight = furn2.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = ib.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            prod.setLowThreshold(resUpperThres);
                            prod.setUppThreshold(resLowerThres);
                            prod.setPdtBreadth(furnBreadthRes);
                            prod.setPdtHeight(furnHeightRes);
                            prod.setPdtLength(furnLengthRes);
                        }
                    }
                    il.setInvItem(prod.getId());
                    il.setItemType(2);
                    il.setQty(ds.getQuantity());
                    Integer qty = prod.getQuantity() + ds.getQuantity();
                    if (qty <= prod.getUppThreshold()) {
                        System.err.println("shelf has enough space for: " + qty);
                        prod.setQuantity(qty);
                        ib.updateInventoryProd(prod);
                        il.setMove(true);
                    } else if (qty > prod.getUppThreshold()) {
                        InventoryProduct prod2 = new InventoryProduct();
                        prod2.setFac(fac);
                        prod2.setProd(prod.getProd());
                        ShelfSlot shelfSlot3 = new ShelfSlot();
                        shelfSlot3 = ib.getAvailableShelfSlot(prod.getId(), fac, InvenLoc.BACKEND_WAREHOUSE);
                        if (shelfSlot3 == null) {
                            shelfSlot3 = ib.getOtherShelfSlot(prod.getId(), fac, InvenLoc.BACKEND_WAREHOUSE);
                        }
                        if (shelfSlot3 == null) {
                            il.setMove(false);
                        } else {
                            prod2.setShelfSlot(shelfSlot3);
                            prod2.setShelf(shelfSlot3.getShelf());
                            prod2.setLocation(InvenLoc.BACKEND_WAREHOUSE);
                            ShelfType shelfType = shelfSlot3.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn2.getLength();
                                Double furnBreadth = furn2.getBreadth();
                                Double furnHeight = furn2.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = ib.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            prod2.setLowThreshold(resUpperThres);
                            prod2.setUppThreshold(resLowerThres);
                            prod2.setPdtBreadth(furnBreadthRes);
                            prod2.setPdtHeight(furnHeightRes);
                            prod2.setPdtLength(furnLengthRes);

                            il.setQty(prod.getUppThreshold() - prod.getQuantity());
                            if ((prod.getUppThreshold() - prod.getQuantity()) > 0) {
                                il.setMove(true);
                            }
                            //ilList.add(il);
                            prod.setQuantity(prod.getUppThreshold());
                            prod2.setQuantity(qty - prod.getUppThreshold());
                            ib.updateInventoryProd(prod);
                            ib.persistInventoryProduct(prod2);
                            InventoryLocation il2 = new InventoryLocation();
                            il2.setInvItem(prod2.getId());
                            il2.setItemType(2);
                            il2.setQty(prod2.getQuantity());
                            il2.setMove(true);
                            ilList.add(il2);
                        }
                    }
                }
                ilList.add(il);
            } else if (ds.getStatus().equals("Cancelled")) {

            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ilList", ilList);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../inventory/inventory_view_restock_location.xhtml");
        return;
    }

}
