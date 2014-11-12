/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import classes.DeliveryScheduleClass;
import classes.WeekHelper;
import entity.DeliverySchedule;
import entity.Facility;
import entity.InventoryMaterial;
import entity.Item;
import entity.ProductionRecord;
import entity.PurchasePlanningRecord;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import session.stateless.ScmBean;

@Named(value = "deliveryScheduleBean")
@javax.enterprise.context.RequestScoped
public class DeliveryScheduleBean implements Serializable{
    
    @EJB
    private ScmBean sb;
    private String loggedInEmail;
    private Date currDate;
    private Integer week;
    private Integer year;
    private Facility mf;
    private int type;
    private List<ProductionRecord> records;
    private List<PurchasePlanningRecord> records2;
    private List<DeliveryScheduleClass> delivs;
    private List<DeliveryScheduleClass> delivs2;
    private WeekHelper wh;
    
    public DeliveryScheduleBean() {
    }
    
    @PostConstruct
    public void init() {
        System.err.println("function: init()");
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        mf = sb.getFac(loggedInEmail);
        System.err.println("fac: " + mf);
        currDate = wh.getCurrDate();
        week = wh.getWeek();
        year = wh.getYear();
        records = sb.getProductionRecords(mf, wh.getYear(0), wh.getPeriod(0));
        delivs = new ArrayList<>();
        for (ProductionRecord pr : records) {
            //public DeliveryScheduleClass(Long matid, Long storeid, int year, int period, int quantity, int week)
            DeliveryScheduleClass dsc = new DeliveryScheduleClass(
                    pr.getMat().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW1(), 1);
            delivs.add(dsc);
            
            dsc = new DeliveryScheduleClass(
                    pr.getMat().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW2(), 2);
            delivs.add(dsc);
            
            dsc = new DeliveryScheduleClass(
                    pr.getMat().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW3(), 3);
            delivs.add(dsc);
            
            dsc = new DeliveryScheduleClass(
                    pr.getMat().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW4(), 4);
            delivs.add(dsc);
        }
        
        records2 = sb.getPurchasePlanningRecords(mf, wh.getYear(0), wh.getPeriod(0));
        delivs2 = new ArrayList<>();
        for (PurchasePlanningRecord pr : records2) {
            //public DeliveryScheduleClass(Long matid, Long storeid, int year, int period, int quantity, int week)
            DeliveryScheduleClass dsc = new DeliveryScheduleClass(
                    pr.getProd().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW1(), 1);
            delivs2.add(dsc);
            
            dsc = new DeliveryScheduleClass(
                    pr.getProd().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW2(), 2);
            delivs2.add(dsc);
            
            dsc = new DeliveryScheduleClass(
                    pr.getProd().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW3(), 3);
            delivs2.add(dsc);
            
            dsc = new DeliveryScheduleClass(
                    pr.getProd().getId(), pr.getStore().getId(), pr.getYear(), pr.getPeriod(), pr.getQuantityW4(), 4);
            delivs2.add(dsc);
        }
    }

    public int getType() {
        return type;
    }
    
    public List<ProductionRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ProductionRecord> records) {
        this.records = records;
    }

    public List<DeliveryScheduleClass> getDelivs() {
        return delivs;
    }

    public List<PurchasePlanningRecord> getRecords2() {
        return records2;
    }

    public List<DeliveryScheduleClass> getDelivs2() {
        return delivs2;
    }
    
    public void sendDeliveryGood() {
        System.out.println("Inventory Update for delivered goods on: " + new Date());
        currDate = wh.getCurrDate();
        List<DeliverySchedule> delsch = new ArrayList<DeliverySchedule>();
        delsch = sb.getDeliverySchedule(mf.getId());

        if (!delsch.isEmpty()) {
            for (DeliverySchedule ds : delsch) {
                System.err.println("function: iterate delivery schedule: " + ds.getMat());
                InventoryMaterial im = new InventoryMaterial();
                im = sb.getInventoryMat(ds);
                System.err.println("function: inventorymaterial " + im.getMat() + " at fac " + im.getFac() + " with qty " + im.getQuantity());

                Integer qty = im.getQuantity() - ds.getQuantity();

                System.err.println("new qty: " + qty);
                if (qty > 0) {
                    im.setQuantity(qty);
                    sb.persistInventoryMaterial(im);
                }
                if (qty < im.getLowThreshold()) {
                    System.err.println("Qty below lower threshold");
                } else {
                    System.err.println("Insufficient Inventory");
                }
            }
        } else {
            System.out.println("No delivery schedule found");
        }
    }
    
}
