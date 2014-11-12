/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.InventoryLocation;
import classes.WeekHelper;
import entity.Facility;
import entity.InventoryMaterial;
import entity.PoItem;
import entity.ShelfSlot;
import entity.Supplier;
import enumerator.InvenLoc;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.CellEditEvent;
import session.stateless.InventoryBean;

/**
 *
 * @author MY ASUS
 */
@ManagedBean(name = "supplierRestockBean")
@ViewScoped
public class SupplierRestockBean {

    @EJB
    private InventoryBean ib;
    private String loggedInEmail;
    private List<Supplier> suppliers;
    private Long supplier;
    private Date currDate;
    private Facility fac;
    private Supplier sup;
    private PoItem poitem;
    private List<PoItem> piList;
    private List<PoItem> restockmats;
    private List<InventoryLocation> ilList = new ArrayList<InventoryLocation>();
    private final static String[] statuses;
    private WeekHelper wh = new WeekHelper();

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

    public SupplierRestockBean() {
    }

    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("supplierrestockbean: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = ib.getFac(loggedInEmail);
        suppliers = ib.getSuppliers(fac);
        if(suppliers.isEmpty())
            System.err.println("no suppliers found!");
        currDate = wh.getCurrDate();
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
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

    public PoItem getPomaterial() {
        return poitem;
    }

    public void setPomaterial(PoItem pomaterial) {
        this.poitem = pomaterial;
    }

    public List<PoItem> getPiList() {
        return piList;
    }

    public void setPiList(List<PoItem> piList) {
        this.piList = piList;
    }

    public List<PoItem> getRestockmats() {
        return restockmats;
    }

    public void setRestockmats(List<PoItem> restockmats) {
        this.restockmats = restockmats;
    }

    public String[] getStatuses() {
        return statuses;
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

    public void updateTable() {
        System.err.println("function: updateTable");
        sup = ib.getSupplier(Long.valueOf(supplier));
        //System.err.println("supplier: " + supplier);
        piList = ib.getPoItem(fac, sup, currDate);
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

    public void updateInventory() { 
        for (PoItem pi : restockmats) {
            //System.err.println("poitem ID: " + pm.getId());
            ib.persistPoItem(pi);
            if (pi.getStatus().equals("Received")) {
                //System.err.println("searching for inventorymaterial..");
                InventoryMaterial mat = new InventoryMaterial();
                InventoryLocation il = new InventoryLocation();
                mat = ib.getInventoryMat(pi.getItem(), fac, InvenLoc.BACKEND_WAREHOUSE);
                //System.err.println("fount mat id: " + pm.getMat());
                il.setInvItem(mat.getId());////
                il.setQty(pi.getQuantity());
                Integer qty = mat.getQuantity() + pi.getQuantity();
                if (qty <= mat.getUppThreshold()) {
                    //System.err.println("shelf has enough space for: " + qty);
                    mat.setQuantity(qty);
                    ib.updateInventory(mat);
                    il.setMove(true);
                } else if (qty > mat.getUppThreshold()) {
                    InventoryMaterial mat2 = new InventoryMaterial();
                    mat2.setFac(fac);
                    mat2.setMat(mat.getMat());
                    ShelfSlot shelfSlot = new ShelfSlot();
                    shelfSlot = ib.getAvailableShelfSlot(mat.getId(), fac, InvenLoc.MF);
                    mat2.setShelfSlot(shelfSlot);
                    mat2.setShelf(shelfSlot.getShelf());
                    mat2.setLocation(InvenLoc.BACKEND_WAREHOUSE);
                    mat2.setLowThreshold(mat.getLowThreshold());
                    mat2.setUppThreshold(mat.getUppThreshold());
                    mat2.setMatBreadth(mat.getMatBreadth());
                    mat2.setMatHeight(mat.getMatHeight());
                    mat2.setMatLength(mat.getMatLength());
                    
                    il.setQty(mat.getUppThreshold() - mat.getQuantity());
                    il.setMove(true);
                    ilList.add(il);
                    mat.setQuantity(mat.getUppThreshold());
                    mat2.setQuantity(qty - mat.getUppThreshold());
                    ib.updateInventory(mat);
                    ib.persistInventoryMaterial(mat2);
                    il = new InventoryLocation();
                    il.setInvItem(mat2.getId());
                    il.setQty(mat2.getQuantity());
                    il.setMove(true);
                }                
                ilList.add(il);
            }
        }
        return;
    }
} 
