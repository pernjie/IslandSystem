/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Campaign;
import entity.Facility;
import entity.Region;
import enumerator.AnalAxis;
import enumerator.CustomerCampaignCat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;
import session.stateless.AnalCrmBean;
import session.stateless.OpCrmBean;

/**
 *
 * @author DY
 */
@ManagedBean(name = "analDisplayCampaignMetricsBean")
@ViewScoped
public class AnalDisplayCampaignMetricsBean implements Serializable {

    @EJB
    AnalCrmBean anal;
    @EJB
    OpCrmBean ocb;
    private String statusMessage;
    private Campaign selectedCampaign;
    private List<Campaign> campaigns;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private PieChartModel numCustPie;
    private PieChartModel numPromoCodePie;
    private PieChartModel numHitPie;
    private LineChartModel numCustAgainstAge;
    private BarChartModel numCustAgainstGender;
    private LineChartModel promoCodeAgainstAge;
    private BarChartModel promoCodeAgainstGender;
    private LineChartModel numHitAgainstAge;
    private BarChartModel numHitAgainstGender;
    private Integer totalNumCust;
    private Integer totalNumPromoCode;
    private Integer totalNumHit;
    private Boolean campaignSelected;
    private LineChartModel custAgeModel;
    private BarChartModel custGenderModel;
    private AnalAxis axisYSelection;
    private List<AnalAxis> axisYSelections;

    @PostConstruct
    public void init() {
        System.out.println("initialized analDisplayBean");
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);
        campaigns = ocb.getRegionCampaigns(userRegion);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("campaigns", campaigns);
        campaignSelected = false;
    }

    public void onCampaignChange() {
        if (selectedCampaign != null) {
            createCampaignModels();
            campaignSelected = true;
        } else {
            campaignSelected = false;
        }
    }

    public void createCampaignModels() {
        numCustPie = initNumCustPie(selectedCampaign);
        numCustPie.setLegendPosition("se");
        totalNumCust = anal.getCampaignNumCust(selectedCampaign, CustomerCampaignCat.ALL);
        numCustPie.setTitle("Number of targeted customers, Total: " + totalNumCust);
        numCustPie.setShowDataLabels(true);

        numPromoCodePie = initNumPromoCodePie(selectedCampaign);
        numPromoCodePie.setLegendPosition("se");
        totalNumPromoCode = anal.getCampaignNumPromoCode(selectedCampaign, CustomerCampaignCat.ALL);
        numPromoCodePie.setTitle("Number of Promo Code used, Total: " + totalNumPromoCode);
        numPromoCodePie.setShowDataLabels(true);

        numHitPie = initNumHitPie(selectedCampaign);
        totalNumHit = anal.getCampaignNumHit(selectedCampaign, CustomerCampaignCat.ALL);
        numHitPie.setTitle("Number of hits in campaign, Total: " + totalNumHit);
        numHitPie.setLegendPosition("se");
        numHitPie.setShowDataLabels(true);

    }

    private List<CustomerCampaignCat> getCustomerCampaignCat() {
        List<CustomerCampaignCat> ccc = new ArrayList<>();
        ccc.add(CustomerCampaignCat.NEW);
        ccc.add(CustomerCampaignCat.ACTIVE);
        ccc.add(CustomerCampaignCat.INACTIVE);
        return ccc;
    }

    public List<AnalAxis> getAxisYSelections() {
        List<AnalAxis> aa = new ArrayList<>();
        aa.add(AnalAxis.CAMPAIGNNUMCUST);
        aa.add(AnalAxis.CAMPAIGNPROMOCODE);
        aa.add(AnalAxis.CAMPAIGNNUMHIT);
        return aa;
    }

    private PieChartModel initNumPromoCodePie(Campaign camp) {
        PieChartModel model = new PieChartModel();
        // System.out.println("initialized initRfmAgeModel");
        for (CustomerCampaignCat ccc : getCustomerCampaignCat()) {
            Integer NumCust = anal.getCampaignNumPromoCode(camp, ccc);
            model.set(ccc.getLabel(), NumCust);
        }
        return model;
    }

    private PieChartModel initNumHitPie(Campaign camp) {
        PieChartModel model = new PieChartModel();
        // System.out.println("initialized initRfmAgeModel");
        for (CustomerCampaignCat ccc : getCustomerCampaignCat()) {
            Integer NumCust = anal.getCampaignNumHit(camp, ccc);
            model.set(ccc.getLabel(), NumCust);
        }
        return model;
    }

    private PieChartModel initNumCustPie(Campaign camp) {
        PieChartModel model = new PieChartModel();
        // System.out.println("initialized initRfmAgeModel");
        for (CustomerCampaignCat ccc : getCustomerCampaignCat()) {
            Integer NumCust = anal.getCampaignNumCust(camp, ccc);
            //System.out.println("Num cust for ccc "+ccc.getLabel()+" is: "+NumCust);
            model.set(ccc.getLabel(), NumCust);
        }
        return model;
    }

    public AnalCrmBean getAnal() {
        return anal;
    }

    public void setAnal(AnalCrmBean anal) {
        this.anal = anal;
    }

    public OpCrmBean getOcb() {
        return ocb;
    }

    public void setOcb(OpCrmBean ocb) {
        this.ocb = ocb;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Campaign getSelectedCampaign() {
        return selectedCampaign;
    }

    public void setSelectedCampaign(Campaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Facility getUserFacility() {
        return userFacility;
    }

    public void setUserFacility(Facility userFacility) {
        this.userFacility = userFacility;
    }

    public Region getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(Region userRegion) {
        this.userRegion = userRegion;
    }

    public PieChartModel getNumCustPie() {
        return numCustPie;
    }

    public void setNumCustPie(PieChartModel numCustPie) {
        this.numCustPie = numCustPie;
    }

    public PieChartModel getNumPromoCodePie() {
        return numPromoCodePie;
    }

    public void setNumPromoCodePie(PieChartModel numPromoCodePie) {
        this.numPromoCodePie = numPromoCodePie;
    }

    public PieChartModel getNumHitPie() {
        return numHitPie;
    }

    public void setNumHitPie(PieChartModel numHitPie) {
        this.numHitPie = numHitPie;
    }

    public Integer getTotalNumCust() {
        return totalNumCust;
    }

    public void setTotalNumCust(Integer totalNumCust) {
        this.totalNumCust = totalNumCust;
    }

    public Integer getTotalNumPromoCode() {
        return totalNumPromoCode;
    }

    public void setTotalNumPromoCode(Integer totalNumPromoCode) {
        this.totalNumPromoCode = totalNumPromoCode;
    }

    public Integer getTotalNumHit() {
        return totalNumHit;
    }

    public void setTotalNumHit(Integer totalNumHit) {
        this.totalNumHit = totalNumHit;
    }

    public Boolean getCampaignSelected() {
        return campaignSelected;
    }

    public void setCampaignSelected(Boolean campaignSelected) {
        this.campaignSelected = campaignSelected;
    }

}
