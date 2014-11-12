/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.CartItem;
import entity.Item;
import entity.Region;
import entity.RegionItemPrice;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import session.stateless.EComBean;

/**
 *
 * @author Anna
 */
@ManagedBean(name = "itemDetailsBean")
@ViewScoped
public class ItemDetailsBean implements Serializable {
    
    @Inject
    private ShoppingCartBean scb;
    @EJB
    private EComBean eb;
    private long regionId;
    private long selectedItemId;
    private Item selectedItemRes;
    private String selectedItemName;
    private String tempItemValue;
    private long itemId;
    private int addQty;
    private boolean added;

    public int getAddQty() {
        return addQty;
    }

    public void setAddQty(int addQty) {
        System.out.println("addQty: " + addQty);
        this.addQty = addQty;
    }

    public void ItemDetailsMB() {

    }

    @PostConstruct
    public void init() {
        selectedItemId = (long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("itemId");
        System.out.println("ITEM: " + selectedItemId);
        selectedItemRes = eb.getItem(selectedItemId);
        System.out.println("ITEM: " + selectedItemRes);
        added = false;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public void addCart() {
        System.out.println("addqty: " + 1);
        System.out.println("item id: " + selectedItemRes.getId());
        List<CartItem> cart;
        cart = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cart");
        boolean found = false;
        for (int i=0;i<cart.size();i++) {
            if (Objects.equals(cart.get(i).getItem().getId(), selectedItemRes.getId())) {
                found = true;
                cart.get(i).setQuantity(cart.get(i).getQuantity() + 1);
            }
        }
        if (!found) {
            CartItem ci = new CartItem();
            ci.setItem(eb.getItem(selectedItemRes.getId()));
            ci.setPrice(calcItemPrice());
            ci.setQuantity(1);
            cart.add(ci);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("cart");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cart", cart);
        added = true;
    }

    public List<String> completeText(String query) {
        System.out.println("IN COMPLETE TEXT");
        regionId = (long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("regionId");
        List<Item> allItems = eb.getItems(regionId);
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
        Item itemResult = eb.getItembyString(tempItemValue);
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
            Item itemResult = eb.getItembyString(selectedItemName);
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

    public EComBean getEComBean() {
        return eb;
    }

    public void setEComBean(EComBean eb) {
        this.eb = eb;
    }

    public Item getSelectedItemRes() {
        return selectedItemRes;
    }

    public void setSelectedItemRes(Item selectedItemRes) {
        this.selectedItemRes = selectedItemRes;
    }

    public long getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(long selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String calcItemPrice() {
        System.out.println("CALC ITEM PRICE");
        System.out.println("SELECTED ITEM ENTITY:" + selectedItemRes);
        long regionId = (long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("regionId");
        Region resultRegion = eb.getRegion(regionId);
        RegionItemPrice regionPriceRecord = eb.getRegionPriceRecord(selectedItemRes, resultRegion);
        System.out.println(resultRegion);
        System.out.println(regionPriceRecord);
        System.out.println("COUNTRY: " + resultRegion.getCountry());
        System.out.println("EXCHANGE RATE: " + resultRegion.getCountry().getExchangeRate());
        System.out.println("PRICE: " + regionPriceRecord.getPrice());
        Double exchangeRate = resultRegion.getCountry().getExchangeRate();
        Double price = regionPriceRecord.getPrice();
        Double resultPrice = price * exchangeRate;

        DecimalFormat df = new DecimalFormat("#.00");
        String priceTemp = df.format(resultPrice);

        String currency = resultRegion.getCountry().getCurrency();

        String priceDisplay = currency + " " + priceTemp;
        System.out.println("PRICE DISPPLAY: " + priceDisplay);

        return priceDisplay;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public void setSelectedItemName(String selectedItemName) {
        this.selectedItemName = selectedItemName;
    }

    public String getTempItemValue() {
        return tempItemValue;
    }

    public void setTempItemValue(String tempItemValue) {
        this.tempItemValue = tempItemValue;
    }

}
