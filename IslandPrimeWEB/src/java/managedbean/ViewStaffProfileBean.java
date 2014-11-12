/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import session.stateless.CIBeanLocal;
import entity.Staff;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import util.exception.DetailsConflictException;

/**
 *
 * @author nataliegoh
 */
@Named(value = "profileManagedBean")
@RequestScoped
public class ViewStaffProfileBean implements Serializable {

    @EJB
    private CIBeanLocal fmsbean;
    private Staff user;
    private String newpassword;
    private String name;
    private String contact;
    @ManagedProperty(value="#{loginManagedBean}")
    private DoLoginBean loginManagedBean;
    private String loggedInEmail;
    private String role1;
    private String role2;
    private String role3;
    private boolean hasRole2;
    private boolean hasRole3;
    private String newcontact;
    private String currentpassword;
    
    
    /**
     * Creates a new instance of profileManagedBean
     */
    
    public ViewStaffProfileBean() {
    }
    
    public Staff getUser(String email) {
        user = new Staff();
        user = fmsbean.getStaffDetails(loggedInEmail);
        return user;
    }
    
    public String getLoggedInEmail() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
    }
    
    public void setLoggedInEmail(String loggedInEmail) {
        this.loggedInEmail = loggedInEmail;
    }

    public void setUser(Staff user) {
        this.user = user;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getName() {
        loggedInEmail = getLoggedInEmail();
        user = fmsbean.getStaffDetails(loggedInEmail);
        name = user.getName();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        contact = user.getContact();
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRole1() {
        role1 = user.getRole1();
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
    }

    public String getRole2() {
        role2 = user.getRole2();
        return role2;
    }

    public void setRole2(String role2) {
        this.role2 = role2;
    }

    public String getRole3() {
        role3 = user.getRole3();
        return role3;
    }

    public void setRole3(String role3) {
        this.role3 = role3;
    }

    public boolean isHasRole2() {
        loggedInEmail = getLoggedInEmail();
        user = fmsbean.getStaffDetails(loggedInEmail);
        return (user.getRole2() != null);
    }

    public void setHasRole2(boolean hasRole2) {
        hasRole2 = user.getRole2().isEmpty();
        this.hasRole2 = hasRole2;
    }

    public boolean isHasRole3() {
        loggedInEmail = getLoggedInEmail();
        user = fmsbean.getStaffDetails(loggedInEmail);
        return (user.getRole3() != null);
    }

    public void setHasRole3(boolean hasRole3) {
        hasRole3 = user.getRole3().isEmpty();
        this.hasRole3 = hasRole3;
    }

    public String getNewcontact() {
        return newcontact;
    }

    public void setNewcontact(String newcontact) {
        this.newcontact = newcontact;
    }

    public String getCurrentpassword() {
        return currentpassword;
    }

    public void setCurrentpassword(String currentpassword) {
        this.currentpassword = currentpassword;
    }

    
    public void changePassword(ActionEvent event) throws IOException, DetailsConflictException {
        try {
            loggedInEmail = getLoggedInEmail();
            System.out.println("profileManagedBean- changePassword: email  = " + loggedInEmail + ", newpassword = " + newpassword);
            fmsbean.changePassword(loggedInEmail, currentpassword, newpassword);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Password Changed!"));
        }
        catch (DetailsConflictException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong Current Password!", "Wrong Current Password"));
        }
    }
    
    public void changeContact(ActionEvent event) throws IOException {
        loggedInEmail = getLoggedInEmail();
        System.out.println("profileManagedBean- changeEmail: email  = " + loggedInEmail + ", newcontact = " + newcontact);
        fmsbean.changeContact(loggedInEmail, newcontact);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Contact Changed!"));
    }
    
}
