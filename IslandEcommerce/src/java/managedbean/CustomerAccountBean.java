/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Customer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.context.RequestContext;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "customerAccountBean")
@ViewScoped
public class CustomerAccountBean implements Serializable {

    @EJB
    private OpCrmBean ecb;
    private Integer points;
    private Integer pointsToPlus;
    private Boolean plus;
    private Boolean notPlus;
    private Boolean cakeRedeemable;
    private String email;
    private Customer customer;
    private String newPassword;
    private String currPassword;

    @PostConstruct

    public void init() {
        System.out.println("Initialized loggedInCustomerBean");
        email = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        populateCustomerInfo();
    }

    public void changePassword(ActionEvent event) throws IOException, DetailsConflictException {
        try {
            ecb.changePassword(email, currPassword, newPassword);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Password Changed!"));
        } catch (DetailsConflictException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong Current Password!", "Wrong Current Password"));
        }
    }

    public void unsubscribe() {
        if (ecb.unsubscribe(customer)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "You have been unsubscribed from the mailing list."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Oops.","We were unable to process that request, please try again in a moment."));
        }
        RequestContext.getCurrentInstance().update("formMain:unsubPanel");
    }

    private void populateCustomerInfo() {
        customer = ecb.getCustomerDetails(email);
        System.out.println("customer email: " + customer.getEmail());
        plus = customer.getPlus();
        notPlus = !customer.getPlus();
        Date birthdayDate = customer.getDob();
        Calendar birthdayCal = Calendar.getInstance();
        birthdayCal.setTime(birthdayDate);
        Integer birthdayMonth = birthdayCal.get(Calendar.MONTH);
        Calendar cal = Calendar.getInstance();
        Integer currMonth = cal.get(Calendar.MONTH);
        cakeRedeemable = (!customer.getRedeemedCake() && birthdayMonth.equals(currMonth) && plus);
        points = customer.getPoints();
        System.out.println("points: " + points);
        pointsToPlus = 150 - points;
    }

    public OpCrmBean getEcb() {
        return ecb;
    }

    public void setEcb(OpCrmBean ecb) {
        this.ecb = ecb;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPointsToPlus() {
        return pointsToPlus;
    }

    public void setPointsToPlus(Integer pointsToPlus) {
        this.pointsToPlus = pointsToPlus;
    }

    public Boolean getPlus() {
        return plus;
    }

    public void setPlus(Boolean plus) {
        this.plus = plus;
    }

    public Boolean getCakeRedeemable() {
        return cakeRedeemable;
    }

    public void setCakeRedeemable(Boolean cakeRedeemable) {
        this.cakeRedeemable = cakeRedeemable;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Boolean getNotPlus() {
        return notPlus;
    }

    public void setNotPlus(Boolean notPlus) {
        this.notPlus = notPlus;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrPassword() {
        return currPassword;
    }

    public void setCurrPassword(String currPassword) {
        this.currPassword = currPassword;
    }

}
