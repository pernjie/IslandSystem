/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.InventoryLocation;
import entity.Item;
import enumerator.InvenLoc;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import session.stateless.InventoryBean;

@ManagedBean(name = "viewRestockLocationBean")
@ViewScoped
public class ViewRestockLocationBean implements Serializable {

    @EJB
    private InventoryBean ib;
    private List<InventoryLocation> ilList;
    private List<InventoryLocation> restockList;
    private Item item;

    public ViewRestockLocationBean() {
    }
    /*
     public void setPoMatBean(PoMatBean poMatBean) {
     this.poMatBean = poMatBean;
     }
     */

    @PostConstruct
    public void init() {
        //check if new product?? TODO
        System.err.println("view restock location init():");
        ilList = new ArrayList<InventoryLocation>();
        try {
            //poRecord = poMatBean.getPoRecord();
            ilList = (List<InventoryLocation>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ilList");
        } catch (Exception ex) {
            String statusMessage = "No Items To Restock.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Generate Purchase Order Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }

        restockList = new ArrayList<InventoryLocation>();
        for (InventoryLocation il : ilList) {
            if (il.isMove()) {
                restockList.add(il);
            }
        }
        
        System.err.println("restock items: " + restockList.size());
    }

    public Long getItemId(Long id, Integer itemType) {
        System.err.println("getInvItem of id: " + id + " and type: " + itemType);
        return ib.getInvItemId(id, itemType);
    }
    
    public String getItemName(Long id, Integer itemType) { 
        return ib.getInvItemName(id, itemType);
    }
    
    public String getZone(Long id) {
        return ib.getZone(id);
    }
    
    public String getShelf(Long id) {
        return ib.getShelf(id);
    }
    
    public Integer getPosition(Long id) {
        return ib.getPosition(id);
    }
    
    public void quit() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ilList", ilList);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../inventory/inventory_home.xhtml");
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
