/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Country;
import entity.Customer;
import entity.Region;
import enumerator.Gender;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import session.stateless.OpCrmBean;
import util.exception.DetailsConflictException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "customerBean")
@ViewScoped
public class CustomerBean implements Serializable {

    @EJB
    private OpCrmBean ecb;
    private String unsubscribeEmail;
    private Customer customer;
    private Long custId;
    List<Customer> customers;
    private String address;
    private Date DOB;
    private String password;
    private String confirmPassword;
    private String email;
    private String mobile;
    private String name;
    private Gender gender;
    private Long newCustomerId;
    private Customer newCustomer;
    private String statusMessage;
    private Country country;
    private String city;
    private List<Country> countries;
    private Long regionId;
    private Region region;

    @PostConstruct

    public void init() {
        System.out.println("Initialized customerBean");
        countries = ecb.getAllCountries();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("countries", countries);
        regionId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("regionId");
        region = ecb.getRegion(regionId);
    }

    public List<Gender> getGenders() {
        List<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        genders.add(Gender.FEMALE);
        return genders;
    }

    public void saveNewCustomer(ActionEvent event) {
        try {
            System.out.println("saveNewCustomer");
            newCustomer = new Customer();
            newCustomer.setName(name);
            newCustomer.setEmail(email);
            newCustomer.setAddress(address);
            newCustomer.setGender(gender);
            newCustomer.setPassword(password);
            newCustomer.setMobile(mobile);
            newCustomer.setDob(DOB);
            newCustomer.setCity(city);
            newCustomer.setCountry(country);
            newCustomer.setUnsubscribed(false);
            newCustomer.setPlus(false);
            newCustomer.setRedeemedCake(false);
            newCustomer.setRegion(region);
            newCustomer.setPoints(0);
            newCustomerId = ecb.addNewCustomer(newCustomer);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account created successfully", ""));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("email",email);

        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newCustomerId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please try again: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newCustomerId = -1L;
            statusMessage = "Account creation failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please try again: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    public OpCrmBean getEcb() {
        return ecb;
    }

    public void setEcb(OpCrmBean ecb) {
        this.ecb = ecb;
    }

    public String getUnsubscribeEmail() {
        return unsubscribeEmail;
    }

    public void setUnsubscribeEmail(String unsubscribeEmail) {
        this.unsubscribeEmail = unsubscribeEmail;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getNewCustomerId() {
        return newCustomerId;
    }

    public void setNewCustomerId(Long newCustomerId) {
        this.newCustomerId = newCustomerId;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

//    public void save(ActionEvent event) throws IOException {
//        unsubscribeEmail = getUnsubscribeEmail();
//        custId = getCustId();
//        ecb.unsubscribe(custId);
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You've successfully unregistered!"));
//    }

}
