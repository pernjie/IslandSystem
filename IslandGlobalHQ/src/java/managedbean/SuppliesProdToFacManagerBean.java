/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Product;
import entity.Supplier;
import entity.SuppliesProdToFac;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
import session.stateless.GlobalHqBean;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "suppliesProdToFacManagerBean")
@ViewScoped
public class SuppliesProdToFacManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String fac;
    private String prod;
    private String sup;
    private String facName;
    private String prodName;
    private String supName;
    private String lotSize;
    private String unitPrice;
    private String leadTime;
    private List<Facility> facilities;
    private List<Product> products;
    private List<Supplier> suppliers;
    private Facility facility;
    private Facility newFacility;

    private Long newSuppliesProdToFacId;
    private String statusMessage;
    private SuppliesProdToFac selectedSuppliesProdToFac;
    private List<SuppliesProdToFac> filteredSuppliesProdToFacs;
    private List<SuppliesProdToFac> suppliesProdToFacs;
    private SuppliesProdToFac newSuppliesProdToFac = new SuppliesProdToFac();

    @PostConstruct
    public void init() {
        suppliesProdToFacs = globalHqBean.getAllSuppliesProdToFacs();
        facilities = globalHqBean.getAllFacilities();
        products = globalHqBean.getAllProducts();
        suppliers = globalHqBean.getAllSuppliers();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facilities", facilities);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("products", products);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("suppliers", suppliers);
    }

    public void saveNewSuppliesProdToFac(ActionEvent event) {
        System.out.println(fac);
        System.out.println(prod);
        System.out.println(sup);
        Long facId = Long.valueOf(fac);
        Long prodId = Long.valueOf(prod);
        Long supId = Long.valueOf(sup);
        Integer lotSizeInt = Integer.valueOf(lotSize);
        Double unitPriceDble = Double.valueOf(unitPrice);
        Integer leadTimeInt = Integer.valueOf(leadTime);

        try {
            newSuppliesProdToFacId = globalHqBean.addNewSuppliesProdToFac(facId, prodId, supId, lotSizeInt, unitPriceDble, leadTimeInt);
            statusMessage = "New SuppliesProdToFac Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SuppliesProdToFac Result: "
                    + statusMessage + " (New SuppliesProdToFac ID is " + newSuppliesProdToFacId + ")", ""));
        } catch (EntityDneException edx) {
            statusMessage = edx.getMessage();
            newSuppliesProdToFacId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New SuppliesProdToFac Result: "
                    + statusMessage, ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newSuppliesProdToFacId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New SuppliesProdToFac Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newSuppliesProdToFacId = -1L;
            statusMessage = "New SuppliesProdToFac Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SuppliesProdToFac Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteSuppliesProdToFac() {
        try {
            globalHqBean.removeSuppliesProdToFac(selectedSuppliesProdToFac);
            suppliesProdToFacs = globalHqBean.getAllSuppliesProdToFacs();
            FacesMessage msg = new FacesMessage("SPF Record Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            suppliesProdToFacs = globalHqBean.getAllSuppliesProdToFacs();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateSuppliesProdToFac((SuppliesProdToFac) event.getObject());
            suppliesProdToFacs = globalHqBean.getAllSuppliesProdToFacs();
            FacesMessage msg = new FacesMessage("SuppliesProdToFac Edited", ((SuppliesProdToFac) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            suppliesProdToFacs = globalHqBean.getAllSuppliesProdToFacs();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((SuppliesProdToFac) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public SuppliesProdToFac getNewSuppliesProdToFac() {
        return newSuppliesProdToFac;
    }

    public void setNewSuppliesProdToFac(SuppliesProdToFac newSuppliesProdToFac) {
        this.newSuppliesProdToFac = newSuppliesProdToFac;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SuppliesProdToFac> getFilteredSuppliesProdToFacs() {
        return filteredSuppliesProdToFacs;
    }

    public void setFilteredSuppliesProdToFacs(List<SuppliesProdToFac> filteredSuppliesProdToFacs) {
        this.filteredSuppliesProdToFacs = filteredSuppliesProdToFacs;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public GlobalHqBean getGlobalHqBean() {
        return globalHqBean;
    }

    public void setGlobalHqBean(GlobalHqBean globalHqBean) {
        this.globalHqBean = globalHqBean;
    }

    public List<SuppliesProdToFac> getSuppliesProdToFacs() {
        return suppliesProdToFacs;
    }

    public SuppliesProdToFac getSelectedSuppliesProdToFac() {
        return selectedSuppliesProdToFac;
    }

    public void setSelectedSuppliesProdToFac(SuppliesProdToFac selectedSuppliesProdToFac) {
        this.selectedSuppliesProdToFac = selectedSuppliesProdToFac;
    }

    /**
     * Creates a new instance of SuppliesProdToFacManagerBean
     */
    public SuppliesProdToFacManagerBean() {

    }

    public Long getNewSuppliesProdToFacId() {
        return newSuppliesProdToFacId;
    }

    public void setNewSuppliesProdToFacId(Long newSuppliesProdToFacId) {
        this.newSuppliesProdToFacId = newSuppliesProdToFacId;
    }

    public String getFac() {
        return fac;
    }

    public void setFac(String fac) {
        this.fac = fac;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public String getLotSize() {
        return lotSize;
    }

    public void setLotSize(String lotSize) {
        this.lotSize = lotSize;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(String leadTime) {
        this.leadTime = leadTime;
    }

    public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String supName) {
        this.supName = supName;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public Facility getNewFacility() {
        return newFacility;
    }

    public void setNewFacility(Facility newFacility) {
        this.newFacility = newFacility;
    }

    public List<String> getFacNames() {
        List<String> facName = new ArrayList<String>();
        for (int i = 0; i < facilities.size(); i++) {
            facName.add(i, facilities.get(i).getName());
        }
        return facName;
    }

    public List<String> getProdNames() {
        List<String> prodName = new ArrayList<String>();
        for (int i = 0; i < products.size(); i++) {
            prodName.add(i, products.get(i).getName());
        }
        return prodName;
    }

    public List<String> getSupNames() {
        List<String> supName = new ArrayList<String>();
        for (int i = 0; i < suppliers.size(); i++) {
            supName.add(i, suppliers.get(i).getName());
        }
        return supName;
    }

}
