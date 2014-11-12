package managedbean;

import entity.Staff;
import java.io.Serializable;
import javax.ejb.EJB;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import session.stateless.GlobalHqBean;

/**
 * Simple login bean.
 *
 * @author dingyi
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    // private static final long serialVersionUID = 7765876811740798583L;
    // Simple user database :)
    // private static final String[] users = {"anna:123", "nat:123"};
    private String email;
    private String password;
    private String user;
    private String name;

    private boolean loggedIn;

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;

    @EJB
    private GlobalHqBean globalHqBean;

    /**
     * Login operation.
     *
     * @return
     */

    public String doLogin() {
        int result;
        email = getEmail();
        password = getPassword();
        System.out.println("Password entered = " + password);
        password = globalHqBean.encryptPassword(email, password);
        result = globalHqBean.verifyUser(email, password);
        System.out.println("result @ loginBean: " + result);
        if (result == 1) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("email", email);
            loggedIn = true;
            return navigationBean.redirectToIndex();
        } else if (result == -1) {

            // Set login ERROR
            FacesMessage msg = new FacesMessage("Wrong credentials.", "ERROR MSG");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            // To to login page
            return navigationBean.toLogin();
        } else if (result == -2) {
            // Set login ERROR
            FacesMessage msg = new FacesMessage("Your account does not have authority to view Global HQ.", "ERROR MSG");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            // To to login page
            return navigationBean.toLogin();
        } else {
            // Set login ERROR
            FacesMessage msg = new FacesMessage("Wrong credentials.", "ERROR MSG");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            // To to login page
            return navigationBean.toLogin();
        }
    }

    /**
     * Logout operation.
     *
     * @return
     */
    public String doLogout() {
        // Set the paremeter indicating that user is logged in to false
        loggedIn = false;

        // Set logout message
        FacesMessage msg = new FacesMessage("Logout success!", "INFO MSG");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return navigationBean.toLogin();
    }

    public String getLoggedInName() {

        String loggedInEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        System.out.println("indexManagedBean, getLoggedName: loggedInEmail = " + loggedInEmail);
        Staff s = globalHqBean.getStaffByEmail(loggedInEmail);
        //boolean test = fmsbean.staffExists(loggedInEmail);
        //System.err.println("test access into FMSbean: test = " + test);
        name = s.getName();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("name", name);
        return name;
    }
        // ------------------------------
    // Getters & Setters 

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GlobalHqBean getGlobalHqBean() {
        return globalHqBean;
    }

    public void setGlobalHqBean(GlobalHqBean globalHqBean) {
        this.globalHqBean = globalHqBean;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
