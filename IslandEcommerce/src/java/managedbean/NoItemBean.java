/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Item;
import entity.Region;
import entity.RegionItemPrice;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
import session.stateless.EComBean;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "noItemBean")
@ViewScoped
public class NoItemBean implements Serializable {

    @EJB
    private EComBean eComBean;

    private long selectedItem;
    private long regionId;
    private String tempItemValue;
    private long itemId;
    private String selectedItemName;

    public void ItemDetailsMB() {

    }

    @PostConstruct
    public void init() {
        selectedItemName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedItemName");
    }

    public List<String> completeText(String query) {
        System.out.println("IN COMPLETE TEXT");
        regionId = (long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("regionId");
        List<Item> allItems = eComBean.getItems(regionId);
        List<String> filteredResults = new ArrayList<String>();

        for (Item indiv : allItems) {
            if (indiv.getName().toLowerCase().contains(query)) {
                filteredResults.add(indiv.getName());
            }
        }

        return filteredResults;
    }

    public void handleSelect(SelectEvent event) {

        tempItemValue = (String) event.getObject();
        Item itemResult = eComBean.getItembyString(tempItemValue);
        itemId = itemResult.getId();
        System.out.println("HANDLE_SELECTED: " + tempItemValue);

        System.out.println("HANDLE_SELECTED 2: " + itemResult);
        itemDetails();
    }

    public void search(ActionEvent event) {
        System.out.println("IN Search!");
        System.out.print("ITEM: " + selectedItemName);
        if (!selectedItemName.isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regionId", regionId);
            Item itemResult = eComBean.getItembyString(selectedItemName);
            if (itemResult == null) {
                try {
                    System.out.println("HERE NULL");
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedItemName", selectedItemName);
                    FacesContext.getCurrentInstance().getExternalContext().redirect("../ecom/ecom_No_Item.xhtml");
                } catch (Exception ex) {

                }
            } else {
                itemId = itemResult.getId();
                System.out.println("HANDLE_SELECTED: " + selectedItemName);

                System.out.println("HANDLE_SELECTED 2: " + itemResult);
                itemDetails();
            }
            System.out.println(selectedItemName);
        }
    }

    public void itemDetails() {

        // itemId = (String)FacesContext.getExternalContext().getRequestParameterMap().get("itemId");   
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("itemId", itemId);
        System.out.println("PRINT: " + itemId);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../ecom/ecom_Item_Display.xhtml");
        } catch (IOException ex) {

        }
       //itemId = (String) event.getComponent().getAttributes().get(itemId);

    }

    public long getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(long selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public void setSelectedItemName(String selectedItemName) {
        this.selectedItemName = selectedItemName;
    }

}
