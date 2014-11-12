/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.SetItem;
import java.io.Serializable;
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
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "setItemManagerBean")
@ViewScoped
public class SetItemManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private Long id;
    private String name;
    private String description;
    private Long newSetItemId;
    private String statusMessage;
    private SetItem selectedSetItem;
    private List<SetItem> filteredSetItems;
    private List<SetItem> setItems;
    private SetItem newSetItem = new SetItem();

    @PostConstruct

    public void init() {
        setItems = globalHqBean.getAllSetItems();
    }

    public void saveNewSetItem(ActionEvent event) {
        try {
            newSetItem = new SetItem();
            newSetItem.setName(name);
            newSetItem.setShortDesc(description);
            newSetItemId = globalHqBean.addNewSetItem(newSetItem);
            statusMessage = "New SetItem Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SetItem Result: "
                    + statusMessage + " (New SetItem ID is " + newSetItemId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newSetItemId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New SetItem Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newSetItemId = -1L;
            statusMessage = "New SetItem Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New SetItem Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteSetItem() {
        try {
            globalHqBean.removeSetItem(selectedSetItem);
            setItems = globalHqBean.getAllSetItems();
            FacesMessage msg = new FacesMessage("SetItem Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            setItems = globalHqBean.getAllSetItems();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateSetItem((SetItem) event.getObject());
            setItems = globalHqBean.getAllSetItems();
            FacesMessage msg = new FacesMessage("SetItem Edited", ((SetItem) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            setItems = globalHqBean.getAllSetItems();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((SetItem) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public SetItem getNewSetItem() {
        return newSetItem;
    }

    public void setNewSetItem(SetItem newSetItem) {
        this.newSetItem = newSetItem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SetItem> getFilteredSetItems() {
        return filteredSetItems;
    }

    public void setFilteredSetItems(List<SetItem> filteredSetItems) {
        this.filteredSetItems = filteredSetItems;
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

    public List<SetItem> getSetItems() {
        return setItems;
    }

    public SetItem getSelectedSetItem() {
        return selectedSetItem;
    }

    public void setSelectedSetItem(SetItem selectedSetItem) {
        this.selectedSetItem = selectedSetItem;
    }

    /**
     * Creates a new instance of SetItemManagerBean
     */
    public SetItemManagerBean() {

    }

    public Long getNewSetItemId() {
        return newSetItemId;
    }

    public void setNewSetItemId(Long newSetItemId) {
        this.newSetItemId = newSetItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
