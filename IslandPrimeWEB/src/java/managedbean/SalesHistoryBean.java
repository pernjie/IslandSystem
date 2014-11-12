package managedbean;

import classes.ItemClass;
import classes.MonthClass;
import classes.StoreClass;
import classes.WeekHelper;
import entity.DistributionMFtoStore;
import entity.Facility;
import entity.Item;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import session.stateless.MrpBean;

@ManagedBean(name = "salesHistoryBean")
@SessionScoped
public class SalesHistoryBean implements Serializable {

    @EJB
    private MrpBean mb;
    private List<DistributionMFtoStore> distribution;
    private Facility mf;
    private List<ItemClass> materials;
    private WeekHelper wh = new WeekHelper();

    public SalesHistoryBean() {
    }

    @PostConstruct
    public void init() {
        mf = mb.getFacility((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email"));

        materials = new ArrayList<ItemClass>();
        distribution = mb.getDistribution(mf);
        for (DistributionMFtoStore d : distribution) {
            Item item = new Item() {};
            item = mb.getItem(d.getMat().getId());
            StoreClass store = new StoreClass();
            store.setStore(d.getStore());
            List<Integer> forecast = mb.getForecast(1,
                    item, store.getStore());
            List<Integer> production = mb.getProductionValues(store.getStore(), item);
            int inventory = mb.getMatInventory(d.getStore(), d.getMat());
            store.setMonth1(new MonthClass(
                    forecast.get(0), production.get(0), inventory));
            inventory = inventory - forecast.get(0) + production.get(0);
            store.setMonth2(new MonthClass(
                    forecast.get(1), production.get(1), inventory));
            inventory = inventory - forecast.get(1) + production.get(1);
            store.setMonth3(new MonthClass(
                    forecast.get(2), production.get(2), inventory));
            inventory = inventory - forecast.get(2) + production.get(2);
            store.setMonth4(new MonthClass(
                    forecast.get(3), production.get(3), inventory));
            inventory = inventory - forecast.get(3) + production.get(3);
            store.setMonth5(new MonthClass(
                    forecast.get(4), production.get(4), inventory));
            inventory = inventory - forecast.get(4) + production.get(4);
            store.setMonth6(new MonthClass(
                    forecast.get(5), forecast.get(5), inventory));
            //find if exist
            boolean found = false;
            for (int i = 0; i < materials.size(); i++) {
                if (d.getMat() == materials.get(i).getMat()) {
                    materials.get(i).getStores().add(store);
                    found = true;
                    break;
                }
            }
            if (!found) {
                ItemClass material = new ItemClass();
                material.setMat(item);
                materials.add(material);
                material.setStores(new ArrayList<StoreClass>(Arrays.asList(store)));
            }
        }
    }

    public void validateInput() throws IOException {
        System.out.println("initialize validateInput");
        Boolean valid = true;
        for (int i = 0; i < materials.size(); i++) {
            for (int j = 0; j < materials.get(i).getStores().size(); j++) {
                Integer input = materials.get(i).getStores().get(j).getMonth6().getPp();
                if (input < 0) {
                    String msg = materials.get(i).getStores().get(j).getStore().getName() + ": " + materials.get(i).getMat().getName() + " input is not a positive integer";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            }
        }
        if (valid == true) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("materials", materials);
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect("mrp_demand.xhtml");
        }
    }

    public boolean checkDone() {
        return mb.checkMfDone(wh.getYear(5), wh.getPeriod(5), mf);
    }

    public List<ItemClass> getMaterials() {
        return materials;
    }

    public WeekHelper getWH() {
        return wh;
    }

    public Facility getMf() {
        return mf;
    }

    public void onValueChange(int index) {
        ItemClass mat = materials.get(index);
        System.out.println("mat name:" + mat.getMat().getName());
        for (StoreClass s : mat.getStores()) {
            System.out.println("update storeid:" + s.getStore());
            List<Integer> forecast = mb.getForecast(mat.getForecastType(),
                    mat.getMat(), s.getStore());
            int inventory = s.getMonth1().getInv();
            s.getMonth1().setFc(forecast.get(0));
            inventory = inventory + s.getMonth1().getPp() - s.getMonth1().getFc();
            s.getMonth2().setInv(inventory);
            s.getMonth2().setFc(forecast.get(1));
            inventory = inventory + s.getMonth2().getPp() - s.getMonth2().getFc();
            s.getMonth3().setInv(inventory);
            s.getMonth3().setFc(forecast.get(2));
            inventory = inventory + s.getMonth3().getPp() - s.getMonth3().getFc();
            s.getMonth4().setInv(inventory);
            s.getMonth4().setFc(forecast.get(3));
            inventory = inventory + s.getMonth4().getPp() - s.getMonth4().getFc();
            s.getMonth5().setInv(inventory);
            s.getMonth5().setFc(forecast.get(4));
            inventory = inventory + s.getMonth5().getPp() - s.getMonth5().getFc();
            s.getMonth6().setInv(inventory);
            s.getMonth6().setFc(forecast.get(5));
        }
        System.out.println(mat.getForecastType());
        RequestContext.getCurrentInstance().update(":form");
    }
}
