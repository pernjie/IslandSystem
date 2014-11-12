/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import session.stateless.CIBeanLocal;
import entity.Log;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author nataliegoh
 */
@Named(value = "userLogManagedBean")
@Dependent
public class ViewUserLogBean {
    
    @EJB
    private CIBeanLocal fmsbean;
    private List<Log> userLog;
    private String loggedInEmail;

    public List<Log> getUserLog() {
        loggedInEmail = getLoggedInEmail();
        userLog = fmsbean.getUserLog(loggedInEmail);
        return userLog;
    }

    public void setUserLog(List<Log> userLog) {
        this.userLog = userLog;
    }
    /**
     * Creates a new instance of userLogManagedBean
     */
    public ViewUserLogBean() {
    }
    
    public String getLoggedInEmail() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
    }
    
    public void setLoggedInEmail(String loggedInEmail) {
        this.loggedInEmail = loggedInEmail;
    }
    
    
}
