/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.InventoryLocation;
import classes.WeekHelper;
import entity.Bill;
import entity.Facility;
import entity.InventoryMaterial;
import entity.InventoryProduct;
import entity.Item;
import entity.PoItem;
import entity.PoRecord;
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
import session.stateless.ScmBean;

/**
 *
 * @author MY ASUS
 */
@ManagedBean(name = "scmRestockBean")
@ViewScoped
public class ScmRestockBean {

    @EJB
    private ScmBean sb;
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

    public ScmRestockBean() {
    }

    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("restockbean: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = sb.getFac(loggedInEmail);
        suppliers = sb.getMatSuppliers(fac);
        suppliers.addAll(sb.getProdSuppliers(fac));
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
        return sb.getMatName(matId);
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
        sup = sb.getSupplier(Long.valueOf(supplier));
        //System.err.println("supplier: " + supplier);
        piList = sb.getPoItem(fac, sup);
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
            sb.persistPoItem(pi);
            PoRecord po = new PoRecord();
            po = pi.getPo();
            List<PoItem> poItems = new ArrayList<PoItem>();
            poItems = sb.getPoItems(po);
            Boolean check = true;
            for(PoItem p : poItems) {
                if(p.getStatus().equals("Ordered")||p.getStatus().equals("Rejected")) {
                    check = false;
                }
            }
            if(check == true) {
                //po.setStatus("Fulfilled");
                //sb.persistPo(po);
                Bill bill = new Bill();
                bill.setPo(po.getId());
                bill.setStatus("unpaid");
                bill.setAmount(po.getTotalPrice());
                bill.setPaymentDate(wh.getDate(wh.getYear(), wh.getWeek()+2));
                sb.persistBill(bill);
            }
            if (pi.getStatus().equals("Received") || pi.getStatus().equals("Partial")) {
                InventoryLocation il = new InventoryLocation();
                if (sb.getItemType(pi.getItem()) == 1) {
                    System.err.println("searching for inventorymaterial..");
                    InventoryMaterial mat = new InventoryMaterial();
                    Item furn = pi.getItem();
                    System.err.println("mat id:" + furn);
                    try {
                        mat = sb.getInventoryMat(furn, fac, InvenLoc.MF);
                        System.err.println("found invmat id: " + mat);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (mat == null) {
                        mat = new InventoryMaterial();
                        mat.setMat(furn);
                        mat.setFac(fac);
                        mat.setQuantity(0);
                        System.err.println("new mat:" + furn);
                        ShelfSlot shelfSlot = new ShelfSlot();
                        shelfSlot = sb.getAvailableShelfSlot(fac);
                        if (shelfSlot == null) {
                            il.setMove(false);
                        } else {
                            mat.setShelfSlot(shelfSlot);
                            shelfSlot.setOccupied(Boolean.TRUE);
                            sb.persistShelfSlot(shelfSlot);
                            mat.setShelf(shelfSlot.getShelf());
                            mat.setZone(shelfSlot.getShelf().getZone());
                            mat.setLocation(InvenLoc.MF);
                            ShelfType shelfType = shelfSlot.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = sb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                    il.setItem(furn);
                    il.setZone(mat.getZone());
                    il.setShelve(mat.getShelf());
                    il.setShelfSlot(mat.getShelfSlot());
                    il.setQty(pi.getQuantity());
                    Integer qty = mat.getQuantity() + pi.getQuantity();
                    if (qty <= mat.getUppThreshold()) {
                        System.err.println("shelf has enough space for: " + qty);
                        mat.setQuantity(qty);
                        sb.updateInventoryMat(mat);
                        il.setMove(true);
                    } else if (qty > mat.getUppThreshold()) {
                        System.err.println("threshold exceeded");
                        InventoryMaterial mat2 = new InventoryMaterial();
                        mat2.setFac(fac);
                        mat2.setMat(mat.getMat());
                        ShelfSlot shelfSlot2 = new ShelfSlot();
                        shelfSlot2 = sb.getAvailableShelfSlot(mat.getId(), fac);
                        if (shelfSlot2 == null) {
                            shelfSlot2 = sb.getOtherShelfSlot(mat.getId(), fac);
                        }
                        if (shelfSlot2 == null) {
                            System.err.println("no shelfslot available");
                            il.setMove(false);
                        } else {
                            System.err.println("shelfslot position: " + shelfSlot2.getPosition());
                            mat2.setShelfSlot(shelfSlot2);
                            shelfSlot2.setOccupied(Boolean.TRUE);
                            sb.persistShelfSlot(shelfSlot2);
                            mat2.setShelf(shelfSlot2.getShelf());
                            mat2.setZone(shelfSlot2.getShelf().getZone());
                            System.err.println("shelf: " + shelfSlot2.getShelf().getShelf());
                            mat2.setLocation(InvenLoc.MF);
                            ShelfType shelfType = shelfSlot2.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn.getLength();
                                Double furnBreadth = furn.getBreadth();
                                Double furnHeight = furn.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = sb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            if((mat.getUppThreshold() - mat.getQuantity()) > 0) {
                                il.setMove(true);
                            }
                            //ilList.add(il);
                            mat.setQuantity(mat.getUppThreshold());
                            mat2.setQuantity(qty - mat.getUppThreshold());
                            sb.updateInventoryMat(mat);
                            sb.persistInventoryMaterial(mat2);
                            InventoryLocation il2 = new InventoryLocation();
                            il2.setInvItem(mat2.getId());
                            il2.setItemType(1);
                            il2.setItem(furn);
                            il2.setZone(mat2.getZone());
                            il2.setShelve(mat2.getShelf());
                            il2.setShelfSlot(mat2.getShelfSlot());
                            il2.setQty(mat2.getQuantity());
                            il2.setMove(true);
                            ilList.add(il2);
                        }
                    }
                } else if (sb.getItemType(pi.getItem()) == 2) {
                    System.err.println("searching for inventoryproduct..");
                    InventoryProduct prod = new InventoryProduct();
                    Item furn2 = pi.getItem();
                    System.err.println("prod id:" + furn2);
                    try {
                        prod = sb.getInventoryProd(furn2, fac, InvenLoc.MF);
                        System.err.println("found invmat id: " + prod);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (prod == null) {
                        System.err.println("new mat:" + prod);
                        ShelfSlot shelfSlot = new ShelfSlot();
                        shelfSlot = sb.getAvailableShelfSlot(fac);
                        if (shelfSlot == null) {
                            il.setMove(false);
                        } else {
                            prod.setShelfSlot(shelfSlot);
                            shelfSlot.setOccupied(Boolean.TRUE);
                            sb.persistShelfSlot(shelfSlot);
                            prod.setShelf(shelfSlot.getShelf());
                            prod.setZone(shelfSlot.getShelf().getZone());
                            prod.setLocation(InvenLoc.MF);
                            ShelfType shelfType = shelfSlot.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn2.getLength();
                                Double furnBreadth = furn2.getBreadth();
                                Double furnHeight = furn2.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = sb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                    il.setItem(furn2);
                    il.setZone(prod.getZone());
                    il.setShelve(prod.getShelf());
                    il.setQty(pi.getQuantity());
                    Integer qty = prod.getQuantity() + pi.getQuantity();
                    if (qty <= prod.getUppThreshold()) {
                        System.err.println("shelf has enough space for: " + qty);
                        prod.setQuantity(qty);
                        sb.updateInventoryProd(prod);
                        il.setMove(true);
                    } else if (qty > prod.getUppThreshold()) {
                        InventoryProduct prod2 = new InventoryProduct();
                        prod2.setFac(fac);
                        prod2.setProd(prod.getProd());
                        ShelfSlot shelfSlot3 = new ShelfSlot();
                        shelfSlot3 = sb.getAvailableShelfSlot(prod.getId(), fac);
                        if (shelfSlot3 == null) {
                            shelfSlot3 = sb.getOtherShelfSlot(prod.getId(), fac);
                        }
                        if (shelfSlot3 == null) {
                            il.setMove(false);
                        } else {
                            prod2.setShelfSlot(shelfSlot3);
                            shelfSlot3.setOccupied(Boolean.TRUE);
                            sb.persistShelfSlot(shelfSlot3);
                            prod2.setShelf(shelfSlot3.getShelf());
                            prod2.setZone(shelfSlot3.getShelf().getZone());
                            prod2.setLocation(InvenLoc.MF);
                            ShelfType shelfType = shelfSlot3.getShelf().getShelfType();
                            Double slotLength = shelfType.getLength();
                            Double slotBreadth = shelfType.getBreadth();
                            Double slotHeight = shelfType.getHeight();
                            try {
                                Double furnLength = furn2.getLength();
                                Double furnBreadth = furn2.getBreadth();
                                Double furnHeight = furn2.getHeight();
                                Map<String, Double> finalvalues = new HashMap<String, Double>();

                                finalvalues = sb.calcThresValues(slotLength, slotBreadth, slotHeight, furnLength, furnBreadth, furnHeight);
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
                            if((prod.getUppThreshold() - prod.getQuantity()) > 0) {
                                il.setMove(true);
                            }
                            //ilList.add(il);
                            prod.setQuantity(prod.getUppThreshold());
                            prod2.setQuantity(qty - prod.getUppThreshold());
                            sb.updateInventoryProd(prod);
                            sb.persistInventoryProduct(prod2);
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
                }
                ilList.add(il);
            } else if (pi.getStatus().equals("Cancelled")) {

            }
        }
        if(ilList.isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../scm/scm_receive_supplier_inventory.xhtml");
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ilList", ilList);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../scm/scm_view_restock_location.xhtml");
    }
}
