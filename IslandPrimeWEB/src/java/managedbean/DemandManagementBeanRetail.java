/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import classes.ItemWeek;
import classes.ItemClass;
import classes.StoreClass;
import classes.StoreClassWeekly;
import classes.WeekHelper;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author pern
 */
@ManagedBean(name = "demandRetailBean")
@SessionScoped
public class DemandManagementBeanRetail implements Serializable {
    private List<ItemWeek> weeklyDemand;
    private WeekHelper wh = new WeekHelper();
    @Inject
    private SalesHistoryBeanRetail rhb;
    private List<ItemClass> prods;

    public DemandManagementBeanRetail() {
    }
    
    @PostConstruct
    public void init() {
        weeklyDemand = new ArrayList<>();
        prods = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("products");
        for (ItemClass m : prods) {
            for (StoreClass store : m.getStores()) {
                ItemWeek matWeek = new ItemWeek();
                matWeek.setMat(m.getMat());
                StoreClassWeekly scw = new StoreClassWeekly();
                scw.setStore(store.getStore());
                scw.setWeek1(wh.getWeeklyDemand(store.getMonth6().getPp(),1));
                scw.setWeek2(wh.getWeeklyDemand(store.getMonth6().getPp(),2));
                scw.setWeek3(wh.getWeeklyDemand(store.getMonth6().getPp(),3));
                scw.setWeek4(wh.getWeeklyDemand(store.getMonth6().getPp(),4));
                scw.setTotal(scw.getWeek1() + scw.getWeek2() + scw.getWeek3() + scw.getWeek4());
                matWeek.getStores().add(scw);
                weeklyDemand.add(matWeek);
            }
        }
    }
    
      public void validateInput() throws IOException {
        System.out.println("initialize validateInput");
        Boolean valid = true;
        for (int i = 0; i < weeklyDemand.size(); i++) {
            for (int j = 0; j < weeklyDemand.get(i).getStores().size(); j++) {
                Integer w1 = weeklyDemand.get(i).getStores().get(j).getWeek1();
                Integer w2 = weeklyDemand.get(i).getStores().get(j).getWeek2();
                Integer w3 = weeklyDemand.get(i).getStores().get(j).getWeek3();
                Integer w4 = weeklyDemand.get(i).getStores().get(j).getWeek4();
                Integer total = weeklyDemand.get(i).getStores().get(j).getTotal();
                if (w1 < 0 || w2 < 0 || w3 < 0 || w4 < 0) {
                    String msg = weeklyDemand.get(i).getStores().get(j).getStore().getName()
                            + ": " + weeklyDemand.get(i).getMat().getName() + " has negative values.";
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
                if (w1 + w2 + w3 + w4 != total) {

                    String msg = weeklyDemand.get(i).getStores().get(j).getStore().getName()
                            + ": " + weeklyDemand.get(i).getMat().getName() + " does not add up to total.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            }
        }
        if (valid == true) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("weeklyDemand", weeklyDemand);
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect("mrp_mrp_retail.xhtml");
        }
    }

    public List<ItemWeek> getWeeklyDemand() {
        return weeklyDemand;
    }

    public WeekHelper getWh() {
        return wh;
    }
}
