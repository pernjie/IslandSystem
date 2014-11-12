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
import enumerator.BusinessArea;
import enumerator.CustomerActivity;
import enumerator.Gender;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LineChartModel;
import session.stateless.AnalCrmBean;
import session.stateless.OpCrmBean;

/**
 *
 * @author DY
 */
@ManagedBean(name = "analDisplayCustMetricsBean")
@ViewScoped
public class AnalDisplayCustMetricsBean implements Serializable {

    @EJB
    AnalCrmBean anal;
    @EJB
    OpCrmBean ocb;
    private String statusMessage;
    private Gender targetGender;
    private BusinessArea businessArea;
    private Campaign selectedCampaign;
    private List<Campaign> filteredCampaigns;
    private List<Campaign> campaigns;
    private List<AnalAxis> axisYSelections;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private LineChartModel custAgeModel;
    private BarChartModel custGenderModel;
    private AnalAxis axisYSelection;
    private AnalAxis lastAxisYSelection;
    private CustomerActivity custActSelection;

    @PostConstruct
    public void init() {
        System.out.println("initialized analDisplayBean");
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);
        campaigns = ocb.getAllCampaigns();
        custActSelection = CustomerActivity.ALL;
        axisYSelection = AnalAxis.RFM;
        createRfmModels();
    }

    public void onYAxisChange() {
        System.out.println("onYAxisChange, axisYSelection is " + axisYSelection);
        System.out.println("onYAxisChange, custActSelection is " + custActSelection);

        lastAxisYSelection = axisYSelection;
                System.out.println("onYAxisChange, lastAxisYSelection is " + lastAxisYSelection);

        if (axisYSelection.equals(AnalAxis.RFM)) {
            createRfmModels();

        } else {
            createNumCustModels();

        }
    }

    public void onCustActChange() {

        if (lastAxisYSelection != null) {
            axisYSelection = lastAxisYSelection;
        }
        System.out.println("onCustActChange, lastAxistYSelection is " + lastAxisYSelection);
        System.out.println("onCustActChange, axisYSelection is " + axisYSelection);
        if (axisYSelection.equals(AnalAxis.RFM)) {
            createRfmModels();
        } else {
            createNumCustModels();
        }
    }

    public List<AnalAxis> getAxisYSelections() {
        List<AnalAxis> aa = new ArrayList<>();
        aa.add(AnalAxis.RFM);
        aa.add(AnalAxis.NUMCUST);
        return aa;
    }

    public List<CustomerActivity> getCustActSelections() {
        List<CustomerActivity> ca = new ArrayList<>();
        ca.add(CustomerActivity.ALL);
        ca.add(CustomerActivity.ACTIVE);
        ca.add(CustomerActivity.INACTIVE);
        return ca;
    }

    public List<Gender> getGenders() {
        Gender[] genders = new Gender[3];
        genders[0] = Gender.ALL;
        genders[1] = Gender.MALE;
        genders[2] = Gender.FEMALE;
        return Arrays.asList(genders);
    }

    public List<BusinessArea> getBusinessAreas() {
        List<BusinessArea> ba = new ArrayList<>();
        ba.add(BusinessArea.FURNITURE);
        ba.add(BusinessArea.PRODUCT);
        ba.add(BusinessArea.KITCHEN);
        return ba;
    }

    private void createRfmModels() {
        custAgeModel = initCustAgeModel(AnalAxis.RFM, custActSelection);
        custAgeModel.setTitle("Average RFM against customer age");
        custAgeModel.setAnimate(true);
        custAgeModel.setLegendPosition("se");
        Axis localYAxis = custAgeModel.getAxis(AxisType.Y);
        localYAxis.setMin(0);
        localYAxis.setMax(15);
        Axis localXAxis = custAgeModel.getAxis(AxisType.X);
        localXAxis.setMin(0);
        localXAxis.setMax(100);

        custGenderModel = initCustGenderModel(AnalAxis.RFM, custActSelection);
        custGenderModel.setTitle("Average RFM against customer gender");
        custGenderModel.setAnimate(true);
        custGenderModel.setLegendPosition("se");
        localYAxis = custGenderModel.getAxis(AxisType.Y);
        localYAxis.setMin(0);
        localYAxis.setMax(15);
    }

    private void createNumCustModels() {

        custAgeModel = initCustAgeModel(AnalAxis.NUMCUST, custActSelection);
        custAgeModel.setTitle("Customer Age Distribution");
        custAgeModel.setAnimate(true);
        custAgeModel.setLegendPosition("se");
        Axis localYAxis = custAgeModel.getAxis(AxisType.Y);
        localYAxis.setMin(0);
        Axis localXAxis = custAgeModel.getAxis(AxisType.X);
        localXAxis.setMin(0);
        localXAxis.setMax(80);

        custGenderModel = initCustGenderModel(AnalAxis.NUMCUST, custActSelection);
        custGenderModel.setTitle("Customer Gender Distribution");
        custGenderModel.setAnimate(true);
        custGenderModel.setLegendPosition("se");
        localYAxis = custGenderModel.getAxis(AxisType.Y);
        localYAxis.setMin(0);
    }

    private LineChartModel initCustAgeModel(AnalAxis aa, CustomerActivity ca) {
        LineChartModel model = new LineChartModel();
        // System.out.println("initialized initRfmAgeModel");

        for (BusinessArea ba : getBusinessAreas()) {
            List<Double> rfmForBa = anal.getRegionBizAreaMetricScoresByAge(userRegion, ba, aa, ca);
            LineChartSeries series = new LineChartSeries();
            if (ba.equals(BusinessArea.FURNITURE)) {
                series.setLabel("Furniture"); 
            } else if (ba.equals(BusinessArea.KITCHEN)) {
                series.setLabel("Kitchen");
            } else if (ba.equals(BusinessArea.PRODUCT)) {
                series.setLabel("Product");
            }

            for (int i = 0; i < 101; i++) {
                if (!rfmForBa.get(i).equals(0)) {
                    series.set(i, rfmForBa.get(i));
                }
            }
            model.addSeries(series);
        }

        return model;
    }

    private BarChartModel initCustGenderModel(AnalAxis aa, CustomerActivity ca) {
        BarChartModel model = new BarChartModel();
        // System.out.println("initialized initRfmGenderModel");

        for (BusinessArea ba : getBusinessAreas()) {
            List<Double> rfmForBa = anal.getRegionBizAreaMetricScoresByGender(userRegion, ba, aa, ca);
            BarChartSeries series = new BarChartSeries();
            if (ba.equals(BusinessArea.FURNITURE)) {
                series.setLabel("Furniture");
            } else if (ba.equals(BusinessArea.KITCHEN)) {
                series.setLabel("Kitchen");
            } else if (ba.equals(BusinessArea.PRODUCT)) {
                series.setLabel("Product");
            }
            series.set("Male", rfmForBa.get(0));
            series.set("Female", rfmForBa.get(1));
            model.addSeries(series);
        }
        return model;
    }

    public void calculateRfm() {
        if (anal.calculateCustomerRfmScoreByRegion()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "RFM Scores for all Regions and Buiness Areas calculated.", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred.", ""));
        }
    }

    public void addCustomersToCampaign() {
        for (Campaign camp : campaigns) {
            try {
                System.out.println("===============================" + camp.getName() + "==========================");
                if (ocb.addCampaignCustomers(camp)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Added customers for campaign: " + camp.getName(), ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No customer added for campaign: " + camp.getName(), ""));
                }

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERRORTTM: No customer added for campaign: " + camp.getName(), ""));
            }
        }
    }

    /**
     * Creates a new instance of AnalDisplayMetricsBean
     */
    public AnalDisplayCustMetricsBean() {
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

    public Gender getTargetGender() {
        return targetGender;
    }

    public void setTargetGender(Gender targetGender) {
        this.targetGender = targetGender;
    }

    public BusinessArea getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(BusinessArea businessArea) {
        this.businessArea = businessArea;
    }

    public Campaign getSelectedCampaign() {
        return selectedCampaign;
    }

    public void setSelectedCampaign(Campaign selectedCampaign) {
        this.selectedCampaign = selectedCampaign;
    }

    public List<Campaign> getFilteredCampaigns() {
        return filteredCampaigns;
    }

    public void setFilteredCampaigns(List<Campaign> filteredCampaigns) {
        this.filteredCampaigns = filteredCampaigns;
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

    public LineChartModel getCustAgeModel() {
        return custAgeModel;
    }

    public void setCustAgeModel(LineChartModel custAgeModel) {
        this.custAgeModel = custAgeModel;
    }

    public BarChartModel getCustGenderModel() {
        return custGenderModel;
    }

    public void setCustGenderModel(BarChartModel custGenderModel) {
        this.custGenderModel = custGenderModel;
    }

    public AnalAxis getAxisYSelection() {
        return axisYSelection;
    }

    public void setAxisYSelection(AnalAxis axisYSelection) {
        this.axisYSelection = axisYSelection;
    }

    public CustomerActivity getCustActSelection() {
        return custActSelection;
    }

    public void setCustActSelection(CustomerActivity custActSelection) {
        this.custActSelection = custActSelection;
    }

}
