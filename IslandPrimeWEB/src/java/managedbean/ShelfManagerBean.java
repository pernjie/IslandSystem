/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Shelf;
import entity.ShelfSlot;
import entity.ShelfType;
import entity.Staff;
import enumerator.InvenLoc;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.primefaces.event.RowEditEvent;
import session.stateless.CIBeanLocal;
import session.stateless.InventoryBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "shelfManagerBean")
@ViewScoped
public class ShelfManagerBean implements Serializable {

    @EJB
    private InventoryBean inventoryBean;
    @EJB
    private CIBeanLocal cib;
    private String fac;
    private String zone;
    private InvenLoc location;
    private String shelfId;
    private String shelfTypeId;
    private Long id;
    private Long newShelfId;
    private String statusMessage;

    private Facility facility;
    private ShelfType shelfType;
    private Shelf shelf;

    private List<ShelfType> shelfTypes;
    private List<Facility> facilities;
    private Shelf selectedShelf;
    private List<Shelf> filteredShelf;
    private List<Shelf> allShelf;

    private List<ShelfSlot> occupied;

    @PostConstruct
    public void init() {
        facilities = inventoryBean.getAllMFsStores();
        shelfTypes = inventoryBean.getAllShelfTypes();
        allShelf = inventoryBean.getAllShelf();

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("shelfTypes", shelfTypes);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("allshelf", allShelf);

    }

    public void saveNewShelf(ActionEvent event) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", loggedInEmail);

        System.out.println("email: " + loggedInEmail);
        Staff temp = (Staff) q.getSingleResult();
        facility = temp.getFac();

        System.out.println(location);
        System.out.println(zone);
        System.out.println(shelfId);
        System.out.println(shelfTypeId);

        Long facLong = facility.getId();
        Integer shelfInt = Integer.valueOf(shelfId);
        Long shelfTypeLong = Long.valueOf(shelfTypeId);

        try {
            newShelfId = inventoryBean.addNewShelf(facLong, location, zone, shelfInt, shelfTypeLong);
            statusMessage = "New Shelf Type Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Shelf Status Result: "
                    + statusMessage + " (New Shelf Status ID is " + newShelfId + ")", ""));
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Added new Shelf: " + newShelfId);
            setLocation(null);
            setZone(null);
            setShelfId(null);
            setShelfTypeId(null);

        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newShelfId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Shelf Status Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newShelfId = -1L;
            statusMessage = "New Shelf Status Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Shelf Status Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public List<ShelfSlot> getOccupied(Shelf shelf) {

        occupied = inventoryBean.getAllShelfSlot(shelf);

        return occupied;

    }

    public void deleteShelf() {
        try {
            Long shelfId = selectedShelf.getId();
            inventoryBean.removeShelf(selectedShelf);
            allShelf = inventoryBean.getAllShelf();
            FacesMessage msg = new FacesMessage("Shelf  Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Shelf: " + shelfId);
        } catch (ReferenceConstraintException ex) {
            allShelf = inventoryBean.getAllShelf();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public List<Shelf> getFilteredShelf() {
        return filteredShelf;
    }

    public void setFilteredShelf(List<Shelf> filteredShelf) {
        this.filteredShelf = filteredShelf;
    }

    /**
     * Creates a new instance of InvenotryManagerBean
     *
     * @return
     */
    public InventoryBean getInventoryBean() {
        return inventoryBean;
    }

    public void setInventoryBean(InventoryBean inventoryBean) {
        this.inventoryBean = inventoryBean;
    }

    public String getFac() {
        return fac;
    }

    public void setFac(String fac) {
        this.fac = fac;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public String getShelfTypeId() {
        return shelfTypeId;
    }

    public void setShelfTypeId(String shelfTypeId) {
        this.shelfTypeId = shelfTypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewShelfId() {
        return newShelfId;
    }

    public void setNewShelfId(Long newShelfId) {
        this.newShelfId = newShelfId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public ShelfType getShelfType() {
        return shelfType;
    }

    public void setShelfType(ShelfType shelfType) {
        this.shelfType = shelfType;
    }

    public List<ShelfType> getShelfTypes() {
        return shelfTypes;
    }

    public void setShelfTypes(List<ShelfType> shelfTypes) {
        this.shelfTypes = shelfTypes;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Shelf> getAllShelf() {
        return allShelf;
    }

    public void setAllShelf(List<Shelf> allShelf) {
        this.allShelf = allShelf;
    }

    public Shelf getSelectedShelf() {
        return selectedShelf;
    }

    public void setSelectedShelf(Shelf selectedShelf) {
        this.selectedShelf = selectedShelf;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public InvenLoc getLocation() {
        return location;
    }

    public void setLocation(InvenLoc location) {
        this.location = location;
    }

    public SelectItem[] getLocValues() {
        Facility fac = (Facility) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facility");
        SelectItem[] items = null;
        System.err.println("Fac type = " + fac.getType());
        if (fac.getType().equals("Manufacturing")) {
            items = new SelectItem[1];
            items[0] = new SelectItem(InvenLoc.getIndex(0), InvenLoc.getIndex(0).getLabel());
        } else if (fac.getType().equals("Store")) {
            items = new SelectItem[4];
            int j = 0;
            int i;
            for (i = 1; i < 5; i++) {
                System.out.println("arr =" + i);
                InvenLoc il = InvenLoc.getIndex(i);
                items[j++] = new SelectItem(il, il.getLabel());
            }
        }
            return items;
        }
}
