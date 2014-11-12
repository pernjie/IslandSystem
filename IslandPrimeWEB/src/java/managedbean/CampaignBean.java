/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Campaign;
import entity.Facility;
import entity.Region;
import entity.Staff;
import enumerator.BusinessArea;
import enumerator.Gender;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import session.stateless.CIBeanLocal;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author nataliegoh
 */
@ManagedBean(name = "campaignBean")
@ViewScoped
public class CampaignBean implements Serializable {

    private String name;
    private Date startDate;
    private Date endDate;
    private String promoCode;
    private String description;
    private Double percentDisc;
    private boolean targetActive;
    private boolean targetInactive;
    private boolean targetNew;
    private Integer lowerAge;
    private Integer upperAge;
    private Integer rfmThreshold;
    private Integer highestRfmThreshold;
    private Long newCampaignId;
    private String statusMessage;
    @EJB
    private OpCrmBean ocb;
    @EJB
    private CIBeanLocal cib;
    private Gender targetGender;
    private BusinessArea businessArea;
    private Campaign selectedCampaign;
    private List<Campaign> filteredCampaigns;
    private List<Campaign> campaigns;
    private Campaign newCampaign = new Campaign();
    private String userEmail;
    private Facility userFacility;
    private Region userRegion;

    @PostConstruct
    public void init() {
        System.out.println("initialized campaignBean");
        userEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        userFacility = ocb.getUserFacility(userEmail);
        userRegion = ocb.getUserRegion(userFacility);
        campaigns = ocb.getAllCampaigns();
        //facService = new FacilityService();
    }

//    public Gender getGender() {
//        return gender;
//    }
    public List<Gender> getGenders() {
        Gender[] genders = new Gender[3];
        genders[0] = Gender.ALL;
        genders[1] = Gender.MALE;
        genders[2] = Gender.FEMALE;
        return Arrays.asList(genders);

    }

    public List<BusinessArea> getBusinessAreas() {
        List<BusinessArea> ba = new ArrayList<BusinessArea>();
        ba.add(BusinessArea.FURNITURE);
        ba.add(BusinessArea.PRODUCT);
        ba.add(BusinessArea.KITCHEN);
        return ba;
    }

    public void saveNewCampaign(ActionEvent event) {
        System.out.println("Campaign Name = " + name);
        System.out.println("Campaign Start Date = " + startDate);
        System.out.println("Campaign End Date = " + endDate);
        System.out.println("Campaign Promo Code = " + promoCode);
        System.out.println("Campaign Promo Desc = " + description);
        System.out.println("Campaign Percent Disc = " + percentDisc);
        System.out.println("Campaign Target Gender= " + targetGender);
        System.out.println("Campaign Target Active = " + targetActive);
        System.out.println("Campaign Target Inactive = " + targetInactive);
        System.out.println("Campaign Target New = " + targetNew);
        System.out.println("Campaign Business Area = " + businessArea);
        System.out.println("Campaign Rfm Threshold = " + rfmThreshold);
        System.out.println("Campaign highestRfmThreshold = " + highestRfmThreshold);

        try {
            Calendar cal = Calendar.getInstance();
            Date currDate = cal.getTime();
            if (startDate.after(endDate)) {
                statusMessage = "Please ensure that the start date is before the end date.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Campaign Result: "
                        + statusMessage, ""));
            } else if (startDate.before(currDate)) {
                statusMessage = "Please ensure that the start date is not before today's date.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Campaign Result: "
                        + statusMessage, ""));
            } else {
                newCampaign.setName(name);
                newCampaign.setStartDate(startDate);
                newCampaign.setEndDate(endDate);
                newCampaign.setPromoCode(promoCode);
                newCampaign.setDescription(description);
                newCampaign.setPercentDisc(percentDisc);
                newCampaign.setUpperAge(upperAge);
                newCampaign.setLowerAge(lowerAge);
                newCampaign.setTargetGender(targetGender);
                newCampaign.setTargetActive(targetActive);
                newCampaign.setTargetInactive(targetInactive);
                newCampaign.setTargetNew(targetNew);
                newCampaign.setBusinessArea(businessArea);
                newCampaign.setRfmThreshold(rfmThreshold);
                newCampaign.setHighestRfmThreshold(highestRfmThreshold);
                newCampaign.setRegion(userRegion);
                newCampaignId = ocb.addNewCampaign(newCampaign);
                statusMessage = "New Campaign Created Successfully.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Campaign Result: "
                        + statusMessage + " (New Campaign ID is " + newCampaignId + ")", ""));
                Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
                cib.addLog(staff, "Added New Campaign: " + newCampaignId);
            }
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newCampaignId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Campaign Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newCampaignId = -1L;
            statusMessage = "Creating new campaign failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Campaign Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public void deleteCampaign() {
        try {
            Long CamId = selectedCampaign.getId();
            ocb.removeCampaign(selectedCampaign);
            campaigns = ocb.getAllCampaigns();
            FacesMessage msg = new FacesMessage("Campaign Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
            cib.addLog(staff, "Deleted Campaign: " + CamId);
        } catch (ReferenceConstraintException ex) {
            campaigns = ocb.getAllCampaigns();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPercentDisc() {
        return percentDisc;
    }

    public void setPercentDisc(Double percentDisc) {
        this.percentDisc = percentDisc;
    }

    public boolean isTargetActive() {
        return targetActive;
    }

    public void setTargetActive(boolean targetActive) {
        this.targetActive = targetActive;
    }

    public boolean isTargetInactive() {
        return targetInactive;
    }

    public void setTargetInactive(boolean targetInactive) {
        this.targetInactive = targetInactive;
    }

    public boolean isTargetNew() {
        return targetNew;
    }

    public void setTargetNew(boolean targetNew) {
        this.targetNew = targetNew;
    }

    public Integer getLowerAge() {
        return lowerAge;
    }

    public void setLowerAge(Integer lowerAge) {
        this.lowerAge = lowerAge;
    }

    public Integer getUpperAge() {
        return upperAge;
    }

    public void setUpperAge(Integer upperAge) {
        this.upperAge = upperAge;
    }

    public Integer getRfmThreshold() {
        return rfmThreshold;
    }

    public void setRfmThreshold(Integer rfmThreshold) {
        this.rfmThreshold = rfmThreshold;
    }

    public Integer getHighestRfmThreshold() {
        return highestRfmThreshold;
    }

    public void setHighestRfmThreshold(Integer highestRfmThreshold) {
        this.highestRfmThreshold = highestRfmThreshold;
    }

    public Long getNewCampaignId() {
        return newCampaignId;
    }

    public void setNewCampaignId(Long newCampaignId) {
        this.newCampaignId = newCampaignId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public OpCrmBean getOcb() {
        return ocb;
    }

    public void setOcb(OpCrmBean ocb) {
        this.ocb = ocb;
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

    public Campaign getNewCampaign() {
        return newCampaign;
    }

    public void setNewCampaign(Campaign newCampaign) {
        this.newCampaign = newCampaign;
    }

}
