/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.InventoryLocation;
import entity.Facility;
import entity.InventoryMaterial;
import entity.InventoryProduct;
import entity.Item;
import java.util.ArrayList;
import java.util.List;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import enumerator.InvenLoc;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.bean.ViewScoped;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.primefaces.event.RowEditEvent;

import org.primefaces.event.SelectEvent;
import session.stateless.InventoryBean;
import util.SMTPAuthenticator;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "replenishInventoryBean")
@ViewScoped
public class ReplenishInventoryBean implements Serializable {

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
    private final static String[] location;
    private String loc;
    private InvenLoc invenLoc;

    private InventoryMaterial selectedInvenFurn;
    private InventoryMaterial filteredInvenFurn;
    private List<InventoryMaterial> invenFurns;
    private List<InventoryProduct> invenProds;
    private InventoryMaterial restockMat;
    private InventoryProduct restockProd;
    private List<InventoryLocation> ilList;
    private List<InventoryLocation> restockList;
    private InventoryLocation il;
    private InventoryLocation rl;

    private String loggedInEmail;
    private Facility fac;
 
    String emailServerName = "mailauth.comp.nus.edu.sg";
// Replace with your real name and unix email address
    String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
// Replace with your real name and NUSNET email address
    String mailer = "JavaMailer";


    @EJB
    private InventoryBean ib;

    static {
        location = new String[3];
        location[0] = "MarketPlace";
        location[1] = "Self Service Warehouse";
        location[2] = "Retail Store";
    }

    @PostConstruct
    public void init() {
        System.err.println("replenishinventorybean: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = ib.getFac(loggedInEmail);
        zoneShelfEntities = ib.getZoneShelfEntitiesFromFac();
        shelfEntities = new ArrayList<Shelf>();
        shelfSlots = new ArrayList<ShelfSlot>();

        resUpperThres = null;

        for (Shelf s : zoneShelfEntities) {
            System.out.println(s.getZone());
        }

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("zoneShelfEntities", zoneShelfEntities);

    }

    public void updateTable() throws EntityDneException, DetailsConflictException {
        Integer qty = 0;
        ilList = new ArrayList<InventoryLocation>();
        restockList = new ArrayList<InventoryLocation>();
        System.err.println("location selected: " + loc);
        if (loc.equals("MarketPlace") || loc.equals("Self Service Warehouse")) {
            if(loc.equals("MarketPlace")) {
                invenLoc = InvenLoc.FRONTEND_STORE;
            }
            else {
                invenLoc = InvenLoc.FRONTEND_WAREHOUSE;
            }
            invenFurns = ib.getFurns(fac, invenLoc);
            System.err.println(invenFurns.size() + " items found");
            for (InventoryMaterial mat : invenFurns) {
                il = new InventoryLocation();
                rl = new InventoryLocation();
                furn = new Item() {};
                furn = mat.getMat();
                System.err.println("for item: " + furn.getId() + " " + furn.getName());
                if (mat.getQuantity() < mat.getUppThreshold()) {
                    System.err.println("inventory level below upper threshold");
                    il.setInvItem(furn.getId());
                    rl.setInvItem(furn.getId());
                    il.setItemType(1);
                    rl.setItemType(1);
                    qty = mat.getUppThreshold() - mat.getQuantity();
                    System.err.println("shortfall of: " + qty);
                    restockMat = new InventoryMaterial();
                    restockMat = ib.getInventoryMat(furn, fac, InvenLoc.BACKEND_WAREHOUSE);
                    if (restockMat.getQuantity() > qty) {
                        System.err.println("sufficient inventory to restock");
                        mat.setQuantity(mat.getUppThreshold());
                        restockMat.setQuantity(restockMat.getQuantity() - qty);
                        il.setItem(furn);
                        il.setZone(mat.getZone());
                        il.setShelve(mat.getShelf());
                        il.setShelfSlot(mat.getShelfSlot());
                        il.setQty(qty);
                        il.setMove(true);
                        rl.setItem(furn);
                        rl.setZone(restockMat.getZone());
                        rl.setShelve(restockMat.getShelf());
                        rl.setShelfSlot(restockMat.getShelfSlot());
                        rl.setQty(qty);
                        rl.setMove(true);
                    } else if (restockMat.getQuantity() > 0) {
                        System.err.println("insufficient inventory to restock");
                        mat.setQuantity(mat.getQuantity() + restockMat.getQuantity());
                        restockMat.setQuantity(0);
                        il.setItem(furn);
                        il.setZone(mat.getZone());
                        il.setShelve(mat.getShelf());
                        il.setShelfSlot(mat.getShelfSlot());
                        il.setQty(restockMat.getQuantity());
                        il.setMove(true);
                        rl.setItem(furn);
                        rl.setZone(restockMat.getZone());
                        rl.setShelve(restockMat.getShelf());
                        rl.setShelfSlot(restockMat.getShelfSlot());
                        rl.setQty(restockMat.getQuantity());
                        rl.setMove(true);
                    } else {
                    // ad hoc production planning
                        sendAdHocProdOrder(fac,mat.getMat());
                    }
                    ib.updateInventory(mat);
                    ib.updateInventory(restockMat);
                    ilList.add(il);
                    restockList.add(rl);
                }
            }
        } else {
            invenProds = ib.getProds(fac, invenLoc);
            for (InventoryProduct prod : invenProds) {
                il = new InventoryLocation();
                furn = new Item() {};
                furn = prod.getProd();
                if (prod.getQuantity() < prod.getUppThreshold()) {
                    il.setInvItem(furn.getId());
                    rl.setInvItem(furn.getId());
                    il.setItemType(2);
                    rl.setItemType(2);
                    qty = prod.getUppThreshold() - prod.getQuantity();
                    restockProd = new InventoryProduct();
                    restockProd = ib.getInventoryProd(furn, fac, InvenLoc.BACKEND_WAREHOUSE);
                    if (restockProd.getQuantity() > qty) {
                        prod.setQuantity(prod.getUppThreshold());
                        restockProd.setQuantity(restockProd.getQuantity() - qty);                      
                        il.setItem(furn);
                        il.setZone(prod.getZone());
                        il.setShelve(prod.getShelf());
                        il.setShelfSlot(prod.getShelfSlot());il.setQty(qty);
                        il.setMove(true);
                        rl.setZone(restockProd.getZone());
                        rl.setShelve(restockProd.getShelf());
                        rl.setShelfSlot(restockProd.getShelfSlot());
                        rl.setQty(qty);
                        rl.setMove(true);
                    } else if (restockProd.getQuantity() > 0) {
                        prod.setQuantity(prod.getQuantity() + restockProd.getQuantity());
                        restockProd.setQuantity(0);
                        il.setItem(furn);
                        il.setZone(prod.getZone());
                        il.setShelve(prod.getShelf());
                        il.setShelfSlot(prod.getShelfSlot());
                        il.setQty(restockProd.getQuantity());
                        il.setMove(true);
                        rl.setItem(furn);
                        rl.setZone(restockProd.getZone());
                        rl.setShelve(restockProd.getShelf());
                        rl.setShelfSlot(restockProd.getShelfSlot());
                        rl.setQty(restockProd.getQuantity());
                        rl.setMove(true);
                    } else {
                    // ad hoc production planning
                        sendAdHocProdOrder(fac,prod.getProd());
                    }
                    ib.updateInventoryProduct(prod);
                    ib.updateInventoryProduct(restockProd);
                    ilList.add(il);
                    restockList.add(il);
                }
            }
        }
    }

    public void updateInventory() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ilList", restockList);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../inventory/inventory_view_restock_location.xhtml");
    }
    
    public void sendAdHocProdOrder(Facility fac, Item mat) {
        System.err.println("send ad hoc prod order");
        Facility mf = new Facility();
        mf = ib.getMatMf(fac, mat);
        String toEmailAddress = "islandFurnituremf@gmail.com";
        System.out.println("send to: " + toEmailAddress);
        
        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            javax.mail.Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth); 
            session.setDebug(true);
            Message msg = new MimeMessage(session);
            if (msg != null) {
                msg.setFrom(InternetAddress.parse(emailFromAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Ad Hoc Production Request from " + fac.getName() + "\n\n");
                
                //Create and fill first part
                MimeBodyPart header = new MimeBodyPart();
                header.setText("Hi " + mf.getName() + ".\n\n");
                
                //Create and fill second part
                MimeBodyPart body = new MimeBodyPart();
                body.setText("This is a Ad Hoc Production request from " + fac.getName() + ".\n\n");
                
                //Create and fill third part
                MimeBodyPart linkText = new MimeBodyPart();
                linkText.setText("The inventory level for " + mat.getName() + " is currently running low.");
                
                //Create the Multipart
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(header);
                mp.addBodyPart(body);
                mp.addBodyPart(linkText);
                
                //Set Message Content
                msg.setContent(mp);
                
                //String messageText = "Welcome to Island Furniture Family, " +name+ ".\n\n Here's the autogenerated password: " + password +"\n\n";
                //msg.setText(messageText);
                //msg.setDisposition(Part.INLINE);
                
                msg.setHeader("X-Mailer", mailer);
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);
                Transport.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e.getMessage());
        }
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
            newInvenFurnId = ib.addNewInventoryRawMat(furn, shelfValueLong, shelfSlotInt, invenLoc, zon, resUpperThres, resLowerThres, furnLengthRes, furnBreadthRes, furnHeightRes);
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

    /**
     * Creates a new instance of SuppliesMatToFacManagerBean
     */
    public ReplenishInventoryBean() {

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

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
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

    public InvenLoc getInvenLoc() {
        return invenLoc;
    }
    
    public String[] getLocation() {
        return location;
    }

    public List<InventoryLocation> getIlList() {
        return ilList;
    }

    public void setIlList(List<InventoryLocation> ilList) {
        this.ilList = ilList;
    }

    public List<InventoryLocation> getRestockList() {
        return restockList;
    }

    public void setRestockList(List<InventoryLocation> restockList) {
        this.restockList = restockList;
    }

}
