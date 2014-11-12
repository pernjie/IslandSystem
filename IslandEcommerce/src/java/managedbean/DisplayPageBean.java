package managedbean;

import classes.CartItem;
import entity.Item;
import entity.Material;
import entity.Region;
import entity.RegionItemPrice;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import session.stateless.EComBean;

@ManagedBean(name = "displayPageBean")
@ViewScoped
public class DisplayPageBean implements Serializable {

    private List<RegionItemPrice> furns;
    private long itemId;
    private long regionId;

    private String tempItemValue;
    private String selectedItemName;
    private Item item;
    private String category;
    private List<String> cats;

    @EJB
    private EComBean eb;
    

    @PostConstruct
    public void init() {
        selectedItemName = null;
        try {
            regionId = (long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("regionId");
        }
        catch (NullPointerException e) {
            regionId = 6L;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regionId", regionId);
        }
        System.out.println("REGION ID: " + regionId);
        furns = eb.getFurnitureDisplays(regionId);
        cats = new ArrayList<>(Arrays.asList("Living Room","Bathroom","Garden","Kitchen","Accessories","Bedroom","Children","Seasonal","Office"));
    }

    public List<String> getCats() {
        return cats;
    }

    public void setCats(List<String> cats) {
        this.cats = cats;
    }

    public List<RegionItemPrice> getFurns() {
        return furns;
    }

    public EComBean getEComBean() {
        return eb;
    }

    public void setEComBean(EComBean eb) {
        this.eb = eb;
    }
    
    public void categorySelectionChanged() {
        System.out.println("furns before size: " + furns.size());
        int selected = 0;
        switch (category) {
            case "Living Room": selected = 1; break;
            case "Bathroom": selected = 2; break;
            case "Garden": selected = 3; break;
            case "Kitchen": selected = 4; break;   
            case "Accessories": selected = 5; break;
            case "Bedroom": selected = 6; break;
            case "Children": selected = 7; break;
            case "Seasonal": selected = 8; break;
            case "Office": selected = 9; break;
        }
        System.out.println("cat: " + selected);
        furns = eb.getFurnitureDisplays(regionId);
        List<RegionItemPrice> filtered = new ArrayList<>();
        for (RegionItemPrice rip: furns) {
            System.out.println("mat " + rip.getItem().getName());
            Material mat = eb.getItem(rip.getItem().getId());
            if (mat.getGenCategory() == selected)
                filtered.add(rip);
        }
        furns = filtered;
        System.out.println("furns size: " + furns.size());
        RequestContext.getCurrentInstance().update("form:furns");
    }

    public void itemDetails() {

        // itemId = (String)FacesContext.getExternalContext().getRequestParameterMap().get("itemId");   
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("itemId", itemId);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../ecom/ecom_Item_Display.xhtml");
        } catch (IOException ex) {

        }
       //itemId = (String) event.getComponent().getAttributes().get(itemId);

    }

    public String calcItemPrice(Double price, Region region) {
        Double exchangeRate = region.getCountry().getExchangeRate();
        Double resultPrice = price * exchangeRate;

        DecimalFormat df = new DecimalFormat("#.00");
        String priceTemp = df.format(resultPrice);

        String currency = region.getCountry().getCurrency();

        String priceDisplay = currency + " " + priceTemp;
        System.out.println("PRICE DISPPLAY: " + priceDisplay);

        return priceDisplay;
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

    public long getItemId() {
        return itemId;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getTempItemValue() {
        return tempItemValue;
    }

    public void setTempItemValue(String tempItemValue) {
        this.tempItemValue = tempItemValue;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public void setSelectedItemName(String selectedItemName) {
        this.selectedItemName = selectedItemName;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
