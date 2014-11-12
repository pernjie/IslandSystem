/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.InventoryLocation;
import classes.WeekHelper;
import entity.Facility;
import entity.InventoryKit;
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
import session.stateless.KitchenBean;

/**
 *
 * @author MY ASUS
 */
@ManagedBean(name = "kitRestockBean")
@ViewScoped
public class KitRestockBean {

    @EJB
    private KitchenBean kb;
    private String loggedInEmail;
    private List<Supplier> suppliers;
    private Long supplier;
    private Date currDate;
    private Facility fac;
    private Supplier sup;
    private PoItem poitem;
    private List<PoItem> piList;
    private List<PoItem> restockmats;
    private List<InventoryLocation> ilList;
    private final static String[] statuses;
    private WeekHelper wh = new WeekHelper();
    private Double furnLengthRes;
    private Double furnBreadthRes;
    private Double furnHeightRes;
    private Integer resUpperThres;
    private Integer resLowerThres;
    private Integer upperThresValue;
    private Integer upperLowerThresValue;

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

    public KitRestockBean() {
    }

    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("restockbean: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = kb.getFac(loggedInEmail);
        suppliers = kb.getIngrSuppliers(fac);
        if (suppliers.isEmpty()) {
            System.err.println("no suppliers found!");
        }
        currDate = wh.getCurrDate();
    }

    public Date getCurrDate() {
        return currDate;
    }

    public void setCurrDate(Date currDate) {
        this.currDate = currDate;
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
        return kb.getIngrName(matId);
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
        sup = kb.getSupplier(Long.valueOf(supplier));
        //System.err.println("supplier: " + supplier);
        piList = kb.getPoItem(fac, sup);
        restockmats = piList;
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status Updated to:", "" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void updateInventory() throws IOException {
        ilList = new ArrayList<InventoryLocation>();
        for (PoItem pi : restockmats) {
            //System.err.println("poitem ID: " + pm.getId());
            kb.persistPoItem(pi);
            if (pi.getStatus().equals("Received") || pi.getStatus().equals("Partial")) {
                InventoryLocation il = new InventoryLocation();
                if (kb.getItemType(pi.getItem()) == 3) {
                    System.err.println("searching for inventoryingredient..");
                    InventoryKit mat = new InventoryKit();
                    Item furn = pi.getItem();
                    System.err.println("mat id:" + furn);
                    try {
                        mat = kb.getInventoryKit(furn, fac);
                        System.err.println("found invmat id: " + mat);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (mat == null) {
                        System.err.println("new mat:" + furn);
                        ShelfSlot shelfSlot = new ShelfSlot();
                        shelfSlot = kb.getAvailableShelfSlot(fac);
                        if (shelfSlot == null) {
                            il.setMove(false);
                        } else {
                            mat.setShelfSlot(shelfSlot);
                            shelfSlot.setOccupied(Boolean.TRUE);
                            mat.setShelf(shelfSlot.getShelf());
                            mat.setLocation(InvenLoc.KITCHEN);
                            ShelfType shelfType = shelfSlot.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = kb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            mat.setIngBreadth(furnBreadthRes);
                            mat.setIngHeight(furnHeightRes);
                            mat.setIngLength(furnLengthRes);
                        }
                    }
                    il.setInvItem(mat.getId());
                    il.setItemType(1);
                    il.setQty(pi.getQuantity());
                    Integer qty = mat.getQuantity() + pi.getQuantity();
                    if (qty <= mat.getUppThreshold()) {
                        System.err.println("shelf has enough space for: " + qty);
                        mat.setQuantity(qty);
                        kb.updateInventoryKit(mat);
                        il.setMove(true);
                    } else if (qty > mat.getUppThreshold()) {
                        System.err.println("threshold exceeded");
                        InventoryKit mat2 = new InventoryKit();
                        mat2.setFac(fac);
                        mat2.setIngr(mat.getIngr());
                        ShelfSlot shelfSlot2 = new ShelfSlot();
                        shelfSlot2 = kb.getAvailableShelfSlot(mat.getId(), fac);
                        if (shelfSlot2 == null) {
                            shelfSlot2 = kb.getOtherShelfSlot(mat.getId(), fac);
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
                            mat2.setLocation(InvenLoc.KITCHEN);
                            ShelfType shelfType = shelfSlot2.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = kb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            mat2.setIngBreadth(furnBreadthRes);
                            mat2.setIngHeight(furnHeightRes);
                            mat2.setIngLength(furnLengthRes);

                            il.setQty(mat.getUppThreshold() - mat.getQuantity());
                            if((mat.getUppThreshold() - mat.getQuantity()) > 0) {
                                il.setMove(true);
                            }
                            //ilList.add(il);
                            mat.setQuantity(mat.getUppThreshold());
                            mat2.setQuantity(qty - mat.getUppThreshold());
                            kb.updateInventoryKit(mat);
                            kb.persistInventoryKit(mat2);
                            InventoryLocation il2 = new InventoryLocation();
                            il2.setInvItem(mat2.getId());
                            il2.setItemType(3);
                            il2.setQty(mat2.getQuantity());
                            il2.setMove(true);
                            ilList.add(il2);
                        }
                    }
                }
                ilList.add(il);
            } else if (pi.getStatus().equals("Cancelled")) {

            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ilList", ilList);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../kitchen/kitchen_view_restock_location.xhtml");
    }
}
