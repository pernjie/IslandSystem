package managedbean;

import classes.ItemClass;
import classes.MonthClass;
import classes.StoreClass;
import classes.WeekHelper;
import entity.DistributionMFtoStore;
import entity.Facility;
import entity.Item;
import entity.RegionItemPrice;
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
import org.primefaces.context.RequestContext;
import session.stateless.MrpBean;

@ManagedBean(name = "salesHistoryBeanKitchen")
@SessionScoped
public class SalesHistoryBeanKitchen implements Serializable {

    @EJB
    private MrpBean mb;
    private List<Item> distribution;
    private Facility fac;
    private List<ItemClass> materials;
    private WeekHelper wh = new WeekHelper();

    public SalesHistoryBeanKitchen() {
    }

    @PostConstruct
    public void init() {
        fac = mb.getFacility((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email"));

        materials = new ArrayList<>();
        distribution = mb.getDistributionKit(fac);
        for (Item d : distribution) {
            System.out.println("kitc item: " + d.getName());
            List<Integer> forecast = mb.getForecast(1,
                    d, fac);
            List<Integer> production = mb.getProductionValues(fac, d);
            StoreClass store = new StoreClass();
            store.setStore(fac);
            store.setMonth1(new MonthClass(
                    forecast.get(0), production.get(0), 0));
            store.setMonth2(new MonthClass(
                    forecast.get(1), production.get(1), 0));
            store.setMonth3(new MonthClass(
                    forecast.get(2), production.get(2), 0));
            store.setMonth4(new MonthClass(
                    forecast.get(3), production.get(3), 0));
            store.setMonth5(new MonthClass(
                    forecast.get(4), production.get(4), 0));
            store.setMonth6(new MonthClass(
                    forecast.get(5), forecast.get(5), 0));

            ItemClass material = new ItemClass();
            material.setMat(d);
            materials.add(material);
            material.setStores(new ArrayList<>(Arrays.asList(store)));
        }
    }

    public void validateInput() throws IOException {
        System.out.println("initialize validateInput");
        Boolean valid = true;
        for (int i = 0; i < materials.size(); i++) {
            for (int j = 0; j < materials.get(i).getStores().size(); j++) {
                Integer input = materials.get(i).getStores().get(j).getMonth6().getPp();
                if (input < 0) {
                    String msg = materials
                            .get(i)
                            .getStores()
                            .get(j)
                            .getStore()
                            .getName() 
                            + ": " + materials.get(i).getMat().getName() + " input is not a positive integer";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            }
        }
        if (valid == true) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("materials", materials);
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect("kitchen_demand.xhtml");
        }
    }

    public boolean checkDone() {
        return mb.checkMfDoneKit(wh.getYear(5), wh.getPeriod(5), fac);
    }

    public List<ItemClass> getMaterials() {
        return materials;
    }

    public WeekHelper getWH() {
        return wh;
    }

    public Facility getFac() {
        return fac;
    }

    public void onValueChange(int index) {
        ItemClass mat = materials.get(index);
        System.out.println("mat name:" + mat.getMat().getName());
        for (StoreClass s : mat.getStores()) {
            System.out.println("update storeid:" + s.getStore());
            List<Integer> forecast = mb.getForecast(mat.getForecastType(),
                    mat.getMat(), s.getStore());
            s.getMonth1().setFc(forecast.get(0));
            s.getMonth2().setFc(forecast.get(1));
            s.getMonth3().setFc(forecast.get(2));
            s.getMonth4().setFc(forecast.get(3));
            s.getMonth5().setFc(forecast.get(4));
            s.getMonth6().setFc(forecast.get(5));
        }
        System.out.println(mat.getForecastType());
        RequestContext.getCurrentInstance().update(":form");
    }
}
