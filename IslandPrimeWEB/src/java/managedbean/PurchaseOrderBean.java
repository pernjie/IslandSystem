package managedbean;
 
import classes.WeekHelper;
import entity.Facility;
import entity.Item;
import entity.PurchasePlanningOrder;
import entity.PurchasePlanningRecord;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;
import session.stateless.MrpBean;
 
@ManagedBean(name="purchaseOrderBean")
@ViewScoped
public class PurchaseOrderBean implements Serializable {
     
    private Facility mf;
    private List<PurchasePlanningRecord> records;
    private List<PurchasePlanningOrder> orders;
    private String order;
    private WeekHelper wh = new WeekHelper();
    PurchasePlanningOrder currOrder;
    @EJB
    private MrpBean mb;
    @PostConstruct
    public void init() {
        //records = mb.getProductionRecords(1L, order, 2014, 10);
        mf = mb.getFacility((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email"));
        orders = mb.getPurchasePlanningOrders();
    }
 
    public List<PurchasePlanningRecord> getRecords() {
        return records;
    }

    public List<PurchasePlanningOrder> getOrders() {
        System.out.println("getting orders");
        return orders;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public PurchasePlanningOrder getCurrOrder() {
        return currOrder;
    }

    public void setCurrOrder(PurchasePlanningOrder currOrder) {
        this.currOrder = currOrder;
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Record Edited", 
                "Store " + ((PurchasePlanningRecord) event.getObject()).getStore() + " record");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        mb.persistRecordProd((PurchasePlanningRecord)event.getObject());
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Record Cancelled", 
                "Store " + ((PurchasePlanningRecord) event.getObject()).getStore() + " record");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void updateTable() {
        currOrder = mb.getPurchasePlanningOrder(Long.valueOf(order));
        records = mb.getPurchasePlanningRecords(mf, currOrder.getProd(), wh.getYear(0), wh.getPeriod(0));
    }
    
    public void approveOrder() {
        currOrder.setStatus("approved");
        mb.updatePurchasePlanningOrder(currOrder);
        orders = mb.getPurchasePlanningOrders();
        currOrder = null;
        records = null;
    }
    
    public void rejectOrder() {
        currOrder.setStatus("rejected");
        mb.updatePurchasePlanningOrder(currOrder);
        orders = mb.getPurchasePlanningOrders();
        currOrder = null;
        records = null;
    }
}