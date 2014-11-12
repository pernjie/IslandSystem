/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import classes.WeekHelper;
import entity.Bill;
import entity.Facility;
import entity.Supplier;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import session.stateless.ScmBean;

/**
 *
 * @author AdminNUS
 */
@ManagedBean(name = "billManagementBean")
@ViewScoped
public class BillManagementBean {

    @EJB
    private ScmBean sb;
    private List<Bill> unpaidBills;
    private String loggedInEmail;
    private List<Supplier> suppliers;
    private String supplier;
    private Supplier sup;
    private Facility fac;
    private Date currDate;
    private WeekHelper wh;
    private Long po;
    private String statusMessage;
    
    public BillManagementBean() {
    }
    
    @PostConstruct
    public void init() {
        System.out.println("init");loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        fac = sb.getFac(loggedInEmail);
        //suppliers = ib.getMatFacilities(fac);
        //suppliers.addAll(ib.getProdFacilities(fac));
        suppliers = sb.getMatSuppliers(fac);
        suppliers.addAll(sb.getProdSuppliers(fac));
        if (suppliers.isEmpty()) {
            System.err.println("no suppliers found!");
        }
        currDate = wh.getCurrDate();
        try {
            unpaidBills = sb.getUnpaidBills();
        } catch (Exception e) {
            statusMessage = "New Inventory Ingredient Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Inventory Ingredient Record Result: "
                    + statusMessage, ""));
        }
    }

    public List<Bill> getUnpaidBills() {
        return unpaidBills;
    }

    public void setUnpaidBills(List<Bill> unpaidBills) {
        this.unpaidBills = unpaidBills;
    }
    
    
    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Supplier getSup() {
        return sup;
    }

    public void setSup(Supplier sup) {
        this.sup = sup;
    }
    
    
}