/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import classes.NameQuantityExpenditure;
import entity.Facility;
import entity.Region;
import enumerator.AnalAxis;
import enumerator.BusinessArea;
import enumerator.CustomerActivity;
import enumerator.Gender;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.PieChartModel;
import session.stateless.AnalCrmBean;
import session.stateless.OpCrmBean;

/**
 *
 * @author user
 */
@ManagedBean(name = "analDisplaySalesMetricsBean")
@ViewScoped
public class AnalDisplaySalesMetricsBean implements Serializable {

    @EJB
    AnalCrmBean anal;
    @EJB
    OpCrmBean ocb;
    private String statusMessage;
    private Gender targetGender;
    private BusinessArea businessArea;
    private List<AnalAxis> axisYSelections;
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;
    private PieChartModel prodCatModel;
    private PieChartModel matCatModel;
    private PieChartModel kitCatModel;
    private BarChartModel custGenderModel;
    private AnalAxis prodCatPieAxis;
    private AnalAxis matCatPieAxis;
    private AnalAxis kitCatPieAxis;
    private AnalAxis lastAxisYSelection;
    private CustomerActivity custActSelection;
    private Integer numMonthsProd;
    private Integer numMonthsMat;
    private Integer numMonthsKit;
    private Integer numMonthsSelection;

    @PostConstruct
    public void init() {
        System.out.println("initialized analDisplayBean");
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);
        numMonthsProd = 6;
        numMonthsMat = 6;
        numMonthsKit = 6;
        prodCatPieAxis = AnalAxis.QUANTITY;
        matCatPieAxis = AnalAxis.QUANTITY;
        kitCatPieAxis = AnalAxis.QUANTITY;
        createPieModels();
    }

    public void onNumMonthsChange() {
        createPieModels();
    }

    public void onPieDisplayAxisChange() {
        createPieModels();
    }

    public List<Integer> getNumMonthsSelection() {
        List<Integer> nm = new ArrayList();
        for (int i = 1; i <= 12; i++) {
            nm.add(i);
        }
        return nm;
    }

    public List<AnalAxis> getPieDisplayAxises() {
        List<AnalAxis> aa = new ArrayList();
        aa.add(AnalAxis.QUANTITY);
        aa.add(AnalAxis.EXPENDITURE);
        return aa;
    }

    public void createPieModels() {
        prodCatModel = initCategoryModel(BusinessArea.PRODUCT, numMonthsProd, prodCatPieAxis);
        matCatModel = initCategoryModel(BusinessArea.FURNITURE, numMonthsMat, matCatPieAxis);
        kitCatModel = initCategoryModel(BusinessArea.KITCHEN, numMonthsKit, kitCatPieAxis);

        prodCatModel.setLegendPosition("se");
        prodCatModel.setTitle(prodCatPieAxis.getLabel() + " for Products");
        prodCatModel.setShowDataLabels(true);
        
        matCatModel.setLegendPosition("se");
        matCatModel.setTitle(matCatPieAxis.getLabel() + " for Furniture");
        matCatModel.setShowDataLabels(true);

        kitCatModel.setLegendPosition("se");
        kitCatModel.setTitle(kitCatPieAxis.getLabel() + " for Kitchen");
        kitCatModel.setShowDataLabels(true);

    }

    private PieChartModel initCategoryModel(BusinessArea ba, Integer numMonths, AnalAxis aa) {
        PieChartModel model = new PieChartModel();
       // System.out.println("numMonth is: "+numMonths);
        List<NameQuantityExpenditure> nqeList = anal.getBusinessAreaRegionCategoryQuantityExpenditure(ba, userRegion, numMonths);
        for (NameQuantityExpenditure nqe : nqeList) {
            if (aa.equals(AnalAxis.QUANTITY)) {
                model.set(nqe.getName(), nqe.getQuantity());
            } else if (aa.equals(AnalAxis.EXPENDITURE)) {
                model.set(nqe.getName(), nqe.getExpenditure());
            }
        }
        return model;
    }

    public AnalDisplaySalesMetricsBean() {
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

    public List<AnalAxis> getAxisYSelections() {
        return axisYSelections;
    }

    public void setAxisYSelections(List<AnalAxis> axisYSelections) {
        this.axisYSelections = axisYSelections;
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

    public PieChartModel getProdCatModel() {
        return prodCatModel;
    }

    public void setProdCatModel(PieChartModel prodCatModel) {
        this.prodCatModel = prodCatModel;
    }

    public PieChartModel getMatCatModel() {
        return matCatModel;
    }

    public void setMatCatModel(PieChartModel matCatModel) {
        this.matCatModel = matCatModel;
    }

    public PieChartModel getKitCatModel() {
        return kitCatModel;
    }

    public void setKitCatModel(PieChartModel kitCatModel) {
        this.kitCatModel = kitCatModel;
    }

    public BarChartModel getCustGenderModel() {
        return custGenderModel;
    }

    public void setCustGenderModel(BarChartModel custGenderModel) {
        this.custGenderModel = custGenderModel;
    }

    public AnalAxis getProdCatPieAxis() {
        return prodCatPieAxis;
    }

    public void setProdCatPieAxis(AnalAxis prodCatPieAxis) {
        this.prodCatPieAxis = prodCatPieAxis;
    }

    public AnalAxis getMatCatPieAxis() {
        return matCatPieAxis;
    }

    public void setMatCatPieAxis(AnalAxis matCatPieAxis) {
        this.matCatPieAxis = matCatPieAxis;
    }

    public AnalAxis getKitCatPieAxis() {
        return kitCatPieAxis;
    }

    public void setKitCatPieAxis(AnalAxis kitCatPieAxis) {
        this.kitCatPieAxis = kitCatPieAxis;
    }

    public AnalAxis getLastAxisYSelection() {
        return lastAxisYSelection;
    }

    public void setLastAxisYSelection(AnalAxis lastAxisYSelection) {
        this.lastAxisYSelection = lastAxisYSelection;
    }

    public CustomerActivity getCustActSelection() {
        return custActSelection;
    }

    public void setCustActSelection(CustomerActivity custActSelection) {
        this.custActSelection = custActSelection;
    }

    public Integer getNumMonthsProd() {
        return numMonthsProd;
    }

    public void setNumMonthsProd(Integer numMonthsProd) {
        this.numMonthsProd = numMonthsProd;
    }

    public Integer getNumMonthsMat() {
        return numMonthsMat;
    }

    public void setNumMonthsMat(Integer numMonthsMat) {
        this.numMonthsMat = numMonthsMat;
    }

    public Integer getNumMonthsKit() {
        return numMonthsKit;
    }

    public void setNumMonthsKit(Integer numMonthsKit) {
        this.numMonthsKit = numMonthsKit;
    }

}
