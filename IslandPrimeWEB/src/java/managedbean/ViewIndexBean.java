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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import services.RoleClass;
import session.stateless.CIBeanLocal;



/**
 *
 * @author nataliegoh
 */
@Named(value = "indexManagedBean")
@SessionScoped
public class ViewIndexBean implements Serializable {
    
    private String loggedInName;
    private String loggedInEmail;
    @EJB
    private CIBeanLocal fmsbean;
    private Staff staff;
    private String role1;
    private String role2;
    private String role3;
    private List<String> rolesSet;
    private List<RoleClass> roles;
    private List<String> choices;
    private List<String> allFunctions;
    private boolean checkSA;
    private boolean checkGHQ;
    private boolean checkMRP;
    private boolean checkSCM;
    private Long loggedInFac;
    private String loggedInRole;
    private String facilityName;
    
    @ManagedProperty(value="#{loginManagedBean}")
    private DoLoginBean loginManagedBean;

    /**
     * Creates a new instance of indexManagedBean
     */
    public ViewIndexBean() {
    }

    public String getLoggedInEmail() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
    }

    public void setLoggedInEmail(String loggedInEmail) {
        this.loggedInEmail = loggedInEmail;
    }

    public DoLoginBean getLoginManagedBean() {
        return loginManagedBean;
    }

    public void setLoginManagedBean(DoLoginBean loginManagedBean) {
        this.loginManagedBean = loginManagedBean;
    }

    public String getRole1() {
        role1 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role");
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
    }

    public String getRole2() {
        role2 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role2");
        return role2;
    }

    public void setRole2(String role2) {
        this.role2 = role2;
    }

    public String getRole3() {
        role3 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role3");
        return role3;
    }

    public void setRole3(String role3) {
        this.role3 = role3;
    }
    
    public String getLoggedInName() {
        loggedInName = new String();
        loggedInEmail = new String();
        loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        System.out.println("indexManagedBean, getLoggedName: loggedInEmail = " + loggedInEmail);
        Staff s = fmsbean.getStaffDetails(loggedInEmail);
        //boolean test = fmsbean.staffExists(loggedInEmail);
        //System.err.println("test access into FMSbean: test = " + test);
        loggedInName = s.getName();
        
        return loggedInName;
    }
    
    public void setLoggedInName(String loggedInName) {
        this.loggedInName = loggedInName;
    }

    public Long getLoggedInFac() {
        loggedInFac = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fac");
        return loggedInFac;
    }

    public void setLoggedInFac(Long loggedInFac) {
        this.loggedInFac = loggedInFac;
    }

    public String getLoggedInRole() {
        loggedInRole = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role");
        return loggedInRole;
    }

    public void setLoggedInRole(String loggedInRole) {
        this.loggedInRole = loggedInRole;
    }

    public String getFacilityName() {
        facilityName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("facName");
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setCheckSCM(boolean checkSCM) {
        this.checkSCM = checkSCM;
    }
    
    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("../common/CI_Logged_Out_Page.xhtml");          
    }
    
    public String announcementDate() {
        String date = new String();
        date = fmsbean.announcementDate();
        return date;
    }
    
    public String announcementDetails() {
        String details = new String();
        details = fmsbean.announcementDetails();
        return details;
    }
    
    public void goToMessages() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("../chat/view_messages.xhtml");
    }
}
