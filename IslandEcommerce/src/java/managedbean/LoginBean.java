/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import session.stateless.EComBean;

/**
 *
 * @author user
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    @EJB
    private EComBean ecb;
    private String email;
    private String password;
    private String forgotPasswordEmail;
    private Boolean isForgotPassword;
    private Boolean loggedIn;

//    @ManagedProperty(value = "#{navigationBean}")
//    private NavigationBean navigationBean;
    @PostConstruct
    public void init() {
        System.out.println("Initialized loginBean");
        isForgotPassword = false;
        loggedIn = false;
    }

    public void doLogin() {
        if (ecb.verifyUser(email, password)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Log-in success", ""));
            loggedIn = true;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("email", email);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("ecom_view_account.xhtml");
            } catch (Exception e) {

            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please check your log-in details", ""));
            //return navigationBean.toLogin();
        }
    }

    public void resetPassword() {
        if (ecb.resetPassword(forgotPasswordEmail)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Thank you, the new password has been sent to your email.", ""));
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please check your email again.", ""));
        }
    }

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    public void renderForgotPassword() {
        isForgotPassword = true;
    }

    public EComBean getEcb() {
        return ecb;
    }

    public void setEcb(EComBean ecb) {
        this.ecb = ecb;
    }

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

    public String getForgotPasswordEmail() {
        return forgotPasswordEmail;
    }

    public void setForgotPasswordEmail(String forgotPasswordEmail) {
        this.forgotPasswordEmail = forgotPasswordEmail;
    }

    public Boolean getIsForgotPassword() {
        return isForgotPassword;
    }

    public void setIsForgotPassword(Boolean isForgotPassword) {
        this.isForgotPassword = isForgotPassword;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

}
