package managedbean;

import classes.WeekHelper;
import entity.Facility;
import entity.ProductionOrder;
import entity.ProductionRecord;
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

@ManagedBean(name = "productionOrderBean")
@ViewScoped
public class ProductionOrderBean implements Serializable {

    private Facility mf;
    private List<ProductionRecord> records;
    private List<ProductionOrder> orders;
    private String order;
    private WeekHelper wh = new WeekHelper();
    ProductionOrder currOrder;
    @EJB
    private MrpBean mb;

    @PostConstruct
    public void init() {
        //records = mb.getProductionRecords(1L, order, 2014, 10);
        mf = mb.getFacility((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email"));
        orders = mb.getProductionOrders();
    }

    public List<ProductionRecord> getRecords() {
        return records;
    }

    public List<ProductionOrder> getOrders() {
        System.out.println("getting orders");
        return orders;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public ProductionOrder getCurrOrder() {
        return currOrder;
    }

    public void setCurrOrder(ProductionOrder currOrder) {
        this.currOrder = currOrder;
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            mb.persistRecord((ProductionRecord) event.getObject());
            FacesMessage msg = new FacesMessage("Record Edited",
                    "Store " + ((ProductionRecord) event.getObject()).getStore() + " record");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(e.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Record Cancelled",
                "Store " + ((ProductionRecord) event.getObject()).getStore() + " record");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateTable() {
        currOrder = mb.getProductionOrder(Long.valueOf(order));
        records = mb.getProductionRecords(mf, currOrder.getMat(), wh.getYear(0), wh.getPeriod(0));
    }

    public void approveOrder() {
        currOrder.setStatus("approved");
        mb.updateProductionOrder(currOrder);
        orders = mb.getProductionOrders();
        currOrder = null;
        records = null;
    }

    public void rejectOrder() {
        currOrder.setStatus("rejected");
        mb.updateProductionOrder(currOrder);
        orders = mb.getProductionOrders();
        currOrder = null;
        records = null;
    }
}
