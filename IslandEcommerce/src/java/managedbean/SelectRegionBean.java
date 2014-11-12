package managedbean;

import classes.CartItem;
import entity.Item;
import entity.Material;
import entity.Region;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import session.stateless.EComBean;

@ManagedBean(name = "selectRegionBean")
@SessionScoped
public class SelectRegionBean implements Serializable {

    private List<Region> regions;

    private Region selectedRegion;

    private long regionId;

    @EJB
    private EComBean eComBean;

    public void DisplayInventoryMB() {

    }

    @PostConstruct
    public void init() {
        regions = eComBean.getRegions();
        List<CartItem> cart = new ArrayList<>();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cart", cart);
    }

    public List<Region> getRegions() {
        return regions;
    }

    public Region getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(Region selectedRegion) {
        this.selectedRegion = selectedRegion;
    }

    public EComBean getEComBean() {
        return eComBean;
    }

    public void setEComBean(EComBean eComBean) {
        this.eComBean = eComBean;
    }

    public void regionDirect() {
        Region region = null;
        if (regionId == 0L) {
            regionId = 1L;
        }
        for (int i=0;i<regions.size();i++) {
            if (regions.get(i).getId() == regionId)
                region = regions.get(i);
        }
        selectedRegion = region;
        try {
            System.out.println("HERE!");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regionId", regionId);
            FacesContext.getCurrentInstance().getExternalContext().redirect("../ecom/index.xhtml");
        } catch (IOException ex) {
        }
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

}
