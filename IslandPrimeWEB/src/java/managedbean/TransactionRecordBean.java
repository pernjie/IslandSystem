/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

//import static entity.Component_.matId;
import entity.Facility;
import entity.Region;
import entity.TransactionRecord;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
import session.stateless.OpCrmBean;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "transactionRecordBean")
@ViewScoped
public class TransactionRecordBean implements Serializable {

    @EJB
    private OpCrmBean ocb;
    private Long id;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private String statusMessage;
    private TransactionRecord selectedTransactionRecord;
    private List<TransactionRecord> filteredTransactionRecords;
    private List<TransactionRecord> transactionRecords;


    @PostConstruct
    public void init() {
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);
        transactionRecords = ocb.getRegionTransactionRecords(userRegion);
    }
public void onRowEdit(RowEditEvent event) {

        try {
            ocb.updateTransactionRecord((TransactionRecord) event.getObject());
            transactionRecords = ocb.getRegionTransactionRecords(userRegion);
            FacesMessage msg = new FacesMessage("Transaction Record Updated", ((TransactionRecord) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((TransactionRecord) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void viewSelectedTransactionRecord() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedTransactionRecord", selectedTransactionRecord);
            FacesContext.getCurrentInstance().getExternalContext().redirect("op_view_selected_transaction_record.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OpCrmBean getOcb() {
        return ocb;
    }

    public void setOcb(OpCrmBean ocb) {
        this.ocb = ocb;
    }

    public Region getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(Region userRegion) {
        this.userRegion = userRegion;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Facility getUserFacility() {
        return userFacility;
    }

    public void setUserFacility(Facility userFacility) {
        this.userFacility = userFacility;
    }
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public TransactionRecord getSelectedTransactionRecord() {
        return selectedTransactionRecord;
    }

    public void setSelectedTransactionRecord(TransactionRecord selectedTransactionRecord) {
        this.selectedTransactionRecord = selectedTransactionRecord;
    }

    public List<TransactionRecord> getFilteredTransactionRecords() {
        return filteredTransactionRecords;
    }

    public void setFilteredTransactionRecords(List<TransactionRecord> filteredTransactionRecords) {
        this.filteredTransactionRecords = filteredTransactionRecords;
    }

    public List<TransactionRecord> getTransactionRecords() {
        return transactionRecords;
    }

    public void setTransactionRecords(List<TransactionRecord> transactionRecords) {
        this.transactionRecords = transactionRecords;
    }

}
