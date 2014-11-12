/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Ingredient;
import entity.InventoryKit;
import entity.InventoryProduct;
import entity.Item;
import entity.Product;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Staff;
import enumerator.InvenLoc;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import session.stateless.CIBeanLocal;
import session.stateless.InventoryBean;
import session.stateless.KitchenBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "kitchenInventoryMB")
@ViewScoped
public class KitchenInventoryBean implements Serializable {

    private Item pdt;
    private Item tempPdt;
    private List<Shelf> zoneShelfEntities;
    private Shelf zoneShelfEntity;
    private List<InventoryKit> retails;
    private List<Shelf> shelfEntities;
    private Shelf shelfEntity;
    private List<ShelfSlot> shelfSlots;
    private ShelfSlot shelfSlot;
    private ShelfType shelfTypeSelected;
    private InventoryKit selectedRetail;
    private String loggedInEmail;
    private String zon;
    private String shelfValue;
    private String shelfSlotPos;
    private String pdtValue;
    private String statusMessage;
    private Double pdtLengthRes;
    private Double pdtBreadthRes;
    private Double pdtHeightRes;
    private Integer resUpperThres;
    private Integer resLowerThres;
    private Integer upperThresValue;
    private Integer upperLowerThresValue;
    private Long newInventoryPdtId;
    private Integer quantity;
    private List<InventoryKit> filteredRetail;
    private List<InventoryKit> inventoryProds;
    private InvenLoc loc;
    private Facility fac;

    @EJB
    private KitchenBean kb;
    @EJB
    private CIBeanLocal cib;

    public KitchenInventoryBean() {
        pdt = null;
    }

    @PostConstruct
    public void init() {
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = kb.getFac(loggedInEmail);
        pdt = null;
        zoneShelfEntities = kb.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();
        retails = kb.getAllInvenIngr();

        resUpperThres = null;

        for (Shelf s : zoneShelfEntities) {
            System.out.println(s.getZone());
        }

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("retails", retails);

    }

    public List<Item> completeText(String query) {

        List<Item> allProducts = kb.getAllIngr();
        List<Item> filteredResults = new ArrayList<Item>();

        for (Item indiv : allProducts) {
            if (indiv.getName().toLowerCase().contains(query)) {
                filteredResults.add(indiv);
            }
        }

        return filteredResults;
    }

    public void storePdtValue() {
        System.out.println(pdt);
    }

    public void onZoneChange() {
        System.out.println("ZONE: " + zon);
        if (zon != null && !zon.equals("")) {
            shelfEntities = kb.getShelfEntities(zon);
            System.out.println("SUCCESS");
        } else {
            shelfEntities = new ArrayList<Shelf>();
        }
    }

    public void onShelfChange() throws Exception {
        pdt = (Item) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pdt");
        System.out.println("In FUNCTION");
        System.out.println("SHELF: " + shelfValue);
        if (shelfValue != null && !shelfValue.equals("")) {
            Long shelfNum = Long.valueOf(shelfValue);
            System.out.println(shelfNum);
            shelfSlots = kb.getShelfSlots(shelfNum);
            shelfTypeSelected = kb.getShelfType(shelfNum);
            Double slotLength = shelfTypeSelected.getLength();
            Double slotBreadth = shelfTypeSelected.getBreadth();
            Double slotHeight = shelfTypeSelected.getHeight();
            System.out.println("SUCCESS 2");
            System.out.println("SlotLength " + slotLength);
            System.out.println("SlotHeight " + slotHeight);
            System.out.println("SlotBreadth " + slotBreadth);

            System.out.println("PRODUCT" + pdt);
            try {
                Double pdtLength = pdt.getLength();
                Double pdtBreadth = pdt.getBreadth();
                Double pdtHeight = pdt.getHeight();
                Map<String, Double> finalvalues = new HashMap<String, Double>();

                finalvalues = kb.calcThresValues(slotLength, slotBreadth, slotHeight, pdtLength, pdtBreadth, pdtHeight);
                pdtLengthRes = finalvalues.get("lengthUsed");
                pdtBreadthRes = finalvalues.get("breadthUsed");
                pdtHeightRes = finalvalues.get("heightUsed");
                Double upperThreshold = finalvalues.get("upperThreshold");
                Double lowerThreshold = finalvalues.get("lowerThreshold");

                resUpperThres = upperThreshold.intValue();
                System.out.println("resUpperThreshold 2: " + resUpperThres);

                resLowerThres = lowerThreshold.intValue();
                System.out.println("resUpperThreshold 2: " + resLowerThres);

                System.out.println("Length to use 2: " + pdtLengthRes);
                System.out.println("Breadth to use 2: " + pdtBreadthRes);
                System.out.println("Height to use 2: " + pdtHeightRes);
                System.out.println("Upper Threshold 2: " + resUpperThres);
                System.out.println("Lower Threshold 2: " + resLowerThres);
            } catch (Exception ex) {

            }

        } else {
            shelfSlots = new ArrayList<ShelfSlot>();
        }
    }

    public void handleSelect(SelectEvent event) {
        tempPdt = (Item) event.getObject();
        System.out.println("HANDLE_SELECTED: " + tempPdt.getId());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pdt", tempPdt);
        System.out.println("HANDLE_SELECTED 2: " + pdt);
    }

    public SelectItem[] getPdtLocValues() {
        SelectItem[] items = new SelectItem[1];

        InvenLoc il = InvenLoc.getIndex(5);
        items[0] = new SelectItem(il, il.getLabel());
        return items;
    }

    public void saveNewInventory(ActionEvent event) {
        System.out.println(pdt);
        System.out.println(zon);
        System.out.println(shelfValue);
        System.out.println(shelfSlotPos);
        System.out.println(loc);

        Long shelfValueLong = Long.valueOf(shelfValue);
        Integer shelfSlotInt = Integer.valueOf(shelfSlotPos);

        try {
            newInventoryPdtId = kb.addNewInventoryIngr(pdt, shelfValueLong, shelfSlotInt, loc, zon, resUpperThres, resLowerThres, pdtLengthRes, pdtBreadthRes, pdtHeightRes);
            statusMessage = "New Inventory Ingredient Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Ingredient Record Result: "
                    + statusMessage + " (New Inventory Ingredient Record ID is " + newInventoryPdtId + ")", ""));
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Added New Inventory Record (Kitchen): " + newInventoryPdtId);
        } catch (Exception ex) {
            newInventoryPdtId = -1L;
            statusMessage = "New Inventory Ingredient Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Ingredient Record Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void updateInventory() {
        InventoryKit ik = new InventoryKit();
        try {
            ik = kb.getInventoryKit(pdt, fac);
            System.out.println("ik: " + ik.getId());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update Inventory Quantity: "
                    + "Item not found", ""));
//            e.printStackTrace();
        }
        System.out.println("qty: " + ik.getQuantity());
        int value = ik.getQuantity() - quantity;
        System.out.println("value: " + value);
        if (value >= 0) {
            ik.setQuantity(value);
            try {
                kb.updateInventoryKit(ik);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("error updating inventorykit");
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Update Inventory Quantity: "
                    + "Successful", ""));
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update Inventory Quantity: "
                    + "Unsuccessful", ""));
        }
    }

    public void deleteRetail() {
        try {
            Long retailId = selectedRetail.getId();
            kb.removeRetail(selectedRetail);
            retails = kb.getAllInvenIngr();
            FacesMessage msg = new FacesMessage("Kitchen Inventory Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Inventory Record (Kitchen): " + retailId);
        } catch (ReferenceConstraintException ex) {
            retails = kb.getAllInvenIngr();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            kb.updateInventoryKit((InventoryKit) event.getObject());
            inventoryProds = kb.getAllInvenIngr();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Edited", ((InventoryProduct) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Edited Inventory Record (Kitchen): " + ((InventoryProduct) event.getObject()).getId().toString());
    /*    } catch (DetailsConflictException dcx) {
            inventoryProds = kb.getAllInvenIngr();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
    */    } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((InventoryProduct) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public Item getPdt() {
        return pdt;
    }

    public void setPdt(Item pdt) {
        this.pdt = pdt;
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

    public String getPdtValue() {
        return pdtValue;
    }

    public void setPdtValue(String pdtValue) {
        this.pdtValue = pdtValue;
    }

    public Item getTempPdt() {
        return tempPdt;
    }

    public void setTempPdt(Item tempPdt) {
        this.tempPdt = tempPdt;
    }

    public ShelfType getShelfTypeSelected() {
        return shelfTypeSelected;
    }

    public void setShelfTypeSelected(ShelfType shelfTypeSelected) {
        this.shelfTypeSelected = shelfTypeSelected;
    }

    public Double getPdtLengthRes() {
        return pdtLengthRes;
    }

    public void setPdtLengthRes(Double pdtLengthRes) {
        this.pdtLengthRes = pdtLengthRes;
    }

    public Double getPdtBreadthRes() {
        return pdtBreadthRes;
    }

    public void setPdtBreadthRes(Double pdtBreadthRes) {
        this.pdtBreadthRes = pdtBreadthRes;
    }

    public Double getPdtHeightRes() {
        return pdtHeightRes;
    }

    public void setPdtHeightRes(Double pdtHeightRes) {
        this.pdtHeightRes = pdtHeightRes;
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

    public Long getNewInventoryPdtId() {
        return newInventoryPdtId;
    }

    public void setNewInventoryPdtId(Long newInventoryPdtId) {
        this.newInventoryPdtId = newInventoryPdtId;
    }

    public List<InventoryKit> getRetails() {
        return retails;
    }

    public void setRetails(List<InventoryKit> retails) {
        this.retails = retails;
    }

    public InventoryKit getSelectedRetail() {
        return selectedRetail;
    }

    public void setSelectedRetail(InventoryKit selectedRetail) {
        this.selectedRetail = selectedRetail;
    }

    public List<InventoryKit> getFilteredRetail() {
        return filteredRetail;
    }

    public void setFilteredRetail(List<InventoryKit> filteredRetail) {
        this.filteredRetail = filteredRetail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String formatDate(Date date) {
        if (date != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
            fmt.setCalendar(cal);
            String dateFormatted = fmt.format(cal.getTime());
            return dateFormatted;
        }
        return "";
    }

}
