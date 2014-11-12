/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import classes.CartItem;
import entity.Region;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import session.stateless.EComBean;

/**
 *
 * @author pern
 */
@ManagedBean(name = "shoppingCartBean")
@RequestScoped
public class ShoppingCartBean {
    @EJB
    private EComBean eb;
    private List<CartItem> cart;
    private long regionId;
    private long itemId;
    private long editId;
    private String qr;
    private double totalCost;
    private String costString;

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public long getEditId() {
        return editId;
    }

    public void setEditId(long editId) {
        this.editId = editId;
    }
    private int editQty;

    public int getEditQty() {
        return editQty;
    }

    public void setEditQty(int editQty) {
        this.editQty = editQty;
    }
    
    public void saveItemId(long id) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("editId", id);
    }
    
    @PostConstruct
    public void init() {
        try {
            regionId = (long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("regionId");
        }
        catch (NullPointerException e) {
            regionId = 6L;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regionId", regionId);
        }
        cart = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cart");
        qr = generateQR();
        System.out.println("qr: " + qr);
        calculateCost();
    }

    public ShoppingCartBean() {
    }
    
    public void calculateCost() {
        totalCost = 0.0;
        for (int i=0;i<cart.size();i++) {
            totalCost += (Double.valueOf(cart.get(i).getPrice().split(" ")[1]) * cart.get(i).getQuantity());
        }
        costString = "";
        Region resultRegion = eb.getRegion(regionId);
        DecimalFormat df = new DecimalFormat("#.00");
        String priceTemp = df.format(totalCost);
        costString = resultRegion.getCountry().getCurrency() + " " + priceTemp;
    }

    public String getCostString() {
        return costString;
    }

    public void setCostString(String costString) {
        this.costString = costString;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public void setCart(List<CartItem> cart) {
        this.cart = cart;
    }
    
    public void removeItem() {
        System.out.println("remove itemid: " + itemId);
        cart = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cart");
        CartItem ci = null;
        for (int i=0;i<cart.size();i++) {
            if (cart.get(i).getItem().getId() == itemId)
                ci = cart.get(i);
        }
        cart.remove(ci);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("cart");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cart", cart);
        calculateCost();
        
        RequestContext.getCurrentInstance().update("form:carttable");
        RequestContext.getCurrentInstance().update("form:qrpanel");
        RequestContext.getCurrentInstance().update("form:costpanel");
    }
    
    public void editItem() {
        System.out.println("change itemid: " + FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("editId"));
        cart = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cart");
        CartItem ci = null;
        for (int i=0;i<cart.size();i++) {
            if (Objects.equals(cart.get(i).getItem().getId(), FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("editId"))) {
                ci = cart.get(i);
                System.out.println("editqty: " + editQty);
                ci.setQuantity(editQty);
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("cart");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cart", cart);
        calculateCost();
        qr = generateQR();
        
        RequestContext.getCurrentInstance().update("form:carttable");
        RequestContext.getCurrentInstance().update("form:qrpanel");
        RequestContext.getCurrentInstance().update("form:costpanel");
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
    
    public String generateQR() {
        String str = "";
        if (cart.isEmpty())
            return null;
        for (CartItem ci : cart) {
            str += ci.getItem().getId() + "-" + ci.getQuantity()+ ",";
        }
        return str.substring(0, str.length()-1);
    }
}
