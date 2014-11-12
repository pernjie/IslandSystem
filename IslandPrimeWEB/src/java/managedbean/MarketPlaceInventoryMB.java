/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

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
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "marketPlaceInventoryMB")
@ViewScoped
public class MarketPlaceInventoryMB implements Serializable {

    private Item pdt;
    private Item tempPdt;
    private List<Shelf> zoneShelfEntities;
    private Shelf zoneShelfEntity;

    private List<InventoryProduct> retails;

    private List<Shelf> shelfEntities;
    private Shelf shelfEntity;

    private List<ShelfSlot> shelfSlots;
    private ShelfSlot shelfSlot;

    private ShelfType shelfTypeSelected;

    private InventoryProduct selectedRetail;

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

    private List<InventoryProduct> filteredRetail;

    private List<InventoryProduct> inventoryProds;

    private InvenLoc loc;


    @EJB
    private InventoryBean inventoryBean;
    @EJB
    private CIBeanLocal cib;

    public MarketPlaceInventoryMB() {
        pdt = null;
    }

    @PostConstruct
    public void init() {
        pdt = null;
        zoneShelfEntities = inventoryBean.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();
        retails = inventoryBean.getAllRetails();

        resUpperThres = null;

        for (Shelf s : zoneShelfEntities) {
            System.out.println(s.getZone());
        }

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("retails", retails);

    }

    public List<Item> completeText(String query) {

        List<Item> allProducts = inventoryBean.getAllProducts();
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
            shelfEntities = inventoryBean.getShelfEntities(zon);
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
            shelfSlots = inventoryBean.getShelfSlots(shelfNum);
            shelfTypeSelected = inventoryBean.getShelfType(shelfNum);
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

                finalvalues = inventoryBean.calcThresValues(slotLength, slotBreadth, slotHeight, pdtLength, pdtBreadth, pdtHeight);
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
        SelectItem[] items = new SelectItem[2];
        int j = 0;
        int i = 2;
        for (i = 2; i < InvenLoc.values().length; ++i) {
            InvenLoc il = InvenLoc.getIndex(i);
            items[j++] = new SelectItem(il, il.getLabel());
        }
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
            newInventoryPdtId = inventoryBean.addNewInventoryPdt(pdt, shelfValueLong, shelfSlotInt, loc, zon, resUpperThres, resLowerThres, pdtLengthRes, pdtBreadthRes, pdtHeightRes);
            statusMessage = "New Inventory Product Record Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Product Record Result: "
                    + statusMessage + " (New Inventory Product Record ID is " + newInventoryPdtId + ")", ""));
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Added New Inventory Record (Product): " + newInventoryPdtId);
        } catch (Exception ex) {
            newInventoryPdtId = -1L;
            statusMessage = "New Inventory Product Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Product Record Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteRetail() {
        try {
            Long furnId = selectedRetail.getId();
            inventoryBean.removeRetail(selectedRetail);
            retails = inventoryBean.getAllRetails();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Inventory Record (Retail): " + furnId);
        } catch (ReferenceConstraintException ex) {
            retails = inventoryBean.getAllRetails();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            inventoryBean.updateInventoryProduct((InventoryProduct) event.getObject());
            inventoryProds = inventoryBean.getAllRetails();
            FacesMessage msg = new FacesMessage("Retail Inventory Record Edited", ((InventoryProduct) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            inventoryProds = inventoryBean.getAllRetails();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((InventoryProduct) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public List<InventoryProduct> getRetails() {
        return retails;
    }

    public void setRetails(List<InventoryProduct> retails) {
        this.retails = retails;
    }

    public InventoryProduct getSelectedRetail() {
        return selectedRetail;
    }

    public void setSelectedRetail(InventoryProduct selectedRetail) {
        this.selectedRetail = selectedRetail;
    }

    public List<InventoryProduct> getFilteredRetail() {
        return filteredRetail;
    }

    public void setFilteredRetail(List<InventoryProduct> filteredRetail) {
        this.filteredRetail = filteredRetail;
    }
    
    public String formatDate(Date date) {
        if (date!=null) {
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
