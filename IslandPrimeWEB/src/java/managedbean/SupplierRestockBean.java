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
import entity.InventoryProduct;
import entity.Item;
import entity.PoItem;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Supplier;
import enumerator.InvenLoc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private String statusMessage;
    private Double furnLengthRes;
    private Double furnBreadthRes;
    private Double furnHeightRes;
    private Integer resUpperThres;
    private Integer resLowerThres;
    private ShelfSlot shelfSlot;

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
        if (suppliers.isEmpty()) {
            System.err.println("no suppliers found!");
        }
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
        System.err.println("fac: " + fac.getId());
        System.err.println("sup: " + sup.getId());
        piList = ib.getPoItem(fac, sup);
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
        for (PoItem pi : restockmats) {
            //System.err.println("poitem ID: " + pm.getId());
            ilList = new ArrayList<InventoryLocation>();
            ib.persistPoItem(pi);
            if (pi.getStatus().equals("Received")||pi.getStatus().equals("Partial")) {
                //System.err.println("searching for inventorymaterial..");
                InventoryLocation il = new InventoryLocation();
                System.err.println("searching for inventoryproduct..");
                InventoryProduct prod = new InventoryProduct();
                Item furn2 = pi.getItem();
                System.err.println("prod id:" + furn2);
                try {
                    prod = ib.getInventoryProd(furn2, fac, InvenLoc.BACKEND_WAREHOUSE);
                    System.err.println("found invmat id: " + prod);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (prod == null) {
                    System.err.println("new mat:" + prod);
                    prod = new InventoryProduct();
                    prod.setProd(furn2);
                    prod.setFac(fac);
                    prod.setQuantity(0);
                    shelfSlot = new ShelfSlot();
                    shelfSlot = ib.getAvailableShelfSlot(fac, InvenLoc.BACKEND_WAREHOUSE);
                    if (shelfSlot == null) {
                        il.setMove(false);
                    } else {
                        prod.setShelfSlot(shelfSlot);
                        shelfSlot.setOccupied(Boolean.TRUE);
                        prod.setShelf(shelfSlot.getShelf());
                        prod.setZone(shelfSlot.getShelf().getZone());
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
                    ib.persistInventoryProduct(prod);
                }
                il.setInvItem(prod.getId());
                il.setItemType(2);
                il.setItem(furn2);
                il.setZone(prod.getZone());
                il.setShelve(prod.getShelf());
                il.setQty(pi.getQuantity());
                Integer qty = prod.getQuantity() + pi.getQuantity();
                if (qty <= prod.getUppThreshold()) {
//                    System.err.println("shelf has enough space for: " + qty);
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
                            shelfSlot3.setOccupied(Boolean.TRUE);
                            ib.persistShelfSlot(shelfSlot3);
                        prod2.setZone(shelfSlot3.getShelf().getZone());
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
                        il2.setItem(furn2);
                        il2.setZone(prod2.getZone());
                        il2.setShelve(prod2.getShelf());
                        il2.setShelfSlot(prod2.getShelfSlot());
                        il2.setQty(prod2.getQuantity());
                        il2.setMove(true);
                        ilList.add(il2);
                    }
                }
                ilList.add(il);
            } else if (pi.getStatus().equals("Cancelled")) {

            }
        }

        FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().put("ilList", ilList);
        FacesContext.getCurrentInstance()
                .getExternalContext().redirect("../inventory/inventory_view_restock_location.xhtml");
    }
}
