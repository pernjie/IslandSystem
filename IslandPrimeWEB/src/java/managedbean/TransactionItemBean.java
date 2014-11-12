/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Facility;
import entity.Region;
import entity.TransactionItem;
import entity.TransactionRecord;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "transactionItemBean")
@SessionScoped
public class TransactionItemBean implements Serializable {

    @EJB
    private OpCrmBean ocb;
    private Long id;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private Long newTransactionRecordId;
    private Long newTransactionItemId;
    private String statusMessage;
    private TransactionRecord selectedTransactionRecord;
    private TransactionItem selectedTransactionItem;
    private List<TransactionItem> transactionItems;
    private List<TransactionItem> filteredTransactionItems;
    private Boolean transantionItemsIsNotEmpty;

    @PostConstruct
    public void init() {
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        selectedTransactionRecord = (TransactionRecord) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedTransactionRecord");
        transactionItems = ocb.getTransactionItems(selectedTransactionRecord);
        transantionItemsIsNotEmpty = !transactionItems.isEmpty();
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);

    }

    public void onRowEdit(RowEditEvent event) {

        try {
            TransactionItem ti = (TransactionItem) event.getObject();
            if (ti.getReturnedQty() > ti.getQuantity()) {
                statusMessage = "Returned Quantity cannot be greater than Purchased Quantity";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
            } else {
                ocb.updateTransactionItem(ti);
                transactionItems = ocb.getTransactionItems(selectedTransactionRecord);
                selectedTransactionRecord = ocb.getSelectedTransactionRecord(selectedTransactionRecord.getId());
                FacesMessage msg = new FacesMessage("Transaction Item Edited", (ti.getId().toString()));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (DetailsConflictException dcx) {
            transactionItems = ocb.getTransactionItems(selectedTransactionRecord);
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: Please enter a valid number", ""));
            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((TransactionItem) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(List<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }

    public Long getNewTransactionItemId() {
        return newTransactionItemId;
    }

    public void setNewTransactionItemId(Long newTransactionItemId) {
        this.newTransactionItemId = newTransactionItemId;
    }

    public OpCrmBean getOcb() {
        return ocb;
    }

    public void setOcb(OpCrmBean ocb) {
        this.ocb = ocb;
    }



    public Long getId() {
        return id;
    }

    public Boolean getTransantionItemsIsNotEmpty() {
        return transantionItemsIsNotEmpty;
    }

    public void setTransantionItemsIsNotEmpty(Boolean transantionItemsIsNotEmpty) {
        this.transantionItemsIsNotEmpty = transantionItemsIsNotEmpty;
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

    public Long getNewTransactionRecordId() {
        return newTransactionRecordId;
    }

    public void setNewTransactionRecordId(Long newTransactionRecordId) {
        this.newTransactionRecordId = newTransactionRecordId;
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

    public Region getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(Region userRegion) {
        this.userRegion = userRegion;
    }

    public List<TransactionItem> getFilteredTransactionItems() {
        return filteredTransactionItems;
    }

    public void setFilteredTransactionItems(List<TransactionItem> filteredTransactionItems) {
        this.filteredTransactionItems = filteredTransactionItems;
    }

    public TransactionItem getSelectedTransactionItem() {
        return selectedTransactionItem;
    }

    public void setSelectedTransactionItem(TransactionItem selectedTransactionItem) {
        this.selectedTransactionItem = selectedTransactionItem;
    }

}
