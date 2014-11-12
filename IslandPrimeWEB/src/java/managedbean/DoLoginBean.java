/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import entity.Facility;
import entity.Staff;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import session.stateless.CIBeanLocal;

/**
 *
 * @author nataliegoh
 */
@Named(value = "loginManagedBean")
@SessionScoped
public class DoLoginBean implements Serializable {

    @EJB
    private CIBeanLocal fmsbean;
    private EntityManager em;
    
    /**
     * Creates a new instance of loginManagedBean
     */
    public DoLoginBean() {
    }
    
    private String email;
    private String password;
    private Long fac;
    private String role;
    private String role2;
    private String role3;
    private String facilityName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getFac() {
        return fac;
    }

    public void setFac(Long fac) {
        this.fac = fac;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole2() {
        return role2;
    }

    public void setRole2(String role2) {
        this.role2 = role2;
    }

    public String getRole3() {
        return role3;
    }

    public void setRole3(String role3) {
        this.role3 = role3;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public void login (ActionEvent event) {
        int result;
        email = getEmail();
        password = getPassword();
        System.out.println("Password entered = " + password);
        password = fmsbean.encryptPassword(email, password);
        
        try {
            result = fmsbean.verifyUser(email, password);
            if (result == 1) {
                
                //ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
                //Map<String, Object> sessionMap = externalContext.getSessionMap();
                //sessionMap.put("email", email);
                
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("email", email);
                Staff s = fmsbean.getStaffDetails(email);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("staff", s);
                Facility facility = s.getFac();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facility", facility);
                fac = s.getFac().getId();
                System.out.println("loginManagedBean fac = " + fac);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fac", fac);
                facilityName = s.getFac().getName();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("facName", facilityName);
                role = s.getRole1();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("role", role);
                role2 = s.getRole2();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("role2", role2);
                role3 = s.getRole3();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("role3", role3);
                
                //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
                //((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
                
                FacesContext.getCurrentInstance().getExternalContext().redirect("../common/CI_Index_Page.xhtml");
            }  
            
            else {
                 //FacesContext.getCurrentInstance().getExternalContext().redirect("/IslandWEB/login.xhtml");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong email or Password!", "Wrong email or Password!"));
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Wrong email or Password!"));
        }
    }
}
